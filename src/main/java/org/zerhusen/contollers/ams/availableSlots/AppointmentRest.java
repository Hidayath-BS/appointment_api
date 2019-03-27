package org.zerhusen.contollers.ams.availableSlots;

import java.text.ParseException;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;

@RestController
@CrossOrigin(origins="*")
public class AppointmentRest {
	
	@Autowired
	private AmsAppointmentsRepository appointrepo;
	
	@GetMapping("/getAppointment/{date}")
	public Iterable<AmsAppointments> getAllAppointments(@PathVariable("date") String date)throws ParseException{		
        LocalDate entrydate = LocalDate.parse(date);
		return appointrepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getDate().equals(entrydate)) && (i.isRescheduled()==false)).collect(Collectors.toList());
	}
	
	@PutMapping("/cancelAppointment")
	public ResponseEntity<?> deleteAppointments(@RequestBody String appointments)throws JSONException{
		JSONObject jsonobj = new JSONObject(appointments);
		
		AmsAppointments appoint = appointrepo.findById(jsonobj.getInt("appointments"));
		
		if(appoint!=null) {
			appoint.setActive(false);
			appointrepo.save(appoint);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

}
