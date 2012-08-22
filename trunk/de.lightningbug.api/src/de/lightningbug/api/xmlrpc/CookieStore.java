/**
 * 
 */
package de.lightningbug.api.xmlrpc;

import java.net.HttpCookie;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A store to manage HTTP cookies (to retreive, cache and apply them).
 * 
 * @author Sebastian Kirchner
 * 
 */
public class CookieStore extends HashSet<HttpCookie> {

	/**
	 * Create a cookies strore, that can be used to manage cookies of an HTTP
	 * connection.
	 */
	public CookieStore() {
		super();
	}

	/**
	 * @param connection
	 * @return
	 */
	public static Set<HttpCookie> retrieveCoockies(final URLConnection connection) {
		return retrieveCoockies(connection, null);
	}

	/**
	 * @param connection
	 * @param cookieDomain
	 * @return an empty set, if no cookies are found in the connection.
	 */
	public static Set<HttpCookie> retrieveCoockies(final URLConnection connection,
			final String cookieDomain) {

		final Map<String, List<String>> headerFields = connection.getHeaderFields();
		final List<String> cookieHeaders = headerFields.get("Set-Cookie");

		final HashSet<HttpCookie> cookies = new HashSet<HttpCookie>();

		if (cookieHeaders == null) {
			return cookies;
		}
		for (final String cookieHeader : cookieHeaders) {

			// Implementation of HttpCookie cannot handle empty params :(
			final String cleanCoockieHeader = cookieHeader.replace("; HttpOnly", "");
			final List<HttpCookie> cookieList = HttpCookie.parse("Set-Cookie: "
					+ cleanCoockieHeader);
			if (cookieList == null) {
				return cookies;
			}
			for (final HttpCookie cookie : cookieList) {

				final String domain = cookie.getDomain();
				if (cookieDomain == null) {
					cookies.add(cookie);
				} else if (domain != null && HttpCookie.domainMatches(domain, cookieDomain)) {
					// a cookie domain has been specified and the domain of the
					// cookie matches
					cookies.add(cookie);
				}
				// not from the spec domain, then skip
			}
		}
		return cookies;
	}

	/**
	 * Applies the registered cookies to the connection.
	 * 
	 * @param connection
	 *            the HTTP connection the given cookies should be applied to
	 * @param cookies
	 *            the set of cookie to apply
	 */
	public static void applyCookies(final URLConnection connection, final Set<HttpCookie> cookies) {

		final StringBuffer rc = new StringBuffer();
		for (final HttpCookie cookie : cookies) {

			if (rc.length() > 0) {
				rc.append(";");
			}
			rc.append(cookie.toString());
		}
		if (rc.length() > 0) {
			connection.setRequestProperty("Cookie", rc.toString());
		}
	}
}
