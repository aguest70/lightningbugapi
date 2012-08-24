package de.lightningbug.api.service;

import de.lightningbug.api.BugzillaClient;

public abstract class AbstractService {

	protected BugzillaClient client = null;

	/**
	 * @param client
	 *            the client used by the service to query information from the
	 *            bugzilla instance
	 */
	public AbstractService(final BugzillaClient client) {
		if(client == null){
			throw new IllegalArgumentException("Paramter <client> must not be mull"); //$NON-NLS-1$
		}
		this.client = client;
	}

}