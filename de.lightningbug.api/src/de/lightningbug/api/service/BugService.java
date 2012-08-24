package de.lightningbug.api.service;

import java.util.ArrayList;
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
import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.domain.User;
import de.lightningbug.api.util.HashArray;
import de.lightningbug.api.util.HashArray.NoHashArrayException;

/**
 * TODO Complete documentation!
 * 
 * @author Sebastian Kirchner (AutoVision GmbH)
 * 
 */
public class BugService extends AbstractService {

	protected final static Log LOG = LogFactory.getLog(ProductService.class);

	public static final String BUG_SEARCH_FIELD_SEVERITY = "severity"; //$NON-NLS-1$
	public static final String BUG_SEARCH_FIELD_ID = "id"; //$NON-NLS-1$

	/**
	 * TODO isn't that stored in the cache???
	 * 
	 * Cache for the names and the legal values of all bug fields
	 */
	private HashArray legalBugFields = null;

	/**
	 * @param client
	 *            the client used by the service to query information from the
	 *            bugzilla instance
	 */
	public BugService(BugzillaClient client) {
		super(client);
	}

	/**
	 * TODO add documentation
	 * 
	 * To get a list of all bugs just pass <code>null</code>
	 * 
	 * @param params
	 * @return
	 */
	public List<Bug> search(final Map<String, Object> searchParams) {

		// convert null into an empty map of parameters
		final Map<String, Object> params = searchParams == null ? new HashMap<String, Object>()
				: searchParams;

		final List<Bug> results = new ArrayList<Bug>();

		try{
			// search for the bugs
			final Object execResult = ((Map<?, ?>) this.client.execute("Bug.search", params))
					.get("bugs");
			if(execResult == null
					|| (execResult.getClass().isArray() && ((Object[]) execResult).length == 0)){
				LOG.warn("Search was not successful: " + searchParams);
				return new LinkedList<Bug>();
			}
			final HashArray bugs = new HashArray(execResult); //$NON-NLS-1$ //$NON-NLS-2$

			final Map<String, User> userCache = new HashMap<String, User>();
			final UserService userService = new UserService(this.client);

			// create bug objects 4 every item in the array
			for(final Map<?, ?> bug : bugs){
				final Bug newBug = new Bug();
				results.add(newBug);

				for(final Object key : bug.keySet()){
					if("summary".equals(key)){ //$NON-NLS-1$
						newBug.setSummary((String) bug.get(key));
						LOG.debug("Property <summary> populated with: " + newBug.getSummary()); //$NON-NLS-1$
					}
					if("severity".equals(key)){ //$NON-NLS-1$
						newBug.setSeverity((String) bug.get(key));
						LOG.debug("Property <severity> populated with: " + newBug.getSeverity()); //$NON-NLS-1$
					}

					if("estimated_time".equals(key)){ //$NON-NLS-1$
						newBug.setEstimatedTime((Double) bug.get(key));
						LOG.debug("Property <estimated_time> populated with: " + newBug.getEstimatedTime()); //$NON-NLS-1$
					}
					if("id".equals(key)){ //$NON-NLS-1$
						newBug.setId((Integer) bug.get(key));
						LOG.debug("Property <id> populated with: " + newBug.getId()); //$NON-NLS-1$
					}
					if("status".equals(key)){ //$NON-NLS-1$
						newBug.setStatus((String) bug.get(key));
						LOG.debug("Property <status> populated with: " + newBug.getId()); //$NON-NLS-1$
					}
					if("assigned_to".equals(key)){ //$NON-NLS-1$

						if(!this.client.isLoggedIn()){
							LOG.info("BugzillaClient is not logged in. Users cannot be queried. Property <assignee> cannot be populated.");
						}else{
							final String loginName = (String) bug.get(key);
							User user = userCache.get(loginName);
							if(user == null){
								user = userService.getUser(loginName);
								userCache.put(loginName, user);
							}
							newBug.setAssignee(user);
							LOG.debug("Property <assignee> populated with: " + newBug.getAssignee()); //$NON-NLS-1$
						}
					}
					if("depends_on".equals(key)){
						final Object[] idObjs = (Object[]) bug.get(key);
						final Set<Integer> ids = new TreeSet<Integer>();
						for(int i = 0; i < idObjs.length; i++){
							ids.add((Integer) idObjs[i]);
						}
						newBug.setDependsOn(ids);
					}

					// else {
					// LOGGER.warning("The field " + key + " (" + bug.get(key)
					// + ") has been skipped");
					// }
				}
			}
		}catch(final XmlRpcException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(final NoHashArrayException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * TODO
	 * 
	 * @return
	 * @throws XmlRpcException
	 * @throws NoHashArrayException
	 */
	public HashArray getLegalBugFields() throws XmlRpcException, NoHashArrayException {
		if(this.legalBugFields == null){
			// result = a hash containing a single element, fields. This is
			// an array of hashes, containing the keys
			final Map<?, ?> result = (Map<?, ?>) this.client.execute("Bug.fields");
			this.legalBugFields = new HashArray(result.values().iterator().next());
		}
		return this.legalBugFields;
	}

	/**
	 * TODO
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<String> getLegalBugFieldValues(final String fieldName) {
		try{
			final HashArray fields = this.getLegalBugFields();
			for(final Map<?, ?> field : fields){
				// search for component field
				if(!field.get("name").equals(fieldName)){ //$NON-NLS-1$
					continue;
				}
				// legal component values found
				final HashArray values = new HashArray(field.get("values")); //$NON-NLS-1$
				final List<String> result = new ArrayList<String>(values.size());
				for(final Map<?, ?> value : values){
					result.add((String) value.get("name")); //$NON-NLS-1$
				}
				return result;
			}
		}catch(final XmlRpcException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(final NoHashArrayException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new LinkedList<String>();
	}

	/**
	 * @param bug
	 * @return
	 */
	public boolean create(final Bug bug) {
		try{

			final Map<String, Object> params = new HashMap<String, Object>();
			params.put(Bug.PRODUCT, bug.getProduct());
			params.put(Bug.COMPONENT, bug.getComponent());
			params.put(Bug.SUMMARY, bug.getSummary());
			params.put(Bug.VERSION, bug.getVersion());
			params.put(Bug.DESCRIPTION, bug.getDescription());
			params.put(Bug.SEVERITY, bug.getSeverity());

			final Object result = this.client.execute("Bug.create", params);

			if(result instanceof Map){
				Object idParam = ((Map<?, ?>) result).get("id"); //$NON-NLS-1$
				if(idParam instanceof Number){
					final Integer id = ((Number) idParam).intValue();
					bug.setId(id);
					return true;
				}
			}
		}catch(XmlRpcException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
