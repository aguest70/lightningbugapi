package de.lightningbug.api.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.BugzillaClient;
import de.lightningbug.api.domain.User;
import de.lightningbug.api.util.HashArray;
import de.lightningbug.api.util.HashArray.NoHashArrayException;

/**
 * This service is used to gather information about the users of the bugzilla instance.
 * 
 * @author Sebastian Kirchner
 * 
 */
public class UserService extends AbstractService {

	protected final static Log LOG = LogFactory.getLog(UserService.class);

	/**
	 * @param client
	 *            the client used by the service to query information from the
	 *            bugzilla instance
	 */
	public UserService(BugzillaClient client) {
		super(client);
	}

	/**
	 * @return a set of all active (not disabled) users of the connected bugzilla, ordered by thier
	 *         id {@link User#getId()}.
	 */
	public Set<User> getActiveUsers() {
		LOG.info("Getting a list of all active users");//$NON-NLS-1$

		final HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("match", new Object[] { "*" }); //$NON-NLS-1$ //$NON-NLS-2$

		final Set<User> users = getUsers(params);
		LOG.info(String.format("%d active users have been found", users.size())); //$NON-NLS-1$
		return users;
	}

	/**
	 * @return a user with the given login name or null, if there is no such user.
	 */
	public User getUser(final String loginName) {

		LOG.info(String.format("Searching for the users with the login name %s", loginName));//$NON-NLS-1$

		final HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("match", new Object[] { loginName }); //$NON-NLS-1$ //$NON-NLS-2$
		params.put("include_disabled", true); //$NON-NLS-1$

		final Set<User> users = getUsers(params);
		if(users.isEmpty()){
			LOG.warn(String.format("User %s not found!")); //$NON-NLS-1$
		}
		return users.iterator().next();
	}

	/**
	 * @return a set of all users of the connected bugzilla, ordered by thier
	 *         id {@link User#getId()}.
	 */
	public Set<User> getUsers() {
		LOG.info("Getting a list of all users");//$NON-NLS-1$

		final HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("match", new Object[] { "*" }); //$NON-NLS-1$ //$NON-NLS-2$
		params.put("include_disabled", true); //$NON-NLS-1$

		final Set<User> users = getUsers(params);
		LOG.info(String.format("%d users have been found", users.size())); //$NON-NLS-1$
		return users;
	}

	/**
	 * @param params
	 * @return
	 */
	private Set<User> getUsers(final HashMap<String, Object> params) {

		// Throw an execption if the client is not logged in
		if(!this.client.isLoggedIn()){
			throw new IllegalStateException(
					"A list of users will only be revealed tho users, that logged in. (see BugzillaClient.login())"); //$NON-NLS-1$
		}

		try{
			/*
			 * From the bugzilla documentation
			 * (http://www.bugzilla.org/docs/4.0/en/html/api/Bugzilla/WebService/User.html#___top):
			 * 
			 * A hash containing one item, users, that is an array of hashes. Each hash describes a
			 * user
			 */
			final Set<User> users = new TreeSet<User>(new Comparator<User>() {

				@Override
				public int compare(User o1, User o2) {
					if(o1.getId() == null && o2.getId() == null)
						return 0;
					if(o1.getId() == null)
						return -1;
					return o1.getId().compareTo(o2.getId());
				}
			});

			// TODO may be null
			final Map<?, ?> map = (Map<?, ?>) this.client.execute("User.get", params); //$NON-NLS-1$
			final HashArray userHash = new HashArray((map).get("users")); //$NON-NLS-1$
			for(final Map<?, ?> userMap : userHash){
				final User user = new User();
				for(final Object key : userMap.keySet()){
					if("id".equals(key)){ //$NON-NLS-1$
						user.setId((Integer) userMap.get(key));
					}
					if("real_name".equals(key)){ //$NON-NLS-1$
						user.setRealName((String) userMap.get(key));
					}
					if("email".equals(key)){ //$NON-NLS-1$
						user.setEMail((String) userMap.get(key));
					}
					if("name".equals(key)){ //$NON-NLS-1$
						user.setLoginName((String) userMap.get(key));
					}
				}
				users.add(user);
			}
			return users;
		}catch(XmlRpcException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(NoHashArrayException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new HashSet<User>();
	}
}
