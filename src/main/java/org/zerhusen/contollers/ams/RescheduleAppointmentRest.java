package org.zerhusen.contollers.ams;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.AmsAvailableTimeSlots;
import org.zerhusen.model.ams.AmsReschedules;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.AmsAvailableTimeSlotRepository;
import org.zerhusen.repository.ams.AmsRescheduleRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/masters")
public class RescheduleAppointmentRest {
	@Autowired
	private AmsRescheduleRepository appointmentResheduleRepo;

	@Autowired
	private AmsAvailableTimeSlotRepository availableTimeSlotRepo;
	@Autowired
	private AmsAppointmentsRepository appointmentRepo;

	@GetMapping("getAppointmentList")
	public Iterable<AmsAppointments> appointmentList(){
		return appointmentRepo.findAll().stream().filter(i->i.isActive()== true).collect(Collectors.toList());
	}


	@GetMapping("/rescheduledAppointmentList")
	public Iterable<AmsReschedules> rescheduledAppointmentList() {
		return appointmentResheduleRepo.findAll().stream().filter(i->i.isActive()== true).collect(Collectors.toList());
	}

	@PutMapping("/deleteResheduledAppointment")
	public @ResponseBody ResponseEntity<?> deleteAppointment(@RequestBody String appointment) throws JSONException{
		
		JSONObject json = new JSONObject(appointment);
		
		AmsReschedules reSheduledAppointment = appointmentResheduleRepo.findById(json.getInt("appointmentid"));
		
		if(reSheduledAppointment != null) {
			reSheduledAppointment.setActive(false);
			appointmentResheduleRepo.save(reSheduledAppointment);
			
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		} {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}



	@GetMapping(value="/getavailableTimeSlot/{id}")
	public Iterable<AmsAvailableTimeSlots> availableSlots(@PathVariable("id") int id)  {
		AmsAppointments appointment = appointmentRepo.findById(id);
		return availableTimeSlotRepo.findAll().stream().filter(i->(i.isOnline() == true)&&
				(i.getBranch()== appointment.getSlot().getBranch())&&(i.isActive()== true)&&
				(i.getDoctor() == appointment.getSlot().getDoctor())).collect(Collectors.toList()); 
	}

	@GetMapping("/getDatee/{id}")
	public AmsAppointments getDatee(@PathVariable int id) {
		return  appointmentRepo.findById(id); 
	}
	
	@PostMapping("/ResheduledAppointments")
	public ResponseEntity<?> postResheduledAppointments(@RequestBody String appointment) throws JSONException{
		JSONObject jsonobj = new JSONObject(appointment);
		LocalDate date = LocalDate.parse(jsonobj.getString("datee"));
		AmsAppointments appointmentId = appointmentRepo.findById(jsonobj.getInt("appointmentId"));
		AmsAvailableTimeSlots availableTimeSlot = availableTimeSlotRepo.findById(jsonobj.getInt("slotid"));
		if(appointmentId != null && availableTimeSlot != null) {
			
			appointmentId.setRescheduled(true);
			appointmentRepo.save(appointmentId);
			AmsReschedules resheduleAppointment = new AmsReschedules(date, true, (byte) 1);
			resheduleAppointment.setAppointment(appointmentId);
			resheduleAppointment.setSlot(availableTimeSlot);
			appointmentResheduleRepo.save(resheduleAppointment);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}
}
