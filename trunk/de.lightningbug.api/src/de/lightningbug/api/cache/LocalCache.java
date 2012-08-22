package de.lightningbug.api.cache;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.lightningbug.api.BugzillaClient;
import de.lightningbug.api.domain.Product;
import de.lightningbug.api.service.ProductService;

/**
 * The local cache is used to store bugzilla objects, like {@link Product}s,
 * {@link Component}s, {@link Version}s and {@link Milestone}s. The necessary
 * initial query for those objects {@link ProductService#getProducts()} could
 * take some time. The store makes it possible share these objects between
 * bugzilla sessions without queriing them each time.
 * <p>
 * The objects are stored in an XML file (<home>/.lightningbug/localCache.xml)
 * in the users home directory. If this file is not available the cache will try
 * to load the objects from a file with the same name in the lightningbug JAR.
 * Such a file is usefull, if you want to deliver a default local cache.
 * </p>
 * 
 * @author Sebastian Kirchner (AutoVision GmbH)
 * 
 */
@XmlRootElement
public class LocalCache {

	/**
	 * TODO Complete documentation!
	 * 
	 * @author Sebastian Kirchner
	 * 
	 */
	@XmlRootElement
	protected static class Cache {

		private Set<Connection> connections = null;

		/**
		 * @return the connections
		 */
		@XmlElement(name = "connection")
		public Set<Connection> getConnections() {
			if (this.connections == null) {
				this.connections = new TreeSet<Connection>();
			}
			return this.connections;
		}

		/**
		 * @param connections
		 *            the connections to set
		 */
		public void setConnections(Set<Connection> connections) {
			this.connections = connections;
		}

	}

	/**
	 * TODO Complete documentation!
	 * 
	 * @author Sebastian Kirchner
	 * 
	 */
	protected static class Connection implements Comparable<Connection> {

		private List<Product> products = null;

		private String url = null;

		@Override
		public int compareTo(Connection o) {
			if (this.url == null) {
				if (o.getUrl() == null) {
					return 0;
				}
				return -1;
			}
			return this.getUrl().compareTo(o.getUrl());
		}

		/**
		 * @return the products
		 */
		@XmlElement(name = "product")
		public List<Product> getProducts() {
			if (this.products == null) {
				this.products = new ArrayList<Product>();
			}
			return this.products;
		}

		/**
		 * @return the url
		 */
		@XmlAttribute
		public String getUrl() {
			return this.url;
		}

		/**
		 * @param products
		 *            the products to set
		 */
		public void setProducts(List<Product> products) {
			this.products = products;
		}

		/**
		 * @param url
		 *            the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

	}

	private static final String CACHE_FILE_NAME = "localCache.xml"; //$NON-NLS-1$
	private static final String USER_SETTINGS_FOLDER_NAME = ".lightningbug"; //$NON-NLS-1$

	private static final File USER_HOME = new File(System.getProperty("user.home")); //$NON-NLS-1$
	private static final File USER_SETTINGS_FOLDER = new File(USER_HOME, USER_SETTINGS_FOLDER_NAME);
	private static final File USER_CACHE_FILE = new File(USER_SETTINGS_FOLDER, CACHE_FILE_NAME);
	private static final File DEFAULT_CACHE_FILE = new File(CACHE_FILE_NAME);
	private static final Log LOG = LogFactory.getLog(LocalCache.class);

	/**
	 * @param client
	 * @return
	 */
	public static List<Product> getProducts(final BugzillaClient client) {
		load();
		if (cache == null) {
			return null;
		}
		final String urlString = client.getURL().toString();
		for (Connection connection : cache.getConnections()) {
			if (connection.getUrl().equals(urlString)) {
				return new ArrayList<Product>(connection.getProducts());
			}
		}
		return null;
	}

	public static void setProducts(final BugzillaClient client, final List<Product> products) {
		// local cache might not have been loaded before (re)try
		if (cache == null) {
			load();
		}
		// no previous cache - create a new one
		if (cache == null) {
			cache = new Cache();
		}
		// find the right connection in the cache
		Connection connection = null;
		for (final Connection c : cache.getConnections()) {
			if (c.getUrl().equals(client.getURL().toString())) {
				connection = c;
			}
		}
		// create a new connection if there was no match in the cache
		if (connection == null) {
			connection = new Connection();
			connection.setUrl(client.getURL().toString());
			cache.getConnections().add(connection);
		}
		connection.setProducts(products);
		save();
	}

	private static void load() {
		// find the cache file in the user home folder
		File cacheFile = null;
		if (USER_CACHE_FILE.isFile()) {
			LOG.info("Local cache file in the user home directory found "); //$NON-NLS-1$
			cacheFile = USER_CACHE_FILE;
		} else {
			LOG.info("Local cache file could not be found in the users home dir. " //$NON-NLS-1$
					+ "Looking for a fallback cache file"); //$NON-NLS-1$
			if (DEFAULT_CACHE_FILE.isFile()) {
				LOG.info("fallback cache file found"); //$NON-NLS-1$
				cacheFile = DEFAULT_CACHE_FILE;
			}
		}
		if (cacheFile == null) {
			LOG.info("Local cache file could not be found. Local cache is not available"); //$NON-NLS-1$
			return;
		}
		try {
			final Unmarshaller unmarshaller = getContext().createUnmarshaller();
			cache = (Cache) unmarshaller.unmarshal(cacheFile);
		} catch (JAXBException e) {
			LOG.warn("Error while creating the JAXBContext for the local cache", e); //$NON-NLS-1$
		}
	}

	private static JAXBContext context = null;

	private static JAXBContext getContext() throws JAXBException {
		if (context == null) {
			context = JAXBContext.newInstance(Cache.class, Connection.class, Product.class);
		}
		return context;
	}

	/**
	 * 
	 */
	private static void save() {
		if (!USER_SETTINGS_FOLDER.isDirectory() && !USER_SETTINGS_FOLDER.mkdir()) {
			LOG.error("The following folder could not be created. The local cache will not be saved: " //$NON-NLS-1$
					+ USER_SETTINGS_FOLDER.getAbsolutePath());
			return;
		}
		try {
			final Marshaller marshaller = getContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(cache, USER_CACHE_FILE);
		} catch (JAXBException e) {
			LOG.warn("Error while creating the JAXBContext for the local cache", e); //$NON-NLS-1$
		}
	}

	private static Cache cache = null;

	private LocalCache() {
		// for static use only
	}

}
