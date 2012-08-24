package de.lightningbug.api;

import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.domain.Bug;
import de.lightningbug.api.xmlrpc.XmlRpcClient;

/**
 * <p>
 * The {@link BugzillaClient} is the Client-Endpoint of the XML-RPC-Connection. It's used to connect
 * to a certain Bugzilla 3.6+ instance. In oder to d that, a base URL, username and password must be
 * provided.
 * </p>
 * <p>
 * After login Bugzilla-Objects can be created using the "create"-method (e.g.
 * {@link BugzillaClient#create(Bug)})
 * </p>
 * <p>
 * Setting a new username or password is possible, but only if the client is disconnected (
 * {@link BugzillaClient#isLoggedIn()}).
 * </p>
 * TODO: System-wide cache for queried data, that can be reset
 * 
 * @see BugzillaClient
 * @see BugzillaClient#login()
 * @see BugzillaClient#setUserName(String)
 * @see BugzillaClient#setPassword(String)
 * @see BugzillaClient#create(IBugzillaObject)
 * 
 * @author Sebastian Kirchner
 * 
 */
// TODO this client can extend XMLRPCClient
public class BugzillaClient extends XmlRpcClient {

	protected final static Log LOG = LogFactory.getLog(BugzillaClient.class);

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#password}
	 */
	public static final String PASSWORD = "password"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#login}
	 */
	public static final String LOGIN = "login"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#url}
	 */
	public static final String URL = "url"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link BugzillaClient#userName}
	 */
	public static final String USER_NAME = "userName"; //$NON-NLS-1$

	private boolean login = false;

	private String password;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final java.net.URL url;

	private String userName;

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
		super(createAPIURL(url)); //$NON-NLS-1$
		this.url = url;
		this.userName = userName;
		this.password = password;
	}

	/**
	 * Constructing the API-URL needed to configure the XML-RPC-Client
	 * 
	 * @param url
	 *            the URL to the bugzilla installation
	 * @return the apiURL needed for the XMLRPCClient
	 * @throws MalformedURLException
	 *             if the URL cannot be created becase the given URL has an unknown protocol or
	 *             could not be parsed
	 */
	private static URL createAPIURL(final URL url) throws MalformedURLException {
		String urlString = url.toString();
		// add a slash to the end, if it's not there
		if(!urlString.endsWith("/")){
			urlString = urlString.concat("/");
		}
		return new URL(urlString + "xmlrpc.cgi");
	}

	/**
	 * Login to a bugzilla instance. A login is required, to access all bug fields or create bugs.
	 * 
	 * @return <code>true</code> if the connection was established, otherwise <code>false</code>.
	 */
	public boolean login() {
		try{

			// assemble the map of params
			final HashMap<String, Object> executionData = new HashMap<String, Object>();
			executionData.put(LOGIN, this.getUserName());
			executionData.put(PASSWORD, this.password);
			executionData.put("remember", true); //$NON-NLS-1$

			// build the stuct
			final ArrayList<Object> params = new ArrayList<Object>();
			params.add(executionData);

			// execute the call
			final HashMap<?, ?> resultMap = (HashMap<?, ?>) this.execute("User.login", params); //$NON-NLS-1$

			final Object userId = resultMap.get("id"); //$NON-NLS-1$
			if(userId instanceof Number && ((Number) userId).intValue() > 0){
				this.login = true;
				this.pcs.firePropertyChange(LOGIN, false, this.login);
			}
		}catch(final XmlRpcException e){
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
		if(!this.isLoggedIn()){
			return false;
		}
		try{
			this.execute("User.logout"); //$NON-NLS-1$
			final boolean oldValue = this.login;
			this.login = false;
			this.pcs.firePropertyChange(LOGIN, oldValue, this.login);

			// clear coockie store from old login cookie
			this.getCookieStore().clear();
		}catch(final XmlRpcException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * TODO document!!!
	 * 
	 * @param methodName
	 * @param paramNamesAndValues
	 * @return
	 * @throws XmlRpcException
	 */
	public Object execute(final String methodName) throws XmlRpcException {
		return this.execute(methodName, new ArrayList<Object>());
	}

	/**
	 * <b>Example:</b>
	 * 
	 * <pre>
	 * HashMap&lt;String, Object&gt; params = new HashMap&lt;String, Object&gt;();
	 * params.put(&quot;match&quot;, new Object[] { &quot;Max&quot;, &quot;Moritz&quot; });
	 * params.put(&quot;include_disabled&quot;, true);
	 * bugzillaClient.execute(&quot;User.get&quot;, params);
	 * </pre>
	 * 
	 * @param methodName
	 * @param params
	 *            a Map of parameter names (Strings) an their corresponing value(s). If a parameter
	 *            has multiple values package them into an object array.
	 * @return
	 * @throws XmlRpcException
	 */
	public Object execute(final String methodName, final Map<String, Object> params)
			throws XmlRpcException {

		// build the stuct
		final ArrayList<Object> pParams = new ArrayList<Object>();
		pParams.add(params);
		return this.execute(methodName, pParams);
	}

	/**
	 * Setter for the HTTP URL of the bugzilla instance this client sould
	 * connect to. This property can only be set once for a {@link BugzillaClient} via constructor.
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

	/**
	 * @return <code>true</code> if this {@link BugzillaClient} is currently
	 *         logged in to the bugzilla instance addressed by the {@link BugzillaClient#getURL()}.
	 *         Otherwise <code>false</code> will be returned.
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
		if(this.isLoggedIn()){
			throw new IllegalStateException(
					"A new username or password can only be set when the client is disconnected"); //$NON-NLS-1$
		}
		final String oldValue = this.password;
		this.password = password;
		this.pcs.firePropertyChange(PASSWORD, oldValue, this.password);
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
		if(this.isLoggedIn()){
			throw new IllegalStateException(
					"A new username or password can only be set when the client is disconnected"); //$NON-NLS-1$
		}
		final String oldValue = this.userName;
		this.userName = userName;
		this.pcs.firePropertyChange(USER_NAME, oldValue, this.userName);
	}

}
