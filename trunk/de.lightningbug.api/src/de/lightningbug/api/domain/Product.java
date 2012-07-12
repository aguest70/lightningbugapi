/**
 * 
 */
package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An Object representing a Bugzilla product
 * 
 * @author Sebastian Kirchner
 * 
 * @see BugzillaClient.get(Product);
 * @see Bug#setProduct(Product);
 * 
 */
public class Product implements BugzillaObject {

	/**
	 * Konstante des Namens der Eigenschaft {@link Product#components}
	 */
	public static final String COMPONENTS = "components"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String ID = "id"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String VERSIONS = "versions"; //$NON-NLS-1$
	
	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String MILESTONES = "milestones"; //$NON-NLS-1$

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private Set<Component> components;

	private Integer id;

	private String name;

	private Set<Version> versions;
	
	private Set<Milestone> milestones;

	/**
	 * Constructs a Bugzilla {@link Product}
	 */
	public Product() {
		super();
	}

	/**
	 * Constructs a Bugzilla {@link Product}
	 * 
	 * @param id
	 *            the id of the product
	 * @param name
	 *            the name of the product
	 */
	public Product(Integer id, String name) {
		this();
		this.id = id;
		this.name = name;
	}

	/**
	 * Gibt den Wert der Eigenschaft {@link Product#components} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link Product#components}
	 */
	@XmlElement(name="component")
	public Set<Component> getComponents() {
		return this.components;
	}

	/**
	 * Gibt den Wert der Eigenschaft {@link Product#id} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link Product#id}
	 */
	@XmlAttribute
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return name of the product {@link Product#name}
	 */
	@XmlAttribute
	public String getName() {
		return this.name;
	}

	/**
	 * @return the versions of the product {@link Product#versions}
	 */
	@XmlElement(name="version")
	public Set<Version> getVersions() {
		return this.versions;
	}
	
	/**
	 * @return the milestones of the product {@link Product#milestones}
	 */
	@XmlElement(name="milestone")
	public Set<Milestone> getMilestones() {
		return this.milestones;
	}

	/**
	 * Setzt den Wert der Eigenschaft {@link Product#components}.
	 * 
	 * @param components
	 *            der Wert der Eigenschaft {@link Product#components}
	 */
	public void setComponents(final Set<Component> components) {
		final Set<Component> oldValue = this.components;
		this.components = components;
		pcs.firePropertyChange(COMPONENTS, oldValue, this.components);
	}

	/**
	 * Setzt den Wert der Eigenschaft {@link Product#id}.
	 * 
	 * @param id
	 *            der Wert der Eigenschaft {@link Product#id}
	 */
	public void setId(final Integer id) {
		final Integer oldValue = this.id;
		this.id = id;
		pcs.firePropertyChange(ID, oldValue, this.id);
	}

	/**
	 * Setter for the name of the product
	 * 
	 * @param name
	 *            the name of the product {@link Product#name}
	 */
	public void setName(final String name) {
		final String oldValue = this.name;
		this.name = name;
		pcs.firePropertyChange(NAME, oldValue, this.name);
	}

	/**
	 * Setter for the milestones of the product {@link Product#milestones}
	 * 
	 * @param milestones
	 *            the milestones of the product {@link Product#milestones}
	 */
	public void setMilestones(final Set<Milestone> milestones) {
		final Set<Milestone> oldValue = this.milestones;
		this.milestones = milestones;
		this.pcs.firePropertyChange(VERSIONS, oldValue, this.milestones);
	}

	/**
	 * Setter for the versions of the product {@link Product#milestones}
	 * 
	 * @param versions
	 *            the versions of the product {@link Product#versions}
	 */
	public void setVersions(final Set<Version> versions) {
		final Set<Version> oldValue = this.versions;
		this.versions = versions;
		this.pcs.firePropertyChange(VERSIONS, oldValue, this.versions);
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
	        .append("components=").append(this.components == null ? "null" : this.components.toString()).append(TAB)
	        .append("id=").append(this.id == null ? "null" : this.id.toString()).append(TAB)
	        .append("name=").append(this.name == null ? "null" : this.name.toString()).append(TAB)
	        .append("pcs=").append(this.pcs == null ? "null" : this.pcs.toString()).append(TAB)
	        .append("versions=").append(this.versions == null ? "null" : this.versions.toString()).append(TAB)
	        .append("]");
	    return retValue.toString();
	}

}
