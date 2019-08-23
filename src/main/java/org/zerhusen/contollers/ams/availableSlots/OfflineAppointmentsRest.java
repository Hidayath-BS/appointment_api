package org.zerhusen.contollers.ams.availableSlots;

import java.time.LocalDate;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.AmsAvailableTimeSlots;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.AmsAvailableTimeSlotRepository;
import org.zerhusen.service.MessageService;

@RestController
@RequestMapping("/offlineAppointments")
@CrossOrigin(origins="*")
public class OfflineAppointmentsRest {

	
	@Autowired
	public AmsAvailableTimeSlotRepository slotsRepository;
	
	@Autowired
	public AmsAppointmentsRepository appointRepo;
	
	@Autowired
	public MessageService msgService;
	
	@GetMapping("/getlist")
	public Iterable<AmsAppointments> getAllAppointments(){
		return appointRepo.findAll();
	}
	
	@PostMapping("/AddAppointment")
	public @ResponseBody ResponseEntity<?> addNewWaliknAppointment(@RequestBody String request) throws JSONException{
		
		JSONObject json = new JSONObject(request);
		
		LocalDate date = LocalDate.parse(json.getString("date"));
		String patientName = json.getString("fullName");
		String gender = json.getString("gender");
		
		String  contactNumber = json.getString("mobileNumber");
		String emailId = json.getString("email");
		
		byte appointmentType = (byte) json.getInt("appointmentType");
		
		
		
		
		boolean diabetic = json.getBoolean("diabetic");
		String diabeticDuration = json.getString("diabeticDuration");
		boolean bp = json.getBoolean("bp");
		String bpDuration = json.getString("bpDuration");
		
		boolean cardiac = json.getBoolean("cardiac");
		
		
		boolean asthama = json.getBoolean("asthma");
		
		boolean drugAllergy = json.getBoolean("drugAllergy");
		String drugAllergyDuration = json.getString("drugAllergyDuration");

		boolean otherMedicalCondition = json.getBoolean("otherMedicalCondition");
		String otherMedicalConditionDuration = json.getString("otherMedicalConditionDuration");		
		
		AmsAvailableTimeSlots slot = slotsRepository.findById(json.getInt("slot"));
		
		
		 
		if(slot != null) {
			int offlineCount = slot.getWalkinCount();

			AmsAppointments appointment = new AmsAppointments(date, patientName, json.getInt("age"), gender, diabetic, diabeticDuration, bp, bpDuration, cardiac, asthama, contactNumber, emailId, appointmentType, (byte) 1, json.getBoolean("eyeProblem") , json.getString("eyeProblemExplain") , json.getBoolean("eyeDrops"), json.getString("eyeDropsExplain") , false, false, true, true, drugAllergy, drugAllergyDuration, otherMedicalCondition, otherMedicalConditionDuration, json.getString("refferedBy"), json.getString("addressLine1"), json.getString("addressLine2") , json.getString("pincode") );
			
			appointment.setSlot(slot);
			
			slot.setWalkinCount(offlineCount+1); 
			
			appointRepo.save(appointment);
			
			slotsRepository.save(slot);
			
			
			String msg = "Dear "+patientName+", \r\n" + 
					"Thank you for booking an appointment with us, Please wait for your turn.\r\n" + 
					"Team Bangalore Nethralaya.";
			
			
			msgService.sendMessage(msg, contactNumber);
			
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping("/getReviewAppointments/{id}")
	public AmsAppointments getReviewAppointments(@PathVariable("id") int id)
	{
		return appointRepo.findById(id);
	}

}
