package org.zerhusen.model.ams;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.zerhusen.model.security.User;

@Entity
@Table(name="ams_doctor_suggestion")
public class AmsDoctorSuggestion {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name="doctor")
	private User doctor;
	
	@JoinColumn(name="seciality")
	private String speciality;
	
	@Column(name="active")
	private boolean active;

	public AmsDoctorSuggestion() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public AmsDoctorSuggestion(String speciality, boolean active) {
		super();
		this.speciality = speciality;
		this.active = active;
	}

	@Override
	public String toString() {
		return "AmsDoctorSuggestion [id=" + id + ", doctor=" + doctor + ", speciality=" + speciality + ", active="
				+ active + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((doctor == null) ? 0 : doctor.hashCode());
		result = prime * result + id;
		result = prime * result + ((speciality == null) ? 0 : speciality.hashCode());
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
		AmsDoctorSuggestion other = (AmsDoctorSuggestion) obj;
		if (active != other.active)
			return false;
		if (doctor == null) {
			if (other.doctor != null)
				return false;
		} else if (!doctor.equals(other.doctor))
			return false;
		if (id != other.id)
			return false;
		if (speciality == null) {
			if (other.speciality != null)
				return false;
		} else if (!speciality.equals(other.speciality))
			return false;
		return true;
	}

	
	
	

	
	
}
