package de.lightningbug.api;

import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.print.attribute.HashAttributeSet;

import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.domain.BugzillaObject;
import de.lightningbug.api.domain.Product;
import de.lightningbug.api.domain.Severity;
import de.lightningbug.api.util.HashArray;
import de.lightningbug.api.util.HashArray.NoHashArrayException;

/**
 * <p>
 * The {@link BugzillaClient} is the Client-Endpoint of the XML-RPC-Connection.
 * It's used to connect to a certain Bugzilla 3.6+ instance. In oder to d that,
 * a base URL, username and password must be provided.
 * </p>
 * <p>
 * After login Bugzilla-Objects can be created using the "create"-method (e.g.
 * {@link BugzillaClient#create(Bug)})
 * </p>
 * <p>
 * Setting a new username or password is possible, but only if the client is
 * disconnected ({@link BugzillaClient#isLoggedIn()}).
 * </p>
 * TODO: System-wide cache for queried data, that can be reset
 * 
 * @see BugzillaClient
 * @see BugzillaClient#login()
 * @see BugzillaClient#setUserName(String)
 * @see BugzillaClient#setPassword(String)
 * @see BugzillaClient#create(BugzillaObject)
 * 
 * @author Sebastian Kirchner
 * 
 */
public class BugzillaClient {

	private static final Logger LOGGER = Logger.getLogger(BugzillaClient.class.getName());

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#password}
	 */
	public static final String PASSWORD = "password"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#url}
	 */
	public static final String URL = "url"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#userName}
	 */
	public static final String USER_NAME = "userName"; //$NON-NLS-1$

	private java.net.URL apiURL;

	private boolean login = false;

	private String password;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private java.net.URL url;

	private String userName;

	private XmlRpcClient xmlRpcClient = null;

	/**
	 * Cache for the names and the legal values of all bug fields
	 */
	private HashArray legalBugFields = null;

	/**
	 * Constructor for the bugzilla client.
	 * 
	 * @param url
	 *            the URL to base folder of the bugzilla instance, this client
	 *            will connect to. Example:
	 *            <p>
	 *            <code>http://10.187.19.238:8080/bugzilla-3.6.2/</code>
	 *            </p>
	 * @param userName
	 *            the name of the account used to login
	 * @param password
	 *            the password of the login account
	 * 
	 * @throws MalformedURLException
	 *             if the given URL is invalid
	 */
	public BugzillaClient(final java.net.URL url, final String userName, final String password)
			throws MalformedURLException {
		super();
		this.url = url;
		this.userName = userName;
		this.password = password;
		// Constructing the API-URL needed to configure the XML-RPC-Client
		final String apiURL = this.url.toString() + "xmlrpc.cgi"; //$NON-NLS-1$
		this.apiURL = new URL(apiURL);

	}

	/**
	 * Open the connection to the bugzilla instance
	 * 
	 * @return <code>true</code> if the connection was established, otherwise
	 *         <code>false</code>.
	 */
	public boolean login() {
		try {
			final HashMap<?, ?> resultMap = (HashMap<?, ?>) this.exec("User.login", "login", //$NON-NLS-1$ //$NON-NLS-2$
					this.getUserName(), "password", this.getPassword(), "remember", true);
			Object userId = resultMap.get("id");
			if (userId instanceof Number && ((Number) userId).intValue() > 0) {
				this.login = true;
			}
		} catch (final XmlRpcException e) {
			// TODO
			e.printStackTrace();
			return false;
		}
		return this.isLoggedIn();
	}

	/**
	 * Disconnnect the {@link BugzillaClient} from the bugzilla instance.
	 * 
	 * @return <code>true</code> if the connection was closed successfully,
	 *         otherwise <code>false</code>.
	 */
	public boolean logout() {
		if (!this.isLoggedIn()) {
			return false;
		}
		try {
			this.exec("User.logout");
			this.login = false;
			// clear coockie store from old login cookie
			this.getXmlRpcClient().getCookieStore().clear();
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Creates the given object in bugzilla. If the object has been successfully
	 * created, <code>true</code> will be returned.
	 * <p>
	 * If a {@link Bug} object has been created, the given bug object will
	 * contain the bug id provided by bugzilla
	 * </p>
	 * 
	 * @param <T>
	 *            Type of the object, that should be created (e.g. {@link Bug})
	 * @param t
	 *            the object, that should be created in bugzilla
	 * @return <code>true</code> if the creation succeeded. In that case the
	 *         given object might be enhanced with information provided by
	 *         bugzilla depending on the type of object. The method will return
	 *         <code>false</code>, if the creation failed.
	 */
	public <T extends BugzillaObject> boolean create(final T t) {
		if (t instanceof Bug) {
			return createBug((Bug) t);
		}
		return false;
	}

	/**
	 * Returns all instances of the given type of Bugzilla objects. The objects
	 * are retrieved from the currently connected bugzilla instance.
	 * <p>
	 * So far this works only for {@link Product} and {@link Severity} objects
	 * </p>
	 * 
	 * @param type
	 *            the type of bugzilla objects to be loaded from the currently
	 *            connected bugzilla instance
	 * @return a list of all objects of the given type stored in bugzilla
	 */
	@SuppressWarnings("unchecked")
	public <T extends BugzillaObject> List<T> getAll(final Class<T> type) {
		// a list for the resulting objects
		final LinkedList<T> resultList = new LinkedList<T>();

		if (Product.class.isAssignableFrom(type)) {
			// TODO keep object, cause it contains an internal cache
			final ProductFactory productFactory = new ProductFactory(this);
			for (final Product product : productFactory.getProducts()) {
				resultList.add((T) product);
			}
		} else if (Severity.class.isAssignableFrom(type)) {

			try {
				final HashArray fields = this.getLegalBugFields();

				for (final Map<?, ?> field : fields) {
					// search for component field
					if (!field.get("name").equals("bug_severity")) {
						continue;
					}
					// legal severity values found
					final HashArray values = new HashArray(field.get("values"));
					for (final Map<?, ?> value : values) {
						resultList.add((T) new Severity((String) value.get("name")));
					}
				}
			} catch (XmlRpcException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoHashArrayException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// TODO
			LOGGER.warning("Not yet supported");
		}

		return resultList;
	}

	/**
	 * Returns a list of objects from the connected bugzilla instance. The type
	 * of the objects depend on the given type parameter <code>type</code>.
	 * <p>
	 * The returned object correspond to the given ids. At least one id must be
	 * provided or an empty list will be returned. If more than one id is
	 * provided, multiple objects will be returned.
	 * </p>
	 * <p>
	 * If no object of the given <code>type</code> with the given
	 * <code>id</code> is found, it is simply missing in the output list.
	 * <b>No</b> exception will be thrown to indicate, that the particular
	 * object doesn't exist.
	 * </p>
	 * <p>
	 * <b>TODO</b>: So far this works only for {@link Bug} objects.
	 * </p>
	 * <p>
	 * Examples:
	 * 
	 * <pre>
	 * client.get(Bug.class, 12); -> returns a list containg the bug object with id = 12
	 * 
	 * client.get(Bug.class, 1, 2 ,3 ); -> returns a list containg three bug objects with id = 1, id = 2 and id = 3
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param <T>
	 *            an inheritor of the class {@link BugzillaObject}
	 * @param type
	 *            type of the object(s) to be returned
	 * @param ids
	 *            one or more ids, that indentify the requested object(s)
	 * @return a list containg the requested objects. If one or more objects of
	 *         the given type with the given id don't exist, then they will not
	 *         be part of the list. This method is guaranteed to return a non
	 *         <code>null</code> value.
	 */
	public <T extends BugzillaObject> List<T> get(final Class<T> type, final Integer... ids) {
		final List<T> results = new ArrayList<T>(ids.length);

		if (!this.isLoggedIn()) {
			LOGGER.warning("This bugzilla client is not logged in! Not all fields accessible by a certain user can be retrieved.");//$NON-NLS-1$
		}

		if (!Bug.class.isAssignableFrom(type)) {
			// TODO
			LOGGER.warning("Not yet supported");
			return new LinkedList<T>();
		}

		try {
			final HashArray bugs = new HashArray(
					((Map<?, ?>) this.exec("Bug.get", "ids", ids)).get("bugs"));
			// create bug objects 4 every item in the array
			for (Map<?, ?> bug : bugs) {
				final Bug newBug = new Bug();
				results.add((T) newBug);
				for (Object key : bug.keySet()) {
					if ("summary".equals(key)) { //$NON-NLS-1$
						newBug.setSummary((String) bug.get(key));
						LOGGER.finer("Field <summary> populated with: " + newBug.getSummary()); //$NON-NLS-1$
					}
					if ("estimated_time".equals(key)) { //$NON-NLS-1$
						newBug.setEstimatedTime((Double)bug.get(key));
						LOGGER.finer("Field <estimated_time> populated with: " + newBug.getEstimatedTime()); //$NON-NLS-1$
					}
					if ("id".equals(key)) { //$NON-NLS-1$
						newBug.setId((Integer)bug.get(key));
						LOGGER.finer("Field <id> populated with: " + newBug.getId()); //$NON-NLS-1$
					}
					
					
//					else {
//						LOGGER.warning("The field " + key + " (" + bug.get(key)
//								+ ") has been skipped");
//					}
				}
			}
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHashArrayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	// /**
	// * @param args
	// */
	// public static void main(String[] args) {
	// try {
	// final URL url = new URL("http://vwagwos00223:8080/bugzilla-3.6.2/");
	// final BugzillaClient client = new BugzillaClient(url,
	// "sebastian.kirchner@autovision-gmbh.com", "");
	//
	// final List<Severity> severities = client.getAll(Severity.class);
	// final List<Product> products = client.getAll(Product.class);
	// final Bug bug = new Bug();
	// bug.setSeverity(severities.get(0));
	// bug.setProduct(products.get(0));
	// bug.setVersion(products.get(0).getVersions().iterator().next());
	// System.out.println(bug);
	//
	// } catch (MalformedURLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	HashArray getLegalBugFields() throws XmlRpcException, NoHashArrayException {
		if (this.legalBugFields == null) {
			// result = a hash containing a single element, fields. This is
			// an array of hashes, containing the keys
			final Map<?, ?> result = (Map<?, ?>) this.exec("Bug.fields");
			this.legalBugFields = new HashArray(result.values().iterator().next());
		}
		return this.legalBugFields;
	}

	// public List<String> getLegalBugFieldValues(final String fieldName){
	// try {
	// final HashArray fields = this.getLegalBugFields();
	// for (final Map<?, ?> field : fields) {
	// // search for component field
	// if (!field.get("name").equals(fieldName)) {
	// continue;
	// }
	// // legal component values found
	// final HashArray values = new HashArray(field.get("values"));
	// final List<String> result = new ArrayList<String>(values.size());
	// for (final Map<?, ?> value : values) {
	// result.add((String)value.get("name"));
	// }
	// return result;
	// }
	//
	// } catch (XmlRpcException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (NoHashArrayException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return new LinkedList<String>();
	// }

	private boolean createBug(final Bug bug) {
		try {
			final Object result = this.exec("Bug.create", Bug.PRODUCT, bug.getProduct(),
					Bug.COMPONENT, bug.getComponent(), Bug.SUMMARY, bug.getSummary(), Bug.VERSION,
					bug.getVersion(), Bug.DESCRIPTION, bug.getDescription(), Bug.SEVERITY, bug
							.getSeverity().getName());
			if (result instanceof Map) {
				Object idParam = ((Map<?, ?>) result).get("id"); //$NON-NLS-1$
				if (idParam instanceof Number) {
					final Integer id = ((Number) idParam).intValue();
					bug.setId(id);
					return true;
				}
			}
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * TODO document!!!
	 * 
	 * @param methodName
	 * @param paramNamesAndValues
	 * @return
	 * @throws XmlRpcException
	 */
	Object exec(final String methodName, final Object... paramNamesAndValues)
			throws XmlRpcException {

		final HashMap<String, Object> executionData = new HashMap<String, Object>();
		for (int i = 0; i < paramNamesAndValues.length; i = i + 2) {
			executionData.put((String) paramNamesAndValues[i], paramNamesAndValues[i + 1]);
		}
		final ArrayList<Object> params = new ArrayList<Object>();
		params.add(executionData);
		return this.getXmlRpcClient().execute(methodName, params);
	}

	/**
	 * Getter for the password used to connect to bugzilla.
	 * 
	 * @return the password used to connect to bugzilla.
	 * 
	 * @see BugzillaClient#setPassword(String)
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Setter for the HTTP URL of the bugzilla instance this client sould
	 * connect to. This property can only be set once for a
	 * {@link BugzillaClient} via constructor.
	 * 
	 * @return der Wert der Eigenschaft {@link BugzillaClient#url}
	 */
	public java.net.URL getURL() {
		return this.url;
	}

	/**
	 * Getter for the user name used to connect to bugzilla (e.g. the email
	 * address of the user).
	 * 
	 * @return the user name used to connect to bugzilla
	 * 
	 * @see BugzillaClient#setUserName(String)
	 */
	public String getUserName() {
		return this.userName;
	}

	private XmlRpcClient getXmlRpcClient() {
		if (this.xmlRpcClient == null) {
			this.xmlRpcClient = new XmlRpcClient(this.apiURL);
		}
		return this.xmlRpcClient;
	}

	/**
	 * @return <code>true</code> if this {@link BugzillaClient} is currently
	 *         logged in to the bugzilla instance addressed by the
	 *         {@link BugzillaClient#getURL()}. Otherwise <code>false</code>
	 *         will be returned.
	 * 
	 * @see BugzillaClient#login()
	 * @see BugzillaClient#logout()
	 */
	public boolean isLoggedIn() {
		return this.login;
	}

/**
	 * Setter for the password used to connect to bugzilla. Setting the password
	 * will only work, if the client is disconnected ({@link BugzillaClient#isConnected()).
	 * 
	 * @param password
	 *            the password used to connect to bugzilla.
	 * 
	 * @see BugzillaClient#getPassword()
	 * @see BugzillaClient#login()
	 * @see BugzillaClient#isLoggedIn()
	 * @see BugzillaClient#logout()
	 */
	public void setPassword(final String password) {
		if (this.isLoggedIn()) {
			throw new IllegalStateException(
					"A new username or password can only be set when the client is disconnected"); //$NON-NLS-1$
		}
		final String oldValue = this.password;
		this.password = password;
		pcs.firePropertyChange(PASSWORD, oldValue, this.password);
	}

/**
	 * Setter for the user name used to connect to bugzilla (e.g. the email
	 * address of the user). Setting the user name
	 * will only work, if the client is disconnected ({@link BugzillaClient#isConnected()).
	 * 
	 * @param userName
	 *            the user name used to connect to bugzilla
	 * 
	 * @see BugzillaClient#getUserName()
	 * @see BugzillaClient#login()
	 * @see BugzillaClient#isLoggedIn()
	 * @see BugzillaClient#logout()
	 */
	public void setUserName(final String userName) {
		if (this.isLoggedIn()) {
			throw new IllegalStateException(
					"A new username or password can only be set when the client is disconnected"); //$NON-NLS-1$
		}
		final String oldValue = this.userName;
		this.userName = userName;
		pcs.firePropertyChange(USER_NAME, oldValue, this.userName);
	}

}
