package org.zerhusen.contollers.ams.masters;

import java.text.ParseException;
import java.time.LocalTime;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsTimeSlots;
import org.zerhusen.repository.ams.AmsTimeSlotsRepository;

@RestController
@RequestMapping("masters")
@CrossOrigin(origins="*")
public class TimeSoltsRest {

	@Autowired
	public AmsTimeSlotsRepository timeSlotRepo;
	
	@GetMapping("/getTimeSlots")
	public @ResponseBody Iterable<AmsTimeSlots> getAllTimeSlots(){
		return timeSlotRepo.findAll().stream().filter(i-> i.isActive()==true).collect(Collectors.toList());
	}
	
	@PostMapping("/addtimeslot")
	public ResponseEntity<?> addtimeslot(@RequestBody String timeslot) throws JSONException{
		JSONObject obj = new JSONObject(timeslot);

        LocalTime startTime = LocalTime.parse(obj.getString("startTime"));
        LocalTime endTime = LocalTime.parse(obj.getString("endTime"));
		AmsTimeSlots slottime = new AmsTimeSlots(obj.getString("slotName"),startTime,endTime,true);

		timeSlotRepo.save(slottime);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/getslot/{id}")
	public AmsTimeSlots getslot(@PathVariable("id") int id) {
		return timeSlotRepo.findById(id);
	}
	@PutMapping("/editTimeslot")
	public ResponseEntity<?> editTimeslot(@RequestBody String timee) throws JSONException,ParseException{
		JSONObject ob = new JSONObject(timee);
		int id = ob.getInt("slotid");
		LocalTime starttime = LocalTime.parse(ob.getString("startTime"));
        LocalTime endtime = LocalTime.parse(ob.getString("endTime"));
    	String slotname = ob.getString("slotName");
    	

		AmsTimeSlots sloty = timeSlotRepo.findById(id);
		
		if(sloty!=null) {
			sloty.setSlotName(slotname);
			sloty.setStartTime(starttime);
			sloty.setEndTime(endtime);
			timeSlotRepo.save(sloty);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
//    		return new ResponseEntity<>(HttpStatus.ACCEPTED);
    	}
    	else {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
	}

//	To delete a branch
	@PutMapping("/deletetimeslot")
	public @ResponseBody ResponseEntity<?> deletetimeslot(@RequestBody String slotss) throws JSONException{
		JSONObject json = new JSONObject(slotss);
		
		AmsTimeSlots slottype =timeSlotRepo.findById(json.getInt("slotss"));

		if(slottype != null) {
			slottype.setActive(false);
			timeSlotRepo.save(slottype);

			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	






}
