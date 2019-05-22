package org.zerhusen.contollers.ams;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.Ams_patient_users;
import org.zerhusen.repository.ams.AmsPatientUsersRepository;

@RestController
@RequestMapping("/patient")
@CrossOrigin(origins="*")
public class AmsPatientsRest {

	@Autowired
	private AmsPatientUsersRepository patientRepository;
	
	@GetMapping("/getAllPatients")
	public Iterable<Ams_patient_users> getPatientUseres(){
		List<Ams_patient_users> patients = patientRepository.findAll().stream().filter(i-> i.isActive()==true).collect(Collectors.toList());
		return patients;
	}
	
	@PutMapping("/deletePatient")
	public ResponseEntity<?> deletePatients(@RequestBody String request) throws JSONException{
		JSONObject json = new JSONObject(request);
		Ams_patient_users patient = patientRepository.findById(json.getLong("patient"));
		
		if(patient != null) {
			patient.setActive(false);
			patientRepository.save(patient);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
