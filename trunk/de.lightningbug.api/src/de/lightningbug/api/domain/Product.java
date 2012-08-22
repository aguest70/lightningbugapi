/**
 * 
 */
package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * An Object representing a Bugzilla product
 * 
 * @author Sebastian Kirchner
 * 
 * @see BugzillaClient.get(Product);
 * @see Bug#setProduct(Product);
 * 
 */
public class Product {

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
	public static final String MILESTONES = "milestones"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String NAME = "name"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Product#severities}
	 */
	public static final String SEVERITIES = "severities"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String VERSIONS = "versions"; //$NON-NLS-1$

	private Set<String> components;

	private Integer id;

	private Set<String> milestones;

	private String name;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Set<String> severities;

	private Set<String> versions;

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
	@XmlElement(name = "component")
	public Set<String> getComponents() {
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
	 * @return the milestones of the product {@link Product#milestones}
	 */
	@XmlElement(name = "milestone")
	public Set<String> getMilestones() {
		return this.milestones;
	}

	/**
	 * @return name of the product {@link Product#name}
	 */
	@XmlAttribute
	public String getName() {
		return this.name;
	}

	/**
	 * Gibt den Wert der Eigenschaft {@link Product#severities} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link Product#severities}
	 */
	public Set<String> getSeverities() {
		return this.severities;
	}

	/**
	 * @return the versions of the product {@link Product#versions}
	 */
	@XmlElement(name = "version")
	public Set<String> getVersions() {
		return this.versions;
	}

	/**
	 * Setzt den Wert der Eigenschaft {@link Product#components}.
	 * 
	 * @param components
	 *            der Wert der Eigenschaft {@link Product#components}
	 */
	public void setComponents(final Set<String> components) {
		final Set<String> oldValue = this.components;
		this.components = components;
		this.pcs.firePropertyChange(COMPONENTS, oldValue, this.components);
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
		this.pcs.firePropertyChange(ID, oldValue, this.id);
	}

	/**
	 * Setter for the milestones of the product {@link Product#milestones}
	 * 
	 * @param milestones
	 *            the milestones of the product {@link Product#milestones}
	 */
	public void setMilestones(final Set<String> milestones) {
		final Set<String> oldValue = this.milestones;
		this.milestones = milestones;
		this.pcs.firePropertyChange(VERSIONS, oldValue, this.milestones);
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
		this.pcs.firePropertyChange(NAME, oldValue, this.name);
	}

	/**
	 * Setzt den Wert der Eigenschaft {@link Product#severities}.
	 * 
	 * @param severities
	 *            der Wert der Eigenschaft {@link Product#severities}
	 */
	public void setSeverities(final Set<String> severities) {
		final Set<String> oldValue = this.severities;
		this.severities = severities;
		this.pcs.firePropertyChange(SEVERITIES, oldValue, this.severities);
	}

	/**
	 * Setter for the versions of the product {@link Product#milestones}
	 * 
	 * @param versions
	 *            the versions of the product {@link Product#versions}
	 */
	public void setVersions(final Set<String> versions) {
		final Set<String> oldValue = this.versions;
		this.versions = versions;
		this.pcs.firePropertyChange(VERSIONS, oldValue, this.versions);
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 * 
	 * @return a <code>String</code> representation
	 *         of this object.
	 */
	@Override
	public String toString() {
		final String TAB = ", ";
		final StringBuilder retValue = new StringBuilder();
		retValue.append(this.getClass().getName()).append("[").append("components=")
				.append(this.components == null ? "null" : this.components.toString()).append(TAB)
				.append("id=").append(this.id == null ? "null" : this.id.toString()).append(TAB)
				.append("name=").append(this.name == null ? "null" : this.name.toString())
				.append(TAB).append("pcs=").append(this.pcs == null ? "null" : this.pcs.toString())
				.append(TAB).append("versions=")
				.append(this.versions == null ? "null" : this.versions.toString()).append(TAB)
				.append("]");
		return retValue.toString();
	}

}
