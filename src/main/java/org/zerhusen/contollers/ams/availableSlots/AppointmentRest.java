package org.zerhusen.contollers.ams.availableSlots;

import java.text.ParseException;
import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.config.EmailConfig;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.payload.AppointmentMail;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;

@RestController
@CrossOrigin(origins="*")
public class AppointmentRest {
	
	@Autowired
	private AmsAppointmentsRepository appointrepo;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private EmailConfig emailCfig;


	@GetMapping("/getOnlineAppointment/{date}")
	public Iterable<AmsAppointments> getOnlineAppointments(@PathVariable("date") String date)throws ParseException{		
        LocalDate entrydatee = LocalDate.parse(date);
		return appointrepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getDate().equals(entrydatee)) && (i.isRescheduled()==false)&& (i.getAppointmentType()==1)).collect(Collectors.toList());
	}
	
	@GetMapping("/getMailid/{id}")
	public AmsAppointments getMailid(@PathVariable int id) throws MessagingException{
		AmsAppointments app = appointrepo.findById(id);

		return appointrepo.findById(id);
	}

	@PostMapping("/submitmail")
	public ResponseEntity<AmsAppointments> mailsubmission(@RequestBody String mails)throws JSONException, ParseException, MessagingException
	{

		JSONObject jsonobj = new JSONObject(mails);
			
		AppointmentMail ap= new AppointmentMail(jsonobj.getString("subject"), jsonobj.getString("text"));
		

		AmsAppointments appp = appointrepo.findById(jsonobj.getInt("appid"));
		
				String textt="<html><body> Hi Dear "+appp.getPatientName()+"<br/>"+ ap.getText()+ " </body></html>";	
		registerEmail(appp.getEmailId(),ap.getSubject(),textt);

			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		
		
	}
	
	public void registerEmail(String emailId, String subject, String text)throws MessagingException{
		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
	helper.setFrom(emailCfig.getUsername());
	helper.setTo(emailId);
		
		helper.setSubject(subject);
		helper.setText(text, true);
		javaMailSender.send(mail);
	}
	
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
