package org.zerhusen.contollers.ams;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.config.EmailConfig;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.OtherAppointments;
import org.zerhusen.repository.ams.AmsAvailableTimeSlotRepository;
import org.zerhusen.repository.ams.OtherAppointmentsRepository;
import org.zerhusen.service.MessageService;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.google.gson.JsonObject;
@CrossOrigin(origins="*")
@RestController
public class OtherAppointmentRescheduleController {
@Autowired
private OtherAppointmentsRepository otherappointmentRepo;
@Autowired
private EmailConfig emailCfig;
@Autowired
private JavaMailSender javaMailSender;
MessageService msgService = new MessageService();

@PostMapping("/rescheduleotherappointment")
public ResponseEntity<?> RescheduleOtherOppointment(@RequestBody String resotherapp) throws JSONException, MessagingException{
	JSONObject resobj = new JSONObject(resotherapp);
	LocalDate resDate= LocalDate.parse(resobj.getString("date"));
	LocalTime restime= LocalTime.parse(resobj.getString("time"));
	OtherAppointments appointmentid= otherappointmentRepo.findById(resobj.getInt("appointmentId"));
	if(appointmentid!= null){
	appointmentid.setAppointmentDate(resDate);
	appointmentid.setAppointmentTime(restime);
	appointmentid.setRescheduled((byte)1);
	otherappointmentRepo.save(appointmentid);
	
	
	//Mail
//	String subject ="Congrats!! Your appointment has been rescheduled!!";
//	String text ="<html> <body> Hi Dear "+appointmentid.getPatientName()+
//			"<br/>"+" Welcome to Bangalore Nethralaya"+" </body> </html>";
//	registerEmail(appointmentid.getEmail(),subject, text);
//	String text1 = "Hi Dear "+appointmentid.getPatientName()+", \r\n" + 
//			"Thank you for registering appoiment with us.\r\n" + 
//			"your appontment has been rescheduled on"+ appointmentid.getAppointmentDate()+"at"+
//			appointmentid.getAppointmentTime()		+	"Team BANGALORE NETHRALAYA";
//	msgService.sendSmsAppointmet(appointmentid.getMobileNumber(), text1);
	return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	else{
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}
public void registerEmail(String email, String subject, String text)throws MessagingException{
	MimeMessage mail = javaMailSender.createMimeMessage();
	MimeMessageHelper helper = new MimeMessageHelper(mail, true);
	helper.setFrom(emailCfig.getUsername());
	helper.setTo(email);
	helper.setSubject(subject);
	helper.setText(text, true);
	javaMailSender.send(mail);
}
@GetMapping("/getResDatee/{id}")
public OtherAppointments getDatee(@PathVariable int id) {
	return  otherappointmentRepo.findById(id); 
}

//public Iterable<OtherAppointments> getRescheduledAppointments(){
//	
//	return otherappointmentRepo.findAll().stream().filter(i->i.getStatus()==1 && i.getRescheduled().equals(true)).collect(Collectors.toList());
//}
@GetMapping("/getrescheduleappointment/{date}")
public Iterable<OtherAppointments> getOtherAppointments(@PathVariable("date") String date){
	LocalDate resdate= LocalDate.parse(date);
	return otherappointmentRepo.findAll().stream().filter(i->i.isActive()==true).collect(Collectors.toList());
}
}
