package de.lightningbug.api;

import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.domain.BugzillaObject;

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
 * disconnected ({@link BugzillaClient#isConnected()}).
 * </p>
 * 
 * @see BugzillaClient
 * @see BugzillaClient#connect()
 * @see BugzillaClient#setUserName(String)
 * @see BugzillaClient#setPassword(String)
 * @see BugzillaClient#create(BugzillaObject)
 * 
 * @author Sebastian Kirchner
 * 
 */
public class BugzillaClient {

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

	private boolean connected = false;

	private String password;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private java.net.URL url;

	private String userName;

	private XmlRpcClient xmlRpcClient = null;

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
	 * Output of the version nummer of this API.
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		System.out.println("LightningBug API version 3.6.2 (2010-11-30)");
	}
	
	/**
	 * Open the connection to the bugzilla instance
	 * 
	 * @return <code>true</code> if the connection was established, otherwise
	 *         <code>false</code>.
	 */
	public boolean connect() {
		try {
			final HashMap<?, ?> resultMap = (HashMap<?, ?>) this.exec("User.login", "login",
					this.getUserName(), "password", this.getPassword(), "remember", true);
			Object userId = resultMap.get("id");
			if (userId instanceof Number && ((Number) userId).intValue() > 0) {
				this.connected = true;
			}
		} catch (final XmlRpcException e) {
			// TODO
			e.printStackTrace();
			return false;
		}
		return this.isConnected();
	}

	/**
	 * Disconnnect the {@link BugzillaClient} from the bugzilla instance.
	 * 
	 * @return <code>true</code> if the connection was closed successfully,
	 *         otherwise <code>false</code>.
	 */
	public boolean disconnect() {
		if (!this.isConnected()) {
			return false;
		}
		try {
			this.exec("User.logout");
			this.connected = false;
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

	private boolean createBug(final Bug bug) {
		try {
			final Object result = this.exec("Bug.create", "product", bug.getProduct(), "component",
					bug.getComponent(), "summary", bug.getSummary(), "version", bug.getVersion(),
					"description", bug.getDescription());
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
	private Object exec(final String methodName, final Object... paramNamesAndValues)
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
	 *         connected to the bugzilla instance addressed by the
	 *         {@link BugzillaClient#getURL()}. Otherwise <code>false</code>
	 *         will be returned.
	 * 
	 * @see BugzillaClient#connect()
	 * @see BugzillaClient#disconnect()
	 */
	public boolean isConnected() {
		return this.connected;
	}

/**
	 * Setter for the password used to connect to bugzilla. Setting the password
	 * will only work, if the client is disconnected ({@link BugzillaClient#isConnected()).
	 * 
	 * @param password
	 *            the password used to connect to bugzilla.
	 * 
	 * @see BugzillaClient#getPassword()
	 * @see BugzillaClient#connect()
	 * @see BugzillaClient#isConnected()
	 * @see BugzillaClient#disconnect()
	 */
	public void setPassword(final String password) {
		if (this.isConnected()) {
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
	 * @see BugzillaClient#connect()
	 * @see BugzillaClient#isConnected()
	 * @see BugzillaClient#disconnect()
	 */
	public void setUserName(final String userName) {
		if (this.isConnected()) {
			throw new IllegalStateException(
					"A new username or password can only be set when the client is disconnected"); //$NON-NLS-1$
		}
		final String oldValue = this.userName;
		this.userName = userName;
		pcs.firePropertyChange(USER_NAME, oldValue, this.userName);
	}
}
