package org.zerhusen.contollers.ams.masters;

import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsReferralDoctors;
import org.zerhusen.repository.ams.AmsReferralRepository;

@RestController
@RequestMapping("/masters")
@CrossOrigin(origins="*")
public class AmsReferralDoctorsRest {

	@Autowired
	public AmsReferralRepository amsDoctorReferral;
	
	@GetMapping("/allReferralDoctors")
	public Iterable<AmsReferralDoctors> getReferralDoctors(){
		return amsDoctorReferral.findAll().stream().filter(i-> i.isActive()==true).collect(Collectors.toList());
	}
	
	@PostMapping("/addReferralDoctor")
	public ResponseEntity<?> addReferralDoctor(@RequestBody String request) throws JSONException{
		JSONObject json = new JSONObject(request);
		
		AmsReferralDoctors doctor = new AmsReferralDoctors(json.getString("fullName"), json.getString("hospitalName") , json.getString("mobileNumber"), true);
		
		amsDoctorReferral.save(doctor);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PutMapping("/deleteReferralDoctor")
	public ResponseEntity<?> deleteReferralDoctor(@RequestBody String request) throws JSONException{
		
		JSONObject json = new JSONObject(request);
		
		AmsReferralDoctors doctor = amsDoctorReferral.findById(json.getInt("doctor"));
		
		if(doctor != null) {
			doctor.setActive(false);
			amsDoctorReferral.save(doctor);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
