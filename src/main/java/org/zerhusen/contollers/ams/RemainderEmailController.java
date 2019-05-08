package org.zerhusen.contollers.ams;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.config.EmailConfig;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;

@RestController
public class RemainderEmailController {

	@Autowired
	private AmsAppointmentsRepository appointmentsRepo;
	
	@Autowired
	private JavaMailSender javamailSender;
	
	@Autowired
	private EmailConfig emailConfig;
	
	
	@Scheduled(cron="0 25 10 * * *")
	public void remiderLogic() throws MessagingException {
		LocalDate tomorrow = LocalDate.now().plusDays(1);
		
		List<AmsAppointments> appointmentList = appointmentsRepo.findAll().stream().filter(i-> i.getDate().equals(tomorrow) && i.isActive() == true).collect(Collectors.toList());
		
		for(AmsAppointments appointment: appointmentList) {
			String email = appointment.getPatientUser().getEmail();
			String subject = "Reminder !!!, Appointment on "+appointment.getDate()+" At "+appointment.getSlot().getSlot().getStartTime();
			String text ="<html><body> Hi "+ appointment.getPatientName() +", <br/> <p>Reminder !!!, Appointment on "+appointment.getDate()+ " at " +appointment.getSlot().getSlot().getStartTime()
					+" At our "+ appointment.getSlot().getBranch().getBranchName() +" Brnach.</p> <br/> "+
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
	
}
