package org.zerhusen.contollers.ams.availableSlots;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAvailableTimeSlots;
import org.zerhusen.model.ams.AmsHospitalBranch;
import org.zerhusen.model.ams.AmsTimeSlots;
import org.zerhusen.model.security.User;
import org.zerhusen.repository.ams.AmsAvailableTimeSlotRepository;
import org.zerhusen.repository.ams.AmsHospitalBranchRepository;
import org.zerhusen.repository.ams.AmsTimeSlotsRepository;
import org.zerhusen.security.repository.UserRepository;

@RestController
@RequestMapping("/availableSlots")
@CrossOrigin(origins="*")
public class AvailableSlotsRest {

	@Autowired
	public AmsHospitalBranchRepository branchRepo;
	
	@Autowired
	public AmsAvailableTimeSlotRepository availSlotRepo;
	
	@Autowired
	public UserRepository doctorRepo;
	
	@Autowired
	public AmsTimeSlotsRepository timeSlotRepo;
	
	
	
	@GetMapping("/getAllSlots/{date}")
	public @ResponseBody Iterable<AmsAvailableTimeSlots> getByBrnach(@PathVariable("date") String date ) throws JSONException{
		LocalDate localDate = LocalDate.parse(date);
		return availSlotRepo.findAll().stream().filter(i-> i.getDate().equals(localDate) && i.isActive()==true).collect(Collectors.toList()); 
	}
	
	@PostMapping("/addNewAvailSlot")
	public ResponseEntity<?> addAvailableSlot(@RequestBody String request) throws JSONException{
		
		JSONObject json = new JSONObject(request);
		
		LocalDate date  = LocalDate.parse(json.getString("date"));
		
		boolean online = json.getBoolean("online");
		
		int onlinelimit = json.getInt("limit");
		
		User doctor = doctorRepo.findById(json.getLong("doctor"));
		
		AmsHospitalBranch branch = branchRepo.findById(json.getInt("branch"));
		
		AmsTimeSlots timeSlot = timeSlotRepo.findById(json.getInt("slot"));
		
		AmsAvailableTimeSlots availSlot = new AmsAvailableTimeSlots(date, online, true, 0, onlinelimit, 0);
		
		if(branch != null && doctor != null && timeSlot != null) {
			
			availSlot.setDoctor(doctor);
			
			availSlot.setBranch(branch);
			
			availSlot.setSlot(timeSlot);
			
			availSlotRepo.save(availSlot);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
			
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}
	
}
