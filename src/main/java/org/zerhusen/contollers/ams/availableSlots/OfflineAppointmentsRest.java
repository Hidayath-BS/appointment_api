package org.zerhusen.contollers.ams.availableSlots;

import java.time.LocalDate;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.AmsAvailableTimeSlots;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.AmsAvailableTimeSlotRepository;

@RestController
@RequestMapping("/offlineAppointments")
@CrossOrigin(origins="*")
public class OfflineAppointmentsRest {

	
	@Autowired
	public AmsAvailableTimeSlotRepository slotsRepository;
	
	@Autowired
	public AmsAppointmentsRepository appointRepo;
	
	@GetMapping("/getlist")
	public Iterable<AmsAppointments> getAllAppointments(){
		return appointRepo.findAll();
	}
	
	@PostMapping("/AddAppointment")
	public @ResponseBody ResponseEntity<?> addNewWaliknAppointment(@RequestBody String request) throws JSONException{
		
		JSONObject json = new JSONObject(request);
		
		LocalDate date = LocalDate.parse(json.getString("date"));
		String patientName = json.getString("fullName");
		LocalDate dateOfBirth = LocalDate.parse(json.getString("dob"));
		String gender = json.getString("gender");
		
		String  contactNumber = json.getString("mobileNumber");
		String emailId = json.getString("email");
		
		byte appointmentType = (byte) json.getInt("appointmentType");
		
		
		
		
		boolean diabetic = json.getBoolean("diabetic");
		String diabeticDuration = json.getString("diabeticDuration");
		boolean bp = json.getBoolean("bp");
		String bpDuration = json.getString("bpDuration");
		
		boolean cardiac = json.getBoolean("cardiac");
		String cardiacDuration = json.getString("cardiacDuration");
		
		boolean asthama = json.getBoolean("asthma");
		String asthamaDuration = json.getString("asthmaDuration");
		
		boolean eyeProblem = json.getBoolean("eyeProblem");
		String eyeProblemDetails = json.getString("eyeprblemDetails");
		
		boolean eyeDrops = json.getBoolean("eyeDrops");
		String eyeDropDetails = json.getString("eyeDropsDetails");
		
		AmsAvailableTimeSlots slot = slotsRepository.findById(json.getInt("slot"));
		
		
		
		if(slot != null) {
			int offlineCount = slot.getWalkinCount();
//			AmsAppointments appointment = new AmsAppointments(date, patientName, dateOfBirth, gender, diabetic, diabeticDuration,
//					bp, bpDuration, cardiac, cardiacDuration, asthama, asthamaDuration, contactNumber, emailId, appointmentType,
//					(byte) 1, false, true);
			
			AmsAppointments appointment = new AmsAppointments(date, patientName, dateOfBirth, gender, diabetic, diabeticDuration, bp,
					bpDuration, cardiac, cardiacDuration, asthama, asthamaDuration, contactNumber, emailId, appointmentType,
					(byte) 1, eyeProblem, eyeProblemDetails, eyeDrops, eyeDropDetails, false, false, true);
			
			
			appointment.setSlot(slot);
			
			slot.setWalkinCount(offlineCount+1);
			
			appointRepo.save(appointment);
			
			slotsRepository.save(slot);
			
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
