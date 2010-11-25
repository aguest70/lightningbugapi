/**
 * 
 */
package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;

/**
 * @author Sebastian Kirchner
 * 
 */
public class Bug {

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#component}
	 */
	public static final String COMPONENT = "component"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#description}
	 */
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#product}
	 */
	public static final String PRODUCT = "product"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#summary}
	 */
	public static final String SUMMARY = "summary"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#version}
	 */
	public static final String VERSION = "version"; //$NON-NLS-1$

	private String component;

	private String description;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private String product;

	private String summary;

	private String version;

	/**
	 * @return The name of a component in the product
	 */
	public String getComponent() {
		return this.component;
	}

	/**
	 * @return The initial description for this bug. Some Bugzilla installations
	 *         require this to not be blank.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return The name of the product the bug is being filed against.
	 */
	public String getProduct() {
		return this.product;
	}

	/**
	 * @return A brief description of the bug being filed.
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * @return The version of the product the bug was found in.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * @param component
	 *            The name of a component in the product
	 */
	public void setComponent(final String component) {
		final String oldValue = this.component;
		this.component = component;
		pcs.firePropertyChange(COMPONENT, oldValue, this.component);
	}

	/**
	 * @param description
	 *            The initial description for this bug. Some Bugzilla
	 *            installations require this to not be blank.
	 */
	public void setDescription(final String description) {
		final String oldValue = this.description;
		this.description = description;
		pcs.firePropertyChange(DESCRIPTION, oldValue, this.description);
	}

	/**
	 * @param product
	 *            The name of the product the bug is being filed against.
	 */
	public void setProduct(final String product) {
		final String oldValue = this.product;
		this.product = product;
		pcs.firePropertyChange(PRODUCT, oldValue, this.product);
	}

	/**
	 * @param summary
	 *            A brief description of the bug being filed.
	 */
	public void setSummary(final String summary) {
		final String oldValue = this.summary;
		this.summary = summary;
		pcs.firePropertyChange(SUMMARY, oldValue, this.summary);
	}

	/**
	 * @param version
	 *            The version of the product the bug was found in.
	 */
	public void setVersion(final String version) {
		final String oldValue = this.version;
		this.version = version;
		pcs.firePropertyChange(VERSION, oldValue, this.version);
	}

}
