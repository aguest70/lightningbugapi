package de.lightningbug.api.domain;

import java.beans.PropertyChangeSupport;

/**
 * Object of this class hold information of an user account in Bugzilla.
 * 
 * @author Sebastian Kirchner
 * 
 */
public class User {

	/**
	 * Constant for the name of the property {@link User#email}
	 */
	public static final String E_MAIL = "email"; //$NON-NLS-1$

	/**
	 * Constant for the name of the property {@link User#loginName}
	 */
	public static final String LOGIN_NAME = "loginName"; //$NON-NLS-1$

	/**
	 * Constant for the name of the property {@link User#realName}
	 */
	public static final String REAL_NAME = "realName"; //$NON-NLS-1$

	/**
	 * Constant for the name of the property {@link User#id}
	 */
	public static final String upper_prop_name = "id"; //$NON-NLS-1$

	private String email;

	private Integer id;

	private String loginName;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private String realName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj){
			return true;
		}
		if(obj == null){
			return false;
		}
		if(this.getClass() != obj.getClass()){
			return false;
		}
		final User other = (User) obj;
		if(this.email == null){
			if(other.email != null){
				return false;
			}
		}else if(!this.email.equals(other.email)){
			return false;
		}
		if(this.id == null){
			if(other.id != null){
				return false;
			}
		}else if(!this.id.equals(other.id)){
			return false;
		}
		if(this.loginName == null){
			if(other.loginName != null){
				return false;
			}
		}else if(!this.loginName.equals(other.loginName)){
			return false;
		}
		if(this.realName == null){
			if(other.realName != null){
				return false;
			}
		}else if(!this.realName.equals(other.realName)){
			return false;
		}
		return true;
	}

	/**
	 * Getter for the email address of the user. Must not be <code>null</code>
	 * 
	 * @return The email address of the user. {@link User#email}
	 */
	public String getEMail() {
		return this.email;
	}

	/**
	 * Getter for the unique integer ID that Bugzilla uses to represent this user. Even if the
	 * user's login
	 * name changes, this will not change.
	 * 
	 * @return The unique integer ID that Bugzilla uses to represent this user {@link User#id}
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Getter for the login name of the user. Note that in some situations this is different than
	 * their {@link User#email}.
	 * 
	 * @return The login name of the user.
	 */
	public String getLoginName() {
		return this.loginName;
	}

	/**
	 * Getter for the actual name of the user. May be blank.
	 * 
	 * @return The actual name of the user {@link User#realName}
	 */
	public String getRealName() {
		return this.realName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result + ((this.loginName == null) ? 0 : this.loginName.hashCode());
		result = prime * result + ((this.realName == null) ? 0 : this.realName.hashCode());
		return result;
	}

	/**
	 * Setter for the email address of the user. {@link User#email}.
	 * 
	 * @param email
	 *            The email address of the user.{@link User#email}
	 */
	public void setEMail(final String email) {
		final String oldValue = this.email;
		this.email = email;
		this.pcs.firePropertyChange(E_MAIL, oldValue, this.email);
	}

	/**
	 * Setter for the unique integer ID that Bugzilla uses to represent this user. Even if the
	 * user's login
	 * name changes, this will not change.
	 * 
	 * @param id
	 *            The unique integer ID that Bugzilla uses to represent this user {@link User#id}
	 */
	public void setId(final Integer id) {
		final Integer oldValue = this.id;
		this.id = id;
		this.pcs.firePropertyChange(upper_prop_name, oldValue, this.id);
	}

	/**
	 * Setter for the login name of the user. Note that in some situations this is different than
	 * their {@link User#email}.
	 * 
	 * @param loginName
	 *            The login name of the user
	 */
	public void setLoginName(final String loginName) {
		final String oldValue = this.loginName;
		this.loginName = loginName;
		this.pcs.firePropertyChange(LOGIN_NAME, oldValue, this.loginName);
	}

	/**
	 * Setter for the actual name of the user. May be blank. {@link User#realName}.
	 * 
	 * @param realName
	 *            The actual name of the user. {@link User#realName}
	 */
	public void setRealName(final String realName) {
		final String oldValue = this.realName;
		this.realName = realName;
		this.pcs.firePropertyChange(REAL_NAME, oldValue, this.realName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [id=" + this.id + ", loginName=" + this.loginName + ", realName=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ this.realName + ", email=" + this.email + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
