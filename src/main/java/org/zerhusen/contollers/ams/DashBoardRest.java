package org.zerhusen.contollers.ams;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.security.Authority;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.AmsDoctorSuggestionRepository;
import org.zerhusen.repository.ams.AmsPatientUsersRepository;
import org.zerhusen.repository.ams.AmsServicesRepository;
import org.zerhusen.security.repository.AuthorityRepository;
import org.zerhusen.security.repository.UserRepository;

@RestController
@CrossOrigin(origins="*")
public class DashBoardRest {

	@Autowired
	private AmsAppointmentsRepository appointmentRepo;
	@Autowired
	private AmsDoctorSuggestionRepository doctorRepo;
	@Autowired 
	private AmsServicesRepository servicesRepo;
	@Autowired
	private AmsPatientUsersRepository patientRepo;
	
	@Autowired
	private AuthorityRepository roleRepo;

	
	@Autowired
	private UserRepository userRepo;
	@GetMapping("/doctorsCount")
	public long doctorCount() {
		Authority role = roleRepo.findByAuthority("DOCTOR");
		
		
		return userRepo.findAll().stream().filter(i->i.getAuthorities().contains(role) && i.getEnabled().equals(true)).count();
	}

	@GetMapping("/patientsCount")
	public long patientCount() {
		return  patientRepo.findAll().stream().filter(i->i.isActive()==true).count();
	}

	@GetMapping("/serviceCount")
	public long serviceCount() {
		return servicesRepo.findAll().stream().filter(i->i.isActive()==true).count();
	}

	@GetMapping("/todaysAppointmentCount")
	public long appointmentCount() {
		LocalDate date = LocalDate.now();
		return appointmentRepo.findAll().stream().filter(i->(i.getDate().equals(date))&&(i.isActive() == true)).count();
	}

	@GetMapping("/getAppointmentList")
	public Iterable<AmsAppointments> appointmentList() {
		return appointmentRepo.findAll().stream().filter(i->i.isActive() == true).collect(Collectors.toList());
	}
	
	
}
