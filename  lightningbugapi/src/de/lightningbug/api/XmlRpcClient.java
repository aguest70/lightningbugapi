/**
 * 
 */
package de.lightningbug.api;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

/**
 * A cookie-aware version of the {@link org.apache.xmlrpc.client.XmlRpcClient}.
 * 
 * @author Sebastian Kirchner
 */
class XmlRpcClient extends org.apache.xmlrpc.client.XmlRpcClient {

	/**
	 * Konstante des Namens der Eigenschaft {@link XmlRpcClient#cookieStore}
	 */
	public static final String COOKIE_STORE = "cookieStore"; //$NON-NLS-1$

	private CookieStore cookieStore = new CookieStore();

	/**
	 * Gibt den Wert der Eigenschaft {@link XmlRpcClient#cookieStore} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link XmlRpcClient#cookieStore}
	 */
	public CookieStore getCookieStore() {
		return this.cookieStore;
	}

	public XmlRpcClient(final URL apiURL) {
		super();

		final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(apiURL);

		final XmlRpcCommonsTransportFactory factory = new XmlRpcCookiesTransportFactory(this,
				this.getCookieStore());
		this.setTransportFactory(factory);
		this.setConfig(config);
	}

}
