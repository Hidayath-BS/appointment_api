package org.zerhusen.contollers.ams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.config.EmailConfig;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.AmsAvailableTimeSlots;
import org.zerhusen.model.ams.AmsHospitalBranch;
import org.zerhusen.model.ams.AmsReschedules;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.AmsAvailableTimeSlotRepository;
import org.zerhusen.repository.ams.AmsHospitalBranchRepository;
import org.zerhusen.repository.ams.AmsRescheduleRepository;
import org.zerhusen.service.MessageService;

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
	
	@Autowired
	private JavaMailSender javamailSender;
	
	@Autowired
	private EmailConfig emailConfig;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private AmsHospitalBranchRepository branchRepo;

	@GetMapping("getAppointmentList")
	public Iterable<AmsAppointments> appointmentList(){
		return appointmentRepo.findAll().stream().filter(i->i.isActive()== true).collect(Collectors.toList());
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
		return availableTimeSlotRepo.findAll().stream().filter(i->
				(i.getBranch()== appointment.getSlot().getBranch())&&(i.isActive()== true) && i.getOnline() == appointment.getAppointmentType()  ).collect(Collectors.toList()); 
	}

	@GetMapping("/getDatee/{id}")
	public AmsAppointments getDatee(@PathVariable("id") int id) {
		return  appointmentRepo.findById(id); 
	}
	
	@PostMapping("/ResheduledAppointments")
	public ResponseEntity<?> postResheduledAppointments(@RequestBody String appointment) throws JSONException, MessagingException{
		JSONObject jsonobj = new JSONObject(appointment);
		LocalDate date = LocalDate.parse(jsonobj.getString("datee"));
		AmsAppointments appointmentId = appointmentRepo.findById(jsonobj.getInt("appointmentId"));
		AmsAvailableTimeSlots availableTimeSlot = availableTimeSlotRepo.findById(jsonobj.getInt("slotid"));
		if(appointmentId != null && availableTimeSlot != null) {
			
			
			String email = appointmentId.getEmailId();
			String subject = "RESCHEDULE INFORMATION OF YOUR APPOINTMENT AT BANGALORE NETHRALAYA";
			String text ="<html><body>"
					+ "<p> Hi "+ appointmentId.getPatientName()+",<br/>"
					+ "We have rescheduled your appointment on - "+ availableTimeSlot.getDate()+" - AT -"+availableTimeSlot.getSlot().getStartTime()+"<br/>"
					+ "<hr>"
					+ "Thank you <br/>"
					+ "Team <b>BANGALORE NETHRALAYA</b></p>"
					+ "</body></html>";
			this.rescehduleEmail(email, subject, text);
			
			String msg = "Dear "+appointmentId.getPatientName()+",\r\n" + 
					"Your appointment is rescheduled to "+availableTimeSlot.getSlot().getStartTime()+" on "+availableTimeSlot.getDate()+", with "+availableTimeSlot.getDoctor().getUsername()+",\r\n" + 
					"Bangalore Nethralaya "+availableTimeSlot.getBranch().getBranchName()+" branch.";
			String phoneNumber = appointmentId.getContactNumber();
			
			if(phoneNumber != null && phoneNumber != "") {
				messageService.sendMessage(msg, phoneNumber);
			}

			
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

	public void rescehduleEmail(String email, String subject, String text)throws MessagingException{
		MimeMessage mail = javamailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setFrom(emailConfig.getUsername());
		helper.setTo(email);
		helper.setSubject(subject);
		helper.setText(text, true);
		javamailSender.send(mail);
	}
	
	
	
	@GetMapping("/rescheduledAppointmentList")
	public Iterable<AmsReschedules> rescheduledAppointmentList() {
	LocalDate date = LocalDate.now();
	return appointmentResheduleRepo.findAll().stream().filter(i->i.isActive()== true && i.getDate().isAfter(date) || i.getDate().equals(date)).collect(Collectors.toList());
	}
	@GetMapping("/getResheduledAppointmentsOnDate/{date}")
	public Iterable<AmsReschedules> getResheduledAppointmentsOnDate(@PathVariable String date){
	LocalDate datee = LocalDate.parse(date);
	return appointmentResheduleRepo.findAll().stream().filter(i->i.isActive()==true && i.getDate().equals(datee)).collect(Collectors.toList());
	}

	@GetMapping("/getMonthwiseResheduledAppiontment/{date}")
	public Iterable<AmsReschedules> getMonthwiseReshudledAppiontment(@PathVariable String date,HttpServletRequest req){
	LocalDate entrydatee = LocalDate.parse(date);
	int month = entrydatee.getMonthValue();
	List<AmsReschedules> result = new ArrayList<AmsReschedules>();
	Iterable<AmsReschedules> app = appointmentResheduleRepo.findAll().stream().filter(i->(i.isActive()==true) ).collect(Collectors.toList());
	for (AmsReschedules amsReshedules : app) {
	LocalDate d1 = amsReshedules.getDate();
	int m1 = d1.getMonthValue();
	if(m1 == month) {
	result.add(amsReshedules);
	}
	}

	return result;
	}

	@PostMapping("/getInBetweenDatesResheduledAppointment")
	public ResponseEntity<?> getInBetweenDatesResheduledAppointment(@RequestBody String data) throws JSONException{
	JSONObject jsonObj = new JSONObject(data);
	LocalDate fromDate = LocalDate.parse(jsonObj.getString("FromDate"));
	LocalDate toDate = LocalDate.parse(jsonObj.getString("ToDate"));
	List<AmsReschedules> appointments = appointmentResheduleRepo.findAll().stream().filter(i->i.isActive() == true && i.getDate().isAfter(fromDate) && i.getDate().isBefore(toDate) || i.getDate().equals(fromDate) || i.getDate().equals(toDate)).collect(Collectors.toList());
	return ResponseEntity.ok(appointments);

	}
	
	@GetMapping("/futurerescheduledAppointmentList/{id}")
	public Iterable<AmsReschedules> futurerescheduledAppointmentList(@PathVariable int id) {
	LocalDate date = LocalDate.now();
	AmsHospitalBranch branch = branchRepo.findById(id);
	return appointmentResheduleRepo.findAll().stream().filter(i->i.isActive()== true && i.getDate().isAfter(date) || i.getDate().equals(date) && i.getAppointment().getSlot().getBranch()==branch).collect(Collectors.toList());
	}
	
	@GetMapping("/getResheduledAppointmentsOnDatebranchWise/{date}/{id}")
	public Iterable<AmsReschedules> getResheduledAppointmentsOnDatebranchWise(@PathVariable String date,@PathVariable int id){
	LocalDate datee = LocalDate.parse(date);
	AmsHospitalBranch branch = branchRepo.findById(id);
	return appointmentResheduleRepo.findAll().stream().filter(i->i.isActive()==true && i.getDate().equals(datee) && i.getAppointment().getSlot().getBranch() == branch).collect(Collectors.toList());
	}
	

}
