/**
 * 
 */
package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAttribute;

import de.lightningbug.api.BugzillaClient;

/**
 * An object representing a software component of a product
 * <p>
 * Components are product-wide unique compared by their name.
 * </p>
 * 
 * @author Sebastian Kirchner
 * 
 * @see Bug#getComponent()
 * @see Product#getComponents()
 * @see BugzillaClient#getAll(Product)
 */
public class Component implements BugzillaObject, Comparable<Component> {
	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	private String name;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public Component(String name) {
		super();
		this.name = name;
	}

	public Component() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return name of the component {@link Component#name}
	 */
	@XmlAttribute
	public String getName() {
		return this.name;
	}

	/**
	 * Setter for the name of the component
	 * 
	 * @param name
	 *            the name of the component {@link Component#name}
	 */
	public void setName(final String name) {
		final String oldValue = this.name;
		this.name = name;
		pcs.firePropertyChange(NAME, oldValue, this.name);
	}

	@Override
	public int compareTo(final Component o) {
		if (o == null) {
			return 1;
		}
		if (this.getName() == null) {
			if (o.getName() == null) {
				return 0;
			}
			return -1;
		}
		return this.getName().compareTo(o.getName());
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + NAME + "=\"" + this.getName() + "\"]";
	}

}
