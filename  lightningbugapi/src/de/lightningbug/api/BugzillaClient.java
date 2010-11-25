package de.lightningbug.api;

import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

import de.lightningbug.api.domain.Bug;

/**
 * TODO Documentation
 * 
 * <p>
 * Setting a new username or password is possible, but only if the client is
 * disconnected ({@link BugzillaClient#isConnected()}).
 * </p>
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

	public static void main(String[] args) {
		try {
			final BugzillaClient bugzillaClient = new BugzillaClient(new URL(
					"http://host:8080/bugzilla-3.6.2/"),
					"username", "password");
			if (bugzillaClient.connect()) {

				final Bug bug = new Bug();
				bug.setSummary("Benutzer kann einen Programmfehler melden");
				bug.setDescription("Über den Menüpunkt Hilfe > Fehler melden kann der Nutzer einen Programmfehler an das Entwicklertem melden.");
				bug.setProduct("Medusa");
				bug.setVersion("Alpha 1 (\"Stheno\")");
				bug.setComponent("!Allgemein");

				bugzillaClient.create(bug);
				bugzillaClient.disconnect();
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
	 * @return
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
	 * @return
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
	 * @param bug
	 *            the Bug to create
	 * @return the bug id, if the creation of the bug was sucessful,
	 *         <code>null</code> otherwise.
	 */
	public Integer create(final Bug bug) {

		try {
			final Object result = this.exec("Bug.create", "product", bug.getProduct(), "component",
					bug.getComponent(), "summary", bug.getSummary(), "version", bug.getVersion(),
					"description", bug.getDescription());
			if (result instanceof Map) {
				Object id = ((Map<?, ?>) result).get("id");
				if (id instanceof Number) {
					return ((Number) id).intValue();
				}
			}
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
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
	 * Gibt den Wert der Eigenschaft {@link BugzillaClient#password} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link BugzillaClient#password}
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Gibt den Wert der Eigenschaft {@link BugzillaClient#url} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link BugzillaClient#url}
	 */
	public java.net.URL getURL() {
		return this.url;
	}

	/**
	 * Gibt den Wert der Eigenschaft {@link BugzillaClient#userName} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link BugzillaClient#userName}
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
	 * @return
	 */
	public boolean isConnected() {
		return this.connected;
	}

	/**
	 * Setzt den Wert der Eigenschaft {@link BugzillaClient#password}.
	 * 
	 * @param password
	 *            der Wert der Eigenschaft {@link BugzillaClient#password}
	 */
	public void setPassword(final String password) {
		if (this.isConnected()) {
			throw new IllegalStateException(
					"A new username or password can onmly be set when the client is disconnected");
		}
		final String oldValue = this.password;
		this.password = password;
		pcs.firePropertyChange(PASSWORD, oldValue, this.password);
	}

	// ////////TEST

	// public void getBug1() {
	// try {
	// final Map map = (Map) exec("Bug.get", "ids", "1");
	// for (final Object key : map.keySet()) {
	// final Object[] objs = (Object[]) map.get(key);
	// for (Object object : objs) {
	// System.out.println(object);
	// }
	// }
	// } catch (XmlRpcException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * Setzt den Wert der Eigenschaft {@link BugzillaClient#userName}.
	 * 
	 * @param userName
	 *            der Wert der Eigenschaft {@link BugzillaClient#userName}
	 */
	public void setUserName(final String userName) {
		if (this.isConnected()) {
			throw new IllegalStateException(
					"A new username or password can onmly be set when the client is disconnected");
		}
		final String oldValue = this.userName;
		this.userName = userName;
		pcs.firePropertyChange(USER_NAME, oldValue, this.userName);
	}
}
