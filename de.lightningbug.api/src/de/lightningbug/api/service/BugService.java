package de.lightningbug.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.BugzillaClient;
import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.util.HashArray;
import de.lightningbug.api.util.HashArray.NoHashArrayException;

public class BugService {

	protected final static Log LOG = LogFactory.getLog(ProductService.class);

	protected BugzillaClient client = null;

	/**
	 * TODO isn't that stored in the cache???
	 * 
	 * Cache for the names and the legal values of all bug fields
	 */
	private HashArray legalBugFields = null;

	/**
	 * @param client
	 *            the client used by the factory to query information from the
	 *            bugzilla instance
	 */
	public BugService(final BugzillaClient client) {
		if(client == null){
			throw new IllegalArgumentException("Paramter <client> must not be mull"); //$NON-NLS-1$
		}
		this.client = client;
	}

	/**
	 * TODO Docu
	 * 
	 * To get a list of all bugs just pass <code>null</code>
	 * 
	 * @param params
	 * @return
	 */
	public List<Bug> search(final Map<String, Object[]> searchParams) {

		// convert null into an empty map of parameters
		final Map<String, Object[]> params = searchParams == null ? new HashMap<String, Object[]>()
				: searchParams;

		final List<Bug> results = new ArrayList<Bug>();

		try{
			final HashArray bugs = new HashArray(
			// TODO may be null
					((Map<?, ?>) this.client.execute("Bug.search", params)).get("bugs")); //$NON-NLS-1$ //$NON-NLS-2$

			// create bug objects 4 every item in the array
			for(final Map<?, ?> bug : bugs){
				final Bug newBug = new Bug();
				results.add(newBug);

				for(final Object key : bug.keySet()){
					if("summary".equals(key)){ //$NON-NLS-1$
						newBug.setSummary((String) bug.get(key));
						LOG.debug("Field <summary> populated with: " + newBug.getSummary()); //$NON-NLS-1$
					}
					if("estimated_time".equals(key)){ //$NON-NLS-1$
						newBug.setEstimatedTime((Double) bug.get(key));
						LOG.debug("Field <estimated_time> populated with: " + newBug.getEstimatedTime()); //$NON-NLS-1$
					}
					if("id".equals(key)){ //$NON-NLS-1$
						newBug.setId((Integer) bug.get(key));
						LOG.debug("Field <id> populated with: " + newBug.getId()); //$NON-NLS-1$
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

}
