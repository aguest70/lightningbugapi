package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;
import java.text.MessageFormat;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import de.lightningbug.api.BugzillaClient;

/**
 * Instances of the class represent a Bugzilla bug.
 * 
 * @author Sebastian Kirchner
 * 
 * @see BugzillaClient#create(IBugzillaObject)
 */
@XmlRootElement
public class Bug implements Comparable<Bug> {

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String COMPONENT = "component"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String ID = "id"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String PRODUCT = "product"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#severity}
	 */
	public static final String SEVERITY = "severity"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String SUMMARY = "summary"; //$NON-NLS-1$

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String VERSION = "version"; //$NON-NLS-1$

	/**
	 * Konstante des Namens der Eigenschaft {@link Bug#dependsOn}
	 */
	public static final String DEPENDS_ON = "dependsOn"; //$NON-NLS-1$

	private Set<Bug> dependsOn;

	/**
	 * Gibt den Wert der Eigenschaft {@link Bug#dependsOn} zurück.
	 * 
	 * @return der Wert der Eigenschaft {@link Bug#dependsOn}
	 */
	public Set<Bug> getDependsOn() {
		return this.dependsOn;
	}

	/**
	 * Setzt den Wert der Eigenschaft {@link Bug#dependsOn}.
	 * 
	 * @param dependsOn
	 *            der Wert der Eigenschaft {@link Bug#dependsOn}
	 */
	public void setDependsOn(final Set<Bug> dependsOn) {
		final Set<Bug> oldValue = this.dependsOn;
		this.dependsOn = dependsOn;
	}

	/**
	 * Constant for the name of the appropriate bean property
	 */
	public static final String ESTIMATED_TIME = "estimatedTime"; //$NON-NLS-1$

	private String component;

	private String description;

	private Integer id;

	private Double estimatedTime;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Product product;

	private String severity;

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
	 * @return The id of the bug
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * @return The time estimated for the completion of this bug
	 */
	public Double getEstimatedTime() {
		return this.estimatedTime;
	}

	/**
	 * @return the product the bug is being filed against.
	 */
	public Product getProduct() {
		return this.product;
	}

	/**
	 * @return the severity of the bug
	 */
	public String getSeverity() {
		return this.severity;
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
	 * Setter for the severity of this bug. All legal values for this field can
	 * be queried using {@link BugzillaClient#getAll(Class)}.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * 
	 * 
	 * 
	 * 
	 * final List&lt;Severity&gt; severities = client.getAll(Severity.class);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param severity
	 *            the severity of the bug
	 */
	public void setSeverity(final String severity) {
		final String oldValue = this.severity;
		this.severity = severity;
		pcs.firePropertyChange(SEVERITY, oldValue, this.severity);
	}

	/**
	 * @param id
	 *            The id of the bug
	 */
	public void setId(final Integer id) {
		final Integer oldValue = this.id;
		this.id = id;
		this.pcs.firePropertyChange(ID, oldValue, this.id);
	}

	/**
	 * @param estimatedTime
	 *            The estimated time of the bug
	 */
	public void setEstimatedTime(final Double estimatedTime) {
		final Object oldValue = this.estimatedTime;
		this.estimatedTime = estimatedTime;
		this.pcs.firePropertyChange(ESTIMATED_TIME, oldValue, this.estimatedTime);
	}

	/**
	 * <p>
	 * All registered products ca be queried by using the method
	 * {@link BugzillaClient#getAll(Class)}.
	 * </p>
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * 
	 * 
	 * 
	 * 
	 * final List&lt;Product&gt; products = client.getAll(Product.class);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * If the version field ({@link Bug#setVersion(Version)}) of this bug has been set before and
	 * the given product doesn't ship in the predefined version, the property {@link Bug#version} is
	 * set to <code>null</code>.
	 * </p>
	 * 
	 * @param product
	 *            the product the bug is being filed against.
	 */
	public void setProduct(final Product product) {
		final Product oldValue = this.product;
		this.product = product;
		pcs.firePropertyChange(PRODUCT, oldValue, this.product);
		if(this.getVersion() != null && product != null
				&& !product.getVersions().contains(this.getVersion())){
			this.setVersion(null);
		}
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
	 * Setter for the version of the product ({@link Bug#product}) this bug is
	 * filed against.
	 * 
	 * @param version
	 *            The version of the product the bug was found in.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given version doesn't match a version of the
	 *             previously defined product ({@link Bug#setProduct(Product)})
	 */
	public void setVersion(final String version) {
		// version must match one of the versions of the associated product if
		// product and version is not null
		if(this.getProduct() != null && this.version != null
				&& !this.getProduct().getVersions().contains(version)){
			final String pattern = "The product {0} doesn't ship in the version {1}";
			final String message = MessageFormat.format(pattern, this.getProduct().getName(),
					version);
			throw new IllegalArgumentException(message);
		}
		final Object oldValue = this.version;
		this.version = version;
		pcs.firePropertyChange(VERSION, oldValue, this.version);
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = ", ";
		final StringBuilder retValue = new StringBuilder();
		retValue.append(this.getClass().getName()).append("[").append("component=")
				.append(this.component == null ? "null" : this.component.toString()).append(TAB)
				.append("description=")
				.append(this.description == null ? "null" : this.description.toString())
				.append(TAB).append("id=").append(this.id == null ? "null" : this.id.toString())
				.append(TAB).append("pcs=").append(this.pcs == null ? "null" : this.pcs.toString())
				.append(TAB).append("product=")
				.append(this.product == null ? "null" : this.product.toString()).append(TAB)
				.append("severity=")
				.append(this.severity == null ? "null" : this.severity.toString()).append(TAB)
				.append("summary=").append(this.summary == null ? "null" : this.summary.toString())
				.append(TAB).append("version=")
				.append(this.version == null ? "null" : this.version.toString()).append(TAB)
				.append("]");
		return retValue.toString();
	}

	@Override
	public int compareTo(final Bug bug) {
		if(bug == null)
			return 0;
		if(this.getId() == null){
			if(bug.getId() == null){
				return 0;
			}
			return -1;
		}
		return this.getId().compareTo(bug.getId());
	}

}
