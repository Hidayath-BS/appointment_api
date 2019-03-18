package org.zerhusen.model.security;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



@Entity
@Table(name = "ams_admin_user")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "mobilenumber", length = 50, unique = true)
    @NotNull
    @Size(min = 4, max = 50)
    private String mobilenumber;

    @Column(name = "password", length = 100)
    @NotNull
    @Size(min = 4, max = 100)
    private String password;
    
    @Column(name="approved")
    private Boolean approved;

    @Column(name = "username", length = 50)
    private String username;

	@OneToOne
	@JoinColumn(name = "city_id")
	private Ak_city city_id;
	
	
	@OneToOne
	@JoinColumn(name = "state_id")
	private Ak_state state_id;
    
   
	@Column(name="address_line_1")
	private String address_line1;
	
	@Column(name="address_line_2")
	private String address_line2;
	
	@Column(name="taluk")
	private String taluk;
	
	@Column(name="pincode")
	private String pincode;
	

	@Column(name="target_amount")
	private Double targetAmount;
	
	@Temporal(TemporalType.DATE)
	private Date targetFrom;
	
	@Temporal(TemporalType.DATE)
	private Date targetTo;
	
    @Column(name = "ENABLED")
    private Boolean enabled;

    @Column(name="email")
    private String email;

    public String getEmail() {
    return email;
    }

    public void setEmail(String email) {
    this.email = email;
    }

	@Column(name = "LASTPASSWORDRESETDATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPasswordResetDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private Collection<Authority> authorities;

    
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}



	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<Authority> authorities) {
        this.authorities = authorities;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }
    
    

	public String getAddress_line1() {
		return address_line1;
	}

	public void setAddress_line1(String address_line1) {
		this.address_line1 = address_line1;
	}

	public String getAddress_line2() {
		return address_line2;
	}

	public void setAddress_line2(String address_line2) {
		this.address_line2 = address_line2;
	}

	public String getTaluk() {
		return taluk;
	}

	public void setTaluk(String taluk) {
		this.taluk = taluk;
	}

	
	public Ak_city getCity_id() {
		return city_id;
	}

	public void setCity_id(Ak_city city_id) {
		this.city_id = city_id;
	}

	public Ak_state getState_id() {
		return state_id;
	}

	public void setState_id(Ak_state state_id) {
		this.state_id = state_id;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	


	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public User() {
		
	}


	public User(@NotNull @Size(min = 4, max = 50) String mobilenumber,
			@NotNull @Size(min = 4, max = 100) String password, Boolean approved, String username, String address_line1,
			String address_line2, String taluk, String pincode, Double targetAmount, Date targetFrom, Date targetTo,
			Boolean enabled, String email, Date lastPasswordResetDate) {
			super();
			this.mobilenumber = mobilenumber;
			this.password = password;
			this.approved = approved;
			this.username = username;
			this.address_line1 = address_line1;
			this.address_line2 = address_line2;
			this.taluk = taluk;
			this.pincode = pincode;
			this.targetAmount = targetAmount;
			this.targetFrom = targetFrom;
			this.targetTo = targetTo;
			this.enabled = enabled;
			this.email = email;
			this.lastPasswordResetDate = lastPasswordResetDate;
			}
	
	
	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(Double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public Date getTargetFrom() {
		return targetFrom;
	}

	public void setTargetFrom(Date targetFrom) {
		this.targetFrom = targetFrom;
	}

	public Date getTargetTo() {
		return targetTo;
	}

	public void setTargetTo(Date targetTo) {
		this.targetTo = targetTo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address_line1 == null) ? 0 : address_line1.hashCode());
		result = prime * result + ((address_line2 == null) ? 0 : address_line2.hashCode());
		result = prime * result + ((approved == null) ? 0 : approved.hashCode());
		result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
		result = prime * result + ((city_id == null) ? 0 : city_id.hashCode());
		result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastPasswordResetDate == null) ? 0 : lastPasswordResetDate.hashCode());
		result = prime * result + ((mobilenumber == null) ? 0 : mobilenumber.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((pincode == null) ? 0 : pincode.hashCode());
		result = prime * result + ((state_id == null) ? 0 : state_id.hashCode());
		result = prime * result + ((taluk == null) ? 0 : taluk.hashCode());
		result = prime * result + ((targetAmount == null) ? 0 : targetAmount.hashCode());
		result = prime * result + ((targetFrom == null) ? 0 : targetFrom.hashCode());
		result = prime * result + ((targetTo == null) ? 0 : targetTo.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (address_line1 == null) {
			if (other.address_line1 != null)
				return false;
		} else if (!address_line1.equals(other.address_line1))
			return false;
		if (address_line2 == null) {
			if (other.address_line2 != null)
				return false;
		} else if (!address_line2.equals(other.address_line2))
			return false;
		if (approved == null) {
			if (other.approved != null)
				return false;
		} else if (!approved.equals(other.approved))
			return false;
		if (authorities == null) {
			if (other.authorities != null)
				return false;
		} else if (!authorities.equals(other.authorities))
			return false;
		if (city_id == null) {
			if (other.city_id != null)
				return false;
		} else if (!city_id.equals(other.city_id))
			return false;
		if (enabled == null) {
			if (other.enabled != null)
				return false;
		} else if (!enabled.equals(other.enabled))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastPasswordResetDate == null) {
			if (other.lastPasswordResetDate != null)
				return false;
		} else if (!lastPasswordResetDate.equals(other.lastPasswordResetDate))
			return false;
		if (mobilenumber == null) {
			if (other.mobilenumber != null)
				return false;
		} else if (!mobilenumber.equals(other.mobilenumber))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (pincode == null) {
			if (other.pincode != null)
				return false;
		} else if (!pincode.equals(other.pincode))
			return false;
		if (state_id == null) {
			if (other.state_id != null)
				return false;
		} else if (!state_id.equals(other.state_id))
			return false;
		if (taluk == null) {
			if (other.taluk != null)
				return false;
		} else if (!taluk.equals(other.taluk))
			return false;
		if (targetAmount == null) {
			if (other.targetAmount != null)
				return false;
		} else if (!targetAmount.equals(other.targetAmount))
			return false;
		if (targetFrom == null) {
			if (other.targetFrom != null)
				return false;
		} else if (!targetFrom.equals(other.targetFrom))
			return false;
		if (targetTo == null) {
			if (other.targetTo != null)
				return false;
		} else if (!targetTo.equals(other.targetTo))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", mobilenumber=" + mobilenumber + ", password=" + password + ", approved=" + approved
				+ ", username=" + username + ", city_id=" + city_id + ", state_id=" + state_id + ", address_line1="
				+ address_line1 + ", address_line2=" + address_line2 + ", taluk=" + taluk + ", pincode=" + pincode
				+ ", targetAmount=" + targetAmount + ", targetFrom=" + targetFrom + ", targetTo=" + targetTo
				+ ", enabled=" + enabled + ", lastPasswordResetDate=" + lastPasswordResetDate + ", authorities="
				+ authorities + "]";
	}

	    
    
    
    
}