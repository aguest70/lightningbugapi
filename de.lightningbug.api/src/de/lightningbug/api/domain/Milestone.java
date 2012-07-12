/**
 * 
 */
package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * TODO
 * 
 * @author Sebastian Kirchner
 * 
 */
public class Milestone implements BugzillaObject, Comparable<Milestone> {

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	private String name;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Milestone() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Milestone(String name) {
		super();
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Milestone milestone) {
		if (milestone == null) {
			return 1;
		}
		if (this.getName() == null) {
			if (milestone.getName() == null) {
				return 0;
			}
			return -1;
		}
		return this.getName().compareTo(milestone.getName());
	}

	/**
	 * @return name of the version {@link Milestone#name}
	 */
	@XmlAttribute
	public String getName() {
		return this.name;
	}

	/**
	 * Setter for the name of the version
	 * 
	 * @param name
	 *            the name of the version {@link Milestone#name}
	 */
	public void setName(final String name) {
		final String oldValue = this.name;
		this.name = name;
		this.pcs.firePropertyChange(NAME, oldValue, this.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Milestone [name=" + this.name + ", pcs=" + this.pcs + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
	}
}
