package org.zerhusen.contollers.ams.availableSlots;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

import org.json.JSONArray;
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
	
	
	
	@GetMapping("/getAllSlotsByBranch/{date}")
	public @ResponseBody Iterable<AmsAvailableTimeSlots> getAllSlotsByBrnach(@PathVariable("date") String date ) throws JSONException{
		LocalDate localDate = LocalDate.parse(date);
		return availSlotRepo.findAll().stream().filter(i-> i.getDate().equals(localDate) && i.isActive()==true).collect(Collectors.toList()); 
	}
//	
	@GetMapping("/getAllSlots/{date}")
	public @ResponseBody Iterable<AmsAvailableTimeSlots> getByBrnach(@PathVariable("date") String date ) throws JSONException{
		LocalDate localDate = LocalDate.parse(date);
		LocalTime localTime = LocalTime.now();
		LocalDate currDate = LocalDate.now();
		if(localDate.equals(currDate)){
		return availSlotRepo.findAll().stream().filter(i-> i.getDate().equals(localDate) && i.isActive()==true && i.getSlot().getStartTime().isAfter(localTime) && i.getWalkinCount()==0).collect(Collectors.toList());
		}
		else{
			return availSlotRepo.findAll().stream().filter(i-> i.getDate().equals(localDate) && i.isActive()==true && i.getWalkinCount()==0 ).collect(Collectors.toList());
		}
	}
	
	
//	@PostMapping("/addNewAvailSlot")
//	public ResponseEntity<?> addAvailableSlot(@RequestBody String request) throws JSONException{
//		
//		JSONObject json = new JSONObject(request);
//		
//		LocalDate date  = LocalDate.parse(json.getString("date"));
//		
//		boolean online = json.getBoolean("online");
//		
//		int onlinelimit = json.getInt("limit");
//		
//		User doctor = doctorRepo.findById(json.getLong("doctor"));
//		
//		AmsHospitalBranch branch = branchRepo.findById(json.getInt("branch"));
//		
//		AmsTimeSlots timeSlot = timeSlotRepo.findById(json.getInt("slot"));
//		
//		AmsAvailableTimeSlots availSlot = new AmsAvailableTimeSlots(date, online, true, 0, onlinelimit, 0);
//		
//		if(branch != null && doctor != null && timeSlot != null) {
//			
//			availSlot.setDoctor(doctor);
//			
//			availSlot.setBranch(branch);
//			
//			availSlot.setSlot(timeSlot);
//			
//			availSlotRepo.save(availSlot);
//			return new ResponseEntity<>(HttpStatus.ACCEPTED);
//			
//		}else {
//			return new ResponseEntity<>(HttpStatus.CONFLICT);
//		}
//		
//	}
	
	
//	API FOR ADDING MULTIPLE AVAILABLE TIME SLOTS AT ONCE.
	
	@PostMapping("/addNewAvailSlot")
	public ResponseEntity<?> createAvailableSlots(@RequestBody String request) throws JSONException{
		JSONObject json = new JSONObject(request);
		
		LocalDate date = LocalDate.parse(json.getString("date"));
		int branchid = json.getInt("branch");
		int doctorId = json.getInt("doctor");
		
		JSONArray slots = json.getJSONArray("slots");
		
//		get Objects of branch and doctor
		
		AmsHospitalBranch branch = branchRepo.findById(branchid);
		User doctor = doctorRepo.findById(doctorId);
		if(branch != null && doctor != null) {
			
			for(int i=0; i< slots.length(); i++) {
				
				JSONObject slotjson = (JSONObject) slots.get(i);
				
				AmsTimeSlots slot = timeSlotRepo.findById(slotjson.getInt("id"));
				
				AmsAvailableTimeSlots availableSlot = new AmsAvailableTimeSlots(date, (byte) slotjson.getInt("onlinestatus"), true, 0, slotjson.getInt("onlineLimit"), 0);
				availableSlot.setBranch(branch);
				availableSlot.setDoctor(doctor);
				availableSlot.setSlot(slot);
				availSlotRepo.save(availableSlot);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}
	
	@PutMapping("/deleteAvailableTimeSlot")
	public ResponseEntity<?> deleteAvailableTimeSlot(@RequestBody String request) throws JSONException
	{
		JSONObject json = new JSONObject(request);
		AmsAvailableTimeSlots availableSlot = availSlotRepo.findById(json.getInt("slots"));
		
		if(availableSlot!=null) {
			availableSlot.setActive(false);
			availSlotRepo.save(availableSlot);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
}
