/**
 * 
 */
package de.lightningbug.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.domain.Component;
import de.lightningbug.api.domain.Milestone;
import de.lightningbug.api.domain.Product;
import de.lightningbug.api.domain.Version;
import de.lightningbug.api.util.HashArray;
import de.lightningbug.api.util.HashArray.NoHashArrayException;

/**
 * encapsulates methods to handle Bugzilla {@link Product} objects.
 * 
 * @author Sebastian Kirchner
 * 
 */
class ProductFactory {

	protected final static Log LOG = LogFactory.getLog(ProductFactory.class);

	protected BugzillaClient client = null;

	/**
	 * Cache for the id's of products the user can search or enter bugs against
	 */
	private Integer[] accessibleProductIds = null;

	protected List<Product> products = null;

	private boolean useLocalCache = true;

	/**
	 * @param client
	 *            the client used by the factory to query information from the
	 *            bugzilla instance
	 */
	public ProductFactory(BugzillaClient client) {
		this(client, true);
	}

	/**
	 * @param client
	 *            the client used by the factory to query information from the
	 *            bugzilla instance
	 * @param useLocalCache
	 */
	public ProductFactory(BugzillaClient client, final boolean useLocalCache) {
		super();
		if (client == null) {
			throw new IllegalArgumentException("Paramter <client> must not be mull"); //$NON-NLS-1$
		}
		this.client = client;
		this.useLocalCache = useLocalCache;
	}

	public List<Product> getProducts() {

		if (this.products != null) {
			return this.products;
		}

		// the availibility of the cache will only be checked and maintained, if
		// the factory is configured to use the cache
		boolean cacheAvailable = true;

		if (this.useLocalCache) {
			final List<Product> productsFromCache = LocalCache.getProducts(this.client);
			cacheAvailable = productsFromCache != null;
			if (cacheAvailable) {
				// update the cache in the backgroud...
				new Thread() {
					@Override
					public void run() {
						LOG.info("updating the product cache in the backgroud"); //$NON-NLS-1$
						ProductFactory.this.products = loadProducts();
						LocalCache.setProducts(ProductFactory.this.client,
								ProductFactory.this.products);
					}
				}.start();
				// ... and return the results from the local cache
				this.products = productsFromCache;
				return this.products;
			}
		}

		this.products = loadProducts();
		if (!cacheAvailable) {
			// the make it available the next time
			LOG.info("updating the product cache"); //$NON-NLS-1$
			LocalCache.setProducts(this.client, this.products);
		}
		return this.products;
	}

	protected List<Product> loadProducts() {
		final List<Product> prods = new LinkedList<Product>();
		try {
			// Param = a hash containing one item, ids, that is an array of
			// product ids.
			final Map<?, ?> result = (Map<?, ?>) this.client.exec("Product.get", "ids",
					this.getAccessibleProductIds());
			// result = a hash containing one item, products, that is an array
			// of hashes.
			final HashArray productHashes = new HashArray(result.values().iterator().next());
			for (final Map<?, ?> productHash : productHashes) {
				// Each hash describes a product, and has the following items:
				// id,name, description, and internals. The id item is the id of
				// the product. The name item is the name of the product. The
				// description is the description of the product. Finally, the
				// internals is an internal representation of the product
				final Integer id = (Integer) productHash.get(Product.ID);
				final String name = (String) productHash.get(Product.NAME);
				final Product product = new Product(id, name);
				product.setComponents(getComponentsFor(product));
				product.setVersions(getVersionsFor(product));
				product.setMilestones(getMilestonesFor(product));
				prods.add(product);
			}

		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHashArrayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return prods;
	}

	/**
	 * Returns an array of the ids of the products the user can search or enter
	 * bugs against.
	 * 
	 * @return an array of the ids of products
	 * @throws XmlRpcException
	 * 
	 * @see ProductFactory#getProducts()
	 */
	private Integer[] getAccessibleProductIds() throws XmlRpcException {
		if (accessibleProductIds == null) {

			final Map<?, ?> result = (Map<?, ?>) this.client
					.exec("Product.get_accessible_products");
			final Object[] ints = (Object[]) result.values().iterator().next();
			accessibleProductIds = new Integer[ints.length];
			for (int i = 0; i < ints.length; i++) {
				accessibleProductIds[i] = (Integer) ints[i];
			}
		}
		return accessibleProductIds;
	}

	/**
	 * TODO
	 * 
	 * @param product
	 * @return
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<Component> getComponentsFor(final Product product) throws XmlRpcException,
			NoHashArrayException {

		final HashArray fields = this.client.getLegalBugFields();

		for (final Map<?, ?> field : fields) {
			// search for component field
			if (!field.get("name").equals("component")) {
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values"));
			final Set<Component> components = new TreeSet<Component>();
			for (final Map<?, ?> value : values) {
				final String componentName = (String) value.get(Component.NAME);
				// extract product <-> component association
				final Object[] visibilityValues = (Object[]) value.get("visibility_values");
				for (Object string : visibilityValues) {
					if (product.getName().equals(string)) {
						components.add(new Component(componentName));
					}
				}
			}
			return components;
		}

		return new TreeSet<Component>();
	}

	/**
	 * TODO
	 * 
	 * @param product
	 * @return
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<Version> getVersionsFor(final Product product) throws XmlRpcException,
			NoHashArrayException {

		LOG.info("Loading versions for product " + product.getName()); //$NON-NLS-1$

		final HashArray fields = this.client.getLegalBugFields();

		for (final Map<?, ?> field : fields) {
			// search for component field
			if (!field.get("name").equals("version")) {
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values"));
			final Set<Version> versions = new TreeSet<Version>();
			for (final Map<?, ?> value : values) {
				final String componentName = (String) value.get(Component.NAME);
				// extract product <-> component association
				final Object[] visibilityValues = (Object[]) value.get("visibility_values");
				for (Object string : visibilityValues) {
					if (product.getName().equals(string)) {
						versions.add(new Version(componentName));
					}
				}
			}
			LOG.debug(versions.size() + " version found for product " + product.getName()); //$NON-NLS-1$
			return versions;
		}
		LOG.debug("No versions found for product " + product.getName()); //$NON-NLS-1$
		return new TreeSet<Version>();
	}

	/**
	 * TODO
	 * 
	 * @param product
	 * @return an empty array, if no {@link Milestone} exist for the given
	 *         {@link Product}.
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<Milestone> getMilestonesFor(final Product product) throws XmlRpcException,
			NoHashArrayException {

		LOG.debug("Loading milestones for product " + product.getName()); //$NON-NLS-1$

		final HashArray fields = this.client.getLegalBugFields();

		for (final Map<?, ?> field : fields) {
			// search for component field
			if (!field.get("name").equals("target_milestone")) { //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values")); //$NON-NLS-1$
			final Set<Milestone> milestones = new TreeSet<Milestone>();
			for (final Map<?, ?> value : values) {
				final String componentName = (String) value.get(Component.NAME);
				// extract product <-> component association
				final Object[] visibilityValues = (Object[]) value.get("visibility_values"); //$NON-NLS-1$
				for (Object string : visibilityValues) {
					if (product.getName().equals(string)) {
						milestones.add(new Milestone(componentName));
					}
				}
			}
			LOG.debug(milestones.size() + " milestones found for product " + product.getName()); //$NON-NLS-1$
			return milestones;
		}
		LOG.debug("No milestones found for product " + product.getName()); //$NON-NLS-1$
		return new TreeSet<Milestone>();
	}
}
