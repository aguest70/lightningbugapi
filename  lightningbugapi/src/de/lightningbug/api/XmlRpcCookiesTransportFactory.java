package de.lightningbug.api;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.client.XmlRpcSunHttpTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;

/**
 * A cookie-aware implementation of an
 * {@link org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory}, that uses a
 * {@link CookieStore} to handle login information over application lifecycle.
 * The cookie store has to be provided when constructing the object.
 * 
 * @author Sebastian Kirchner
 * 
 * @see BugzillaClient
 * @see CookieStore
 * 
 */
class XmlRpcCookiesTransportFactory extends XmlRpcCommonsTransportFactory {

	private CookieStore cookieStore = null;

	private XmlRpcClient xmlRpcClient = null;

	/**
	 * @param xmlRpcClient
	 *            the client, which is controlling the factory.
	 * @param cookieStore
	 *            store, that should be used to manage the cookies (eg. login
	 *            cookie) of the url connection over the application runtime
	 */
	public XmlRpcCookiesTransportFactory(final XmlRpcClient xmlRpcClient,
			final CookieStore cookieStore) {
		super(xmlRpcClient);
		this.xmlRpcClient = xmlRpcClient;
		this.cookieStore = cookieStore;
		// Don't remember what this was for
		this.setHttpClient(new HttpClient());
	}

	@Override
	public XmlRpcTransport getTransport() {
		final CookieStore cookieStore = this.cookieStore;
		return new XmlRpcSunHttpTransport(this.xmlRpcClient) {

			private URLConnection connection;

			@Override
			protected void close() throws XmlRpcClientException {
				cookieStore.addAll(CookieStore.retrieveCoockies(this.connection));
			}

			@Override
			protected void initHttpHeaders(final XmlRpcRequest pRequest) {
				try {
					super.initHttpHeaders(pRequest);
				} catch (XmlRpcClientException e) {
					e.printStackTrace();
				}
				CookieStore.applyCookies(this.connection, cookieStore);
			}

			@Override
			protected URLConnection newURLConnection(final URL url) throws IOException {
				// cache the connection to retrieve or apply cookies later on
				this.connection = super.newURLConnection(url);
				return this.connection;
			}
		};
	}
}
