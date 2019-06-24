package org.zerhusen.contollers.ams;

import java.text.ParseException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
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
import org.zerhusen.model.ams.OtherAppointments;
import org.zerhusen.model.security.User;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.OtherAppointmentsRepository;
import org.zerhusen.security.JwtTokenUtil;
import org.zerhusen.security.repository.UserRepository;

@RestController
@RequestMapping(value = "/masters")
@CrossOrigin(origins="*")
public class SurgicalAppointmentsRest {
	
	@Autowired
	  private OtherAppointmentsRepository otherAppointmentsRepo;
	
	@Autowired
	private AmsAppointmentsRepository appointmentsRepo;
	
	@Autowired
	private UserRepository userrepo;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private JavaMailSender javamailSender;
	
	@Autowired
	private EmailConfig emailConfig;
	
	@GetMapping("/oterAppointmentsPending")
	public Iterable<OtherAppointments> oterAppointmentsPending(){
		LocalDate date =LocalDate.now();
		return otherAppointmentsRepo.findAll().stream().filter(i->i.getAppointmentDate().compareTo(date)>0 && i.isCompleted() == false).collect(Collectors.toList());
	}
	
	@Scheduled(cron="0 25 10 * * *")
	public void remiderLogic() throws MessagingException {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		
		List<OtherAppointments> appointmentList = otherAppointmentsRepo.findAll().stream().filter(i-> i.getAppointmentDate().equals(tomorrow) && i.isActive() == true).collect(Collectors.toList());
		
		for(OtherAppointments appointment: appointmentList) {
			String email = appointment.getEmail();
			String subject = "Reminder !!!, Appointment on "+appointment.getAppointmentDate()+" At "+appointment.getAppointmentTime();
			String text ="<html><body> Hi "+ appointment.getPatientName() +", <br/> <p>Reminder !!!, Appointment on "+appointment.getAppointmentDate()+ " at " +appointment.getAppointmentTime()
					+" At our "+ appointment.getBranch().getBranchName() +" Brnach.</p> <br/> "+
					"<p> Please be Available at our Hospital before 30 minutes of your schedule. </p>"
					+"  </body></html>";
			
			this.appointmentEmail(email, subject, text);
			
		}
	}
	
	public void appointmentEmail(String email, String subject, String text)throws MessagingException{
		MimeMessage mail = javamailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setFrom(emailConfig.getUsername());
		helper.setTo(email);
		helper.setSubject(subject);
		helper.setText(text, true);
		javamailSender.send(mail);
	}
	
	@PutMapping("/CompletedOtherAppointment")
	public @ResponseBody ResponseEntity<?> CompletedOtherAppointment(@RequestBody String cappointment) throws JSONException, MessagingException{
		JSONObject json = new JSONObject(cappointment);
		
		OtherAppointments capp = otherAppointmentsRepo.findById(json.getInt("cappointment"));
		
		if(capp != null) {
			capp.setCompleted(true);
			otherAppointmentsRepo.save(capp);
			String csubject="Thank You";
			String ctext="<html><body> Hi Dear "+capp.getPatientName()+" <br/>Thank you for Consulting Bangalore Nethralaya </body></html>";	
			registerEmaill(capp.getEmail(),csubject,ctext);

			
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
}
	public void registerEmaill(String emailId, String csubject, String ctext)throws MessagingException{
		MimeMessage mail = javamailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
	helper.setFrom(emailConfig.getUsername());
	helper.setTo(emailId);
		
		helper.setSubject(csubject);
		helper.setText(ctext, true);
		javamailSender.send(mail);;
	}  


	
}
