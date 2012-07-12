/**
 * 
 */
package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;

/**
 * Object representing the Severity of a {@link Bug}
 * 
 * @author Sebastian Kirchner
 * 
 */
public class Severity implements BugzillaObject, Comparable<Severity> {

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	private String name;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Severity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Severity(String name) {
		super();
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Severity version) {
		if (version == null) {
			return 1;
		}
		if (this.getName() == null) {
			if (version.getName() == null) {
				return 0;
			}
			return -1;
		}
		return this.getName().compareTo(version.getName());
	}

	/**
	 * @return name of the version {@link Severity#name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Setter for the name of the version
	 * 
	 * @param name
	 *            the name of the version {@link Severity#name}
	 */
	public void setName(final String name) {
		final String oldValue = this.name;
		this.name = name;
		pcs.firePropertyChange(NAME, oldValue, this.name);
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	@Override
	public String toString()
	{
	    final String TAB = ", ";
	    final StringBuilder retValue = new StringBuilder();
	    retValue.append(this.getClass().getName()).append("[")
	        .append("name=").append(this.name == null ? "null" : this.name.toString()).append(TAB)
	        .append("pcs=").append(this.pcs == null ? "null" : this.pcs.toString()).append(TAB)
	        .append("]");
	    return retValue.toString();
	}

}