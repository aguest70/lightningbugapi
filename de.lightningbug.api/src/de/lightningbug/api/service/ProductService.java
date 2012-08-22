/**
 * 
 */
package de.lightningbug.api.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.BugzillaClient;
import de.lightningbug.api.cache.LocalCache;
import de.lightningbug.api.domain.Product;
import de.lightningbug.api.util.HashArray;
import de.lightningbug.api.util.HashArray.NoHashArrayException;

/**
 * encapsulates methods to handle Bugzilla {@link Product} objects.
 * 
 * @author Sebastian Kirchner
 * 
 */
public class ProductService {

	protected final static Log LOG = LogFactory.getLog(ProductService.class);

	protected BugzillaClient client = null;

	protected BugService bugService = null;

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
	public ProductService(final BugzillaClient client) {
		this(client, true);
	}

	/**
	 * @param client
	 *            the client used by the factory to query information from the
	 *            bugzilla instance
	 * @param useLocalCache
	 */
	public ProductService(final BugzillaClient client, final boolean useLocalCache) {
		super();
		if(client == null){
			throw new IllegalArgumentException("Paramter <client> must not be mull"); //$NON-NLS-1$
		}
		this.client = client;
		this.useLocalCache = useLocalCache;
	}

	public List<Product> getProducts() {

		if(this.products != null){
			return this.products;
		}

		// the availibility of the cache will only be checked and maintained, if
		// the factory is configured to use the cache
		boolean cacheAvailable = true;

		if(this.useLocalCache){
			final List<Product> productsFromCache = LocalCache.getProducts(this.client);
			cacheAvailable = productsFromCache != null;
			if(cacheAvailable){
				// update the cache in the backgroud...
				new Thread() {

					@Override
					public void run() {
						LOG.info("updating the product cache in the backgroud"); //$NON-NLS-1$
						ProductService.this.products = ProductService.this.loadProducts();
						LocalCache.setProducts(ProductService.this.client,
								ProductService.this.products);
					}
				}.start();
				// ... and return the results from the local cache
				this.products = productsFromCache;
				return this.products;
			}
		}

		this.products = this.loadProducts();
		if(!cacheAvailable){
			// the make it available the next time
			LOG.info("updating the product cache"); //$NON-NLS-1$
			LocalCache.setProducts(this.client, this.products);
		}
		return this.products;
	}
	

	protected List<Product> loadProducts() {
		final List<Product> prods = new LinkedList<Product>();
		try{
			// Param = a hash containing one item, ids, that is an array of
			// product ids.
			final HashMap<String, Object[]> params = new HashMap<String, Object[]>();
			params.put("ids", this.getAccessibleProductIds()); //$NON-NLS-1$
			final Map<?, ?> result = (Map<?, ?>) this.client.execute("Product.get", params); //$NON-NLS-1$

			// result = a hash containing one item, products, that is an array
			// of hashes.
			final HashArray productHashes = new HashArray(result.values().iterator().next());
			for(final Map<?, ?> productHash : productHashes){
				// Each hash describes a product, and has the following items:
				// id,name, description, and internals. The id item is the id of
				// the product. The name item is the name of the product. The
				// description is the description of the product. Finally, the
				// internals is an internal representation of the product
				final Integer id = (Integer) productHash.get(Product.ID);
				final String name = (String) productHash.get(Product.NAME);
				final Product product = new Product(id, name);
				product.setComponents(this.getComponentsFor(product));
				product.setVersions(this.getVersionsFor(product));
				product.setMilestones(this.getMilestonesFor(product));
				product.setSeverities(this.getSeverities());
				prods.add(product);
			}

		}catch(final XmlRpcException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(final NoHashArrayException e){
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
	 * @see ProductService#getProducts()
	 */
	private Integer[] getAccessibleProductIds() throws XmlRpcException {
		if(this.accessibleProductIds == null){

			final Map<?, ?> result = (Map<?, ?>) this.client
					.execute("Product.get_accessible_products"); //$NON-NLS-1$
			final Object[] ints = (Object[]) result.values().iterator().next();
			this.accessibleProductIds = new Integer[ints.length];
			for(int i = 0; i < ints.length; i++){
				this.accessibleProductIds[i] = (Integer) ints[i];
			}
		}
		return this.accessibleProductIds;
	}

	/**
	 * TODO
	 * 
	 * @param product
	 * @return
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<String> getComponentsFor(final Product product) throws XmlRpcException,
			NoHashArrayException {

		final HashArray fields = this.getLegalBugFields();

		for(final Map<?, ?> field : fields){
			// search for component field
			if(!field.get("name").equals("component")){
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values"));
			final Set<String> components = new TreeSet<String>();
			for(final Map<?, ?> value : values){
				final String componentName = (String) value.get("name");
				// extract product <-> component association
				final Object[] visibilityValues = (Object[]) value.get("visibility_values");
				for(final Object string : visibilityValues){
					if(product.getName().equals(string)){
						components.add(componentName);
					}
				}
			}
			return components;
		}

		return new TreeSet<String>();
	}

	/**
	 * TODO
	 * 
	 * @param product
	 * @return
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<String> getVersionsFor(final Product product) throws XmlRpcException,
			NoHashArrayException {

		LOG.info("Loading versions for product " + product.getName()); //$NON-NLS-1$

		final HashArray fields = this.getLegalBugFields();

		for(final Map<?, ?> field : fields){
			// search for component field
			if(!field.get("name").equals("version")){
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values"));
			final Set<String> versions = new TreeSet<String>();
			for(final Map<?, ?> value : values){
				final String version = (String) value.get("name");
				// extract product <-> component association
				final Object[] visibilityValues = (Object[]) value.get("visibility_values");
				for(final Object visibilityValue : visibilityValues){
					if(product.getName().equals(visibilityValue)){
						versions.add(version);
					}
				}
			}
			LOG.debug(versions.size() + " version found for product " + product.getName()); //$NON-NLS-1$
			return versions;
		}
		LOG.debug("No versions found for product " + product.getName()); //$NON-NLS-1$
		return new TreeSet<String>();
	}

	/**
	 * TODO
	 * 
	 * @param product
	 * @return an empty array, if no {@link Milestone} exist for the given {@link Product}.
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<String> getMilestonesFor(final Product product) throws XmlRpcException,
			NoHashArrayException {

		LOG.debug("Loading milestones for product " + product.getName()); //$NON-NLS-1$

		final HashArray fields = this.getLegalBugFields();

		for(final Map<?, ?> field : fields){
			// search for component field
			if(!field.get("name").equals("target_milestone")){ //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values")); //$NON-NLS-1$
			final Set<String> milestones = new TreeSet<String>();
			for(final Map<?, ?> value : values){
				final String milestone = (String) value.get("name");
				// extract product <-> component association
				final Object[] visibilityValues = (Object[]) value.get("visibility_values"); //$NON-NLS-1$
				for(final Object visibilityValue : visibilityValues){
					if(product.getName().equals(visibilityValue)){
						milestones.add(milestone);
					}
				}
			}
			LOG.debug(milestones.size() + " milestones found for product " + product.getName()); //$NON-NLS-1$
			return milestones;
		}
		LOG.debug("No milestones found for product " + product.getName()); //$NON-NLS-1$
		return new TreeSet<String>();
	}

	/**
	 * TODO
	 * 
	 * @return an empty array, if no {@link Milestone} exist for the given {@link Product}.
	 * @throws NoHashArrayException
	 * @throws XmlRpcException
	 */
	private Set<String> getSeverities() throws XmlRpcException, NoHashArrayException {

		LOG.debug("Loading severities"); //$NON-NLS-1$

		final HashArray fields = this.getLegalBugFields();

		for(final Map<?, ?> field : fields){
			// search for component field
			if(!field.get("name").equals("bug_severity")){ //$NON-NLS-1$ //$NON-NLS-2$
				continue;
			}
			// legal component values found
			final HashArray values = new HashArray(field.get("values")); //$NON-NLS-1$
			final Set<String> severities = new TreeSet<String>();
			for(final Map<?, ?> value : values){
				//$NON-NLS-1$
				severities.add((String) value.get("name"));
			}
			LOG.debug(severities.size() + " severities found"); //$NON-NLS-1$
			return severities;
		}
		LOG.debug("No severities found"); //$NON-NLS-1$
		return new TreeSet<String>();
	}

	private HashArray getLegalBugFields() throws XmlRpcException, NoHashArrayException {
		if(this.bugService == null){
			this.bugService = new BugService(this.client);
		}
		return this.bugService.getLegalBugFields();
	}
}
