package org.zerhusen.contollers.ams.availableSlots;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.config.EmailConfig;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.AmsAppointmentsForReview;
import org.zerhusen.model.ams.AmsHospitalBranch;
import org.zerhusen.payload.AppointmentMail;
import org.zerhusen.repository.ams.AmsAppointmentForReviewRepository;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.repository.ams.AmsHospitalBranchRepository;
import org.zerhusen.service.MessageService;

import com.google.gson.JsonObject;

@RestController
@CrossOrigin(origins="*")
public class AppointmentRest {
	
	@Autowired
	private AmsAppointmentsRepository appointrepo;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private EmailConfig emailCfig;
	
	@Autowired
	private AmsAppointmentForReviewRepository forReviewRepo;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private AmsHospitalBranchRepository branchRepo;


	@GetMapping("/getOnlineAppointment/{date}")
	public Iterable<AmsAppointments> getOnlineAppointments(@PathVariable("date") String date)throws ParseException{		
        LocalDate entrydatee = LocalDate.parse(date);
		return appointrepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getDate().equals(entrydatee)) && i.isCompleted()==false && (i.isRescheduled()==false)&& (i.getAppointmentType()==1)).collect(Collectors.toList());
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
	
//	change status request form appointments list 
	
	@PutMapping("/changeAppointmentStatus")
	public ResponseEntity<?> changeAppointmentStatus(@RequestBody String statusrequest) throws JSONException{
		JSONObject json = new JSONObject(statusrequest);
		
		AmsAppointments appointment = appointrepo.findById(json.getInt("appointment"));
		boolean completed = json.getBoolean("status");
		if(appointment != null) {
			appointment.setCompleted(completed);
			appointrepo.save(appointment);
			
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping("/getreviewDate/{id}")
	public AmsAppointments getreviewDate(@PathVariable int id){
		return appointrepo.findById(id);
	}
	
	
	// review date
	
	@PutMapping("/addDate")
    public ResponseEntity<?> addDate(@RequestBody String request)throws JSONException,ParseException, MessagingException
    {
    	JSONObject req = new JSONObject(request);
    	
    	int id = req.getInt("reviewid");
    	
    	LocalDate reviewDat = LocalDate.parse(req.getString("reviewDate"));
    	
    	AmsAppointments appDate = appointrepo.findById(id);
    	
    	
    
    	if(appDate!=null) {
    		AmsAppointmentsForReview forReview = new AmsAppointmentsForReview(reviewDat, true);
    		forReview.setAppointment(appDate);
    		forReviewRepo.save(forReview);
        	//Message
    		
    		String phoneNumber = appDate.getContactNumber();
    		 
    		 String msg = "Dear "+appDate.getPatientName()+",\r\n" + 
    		 		"a gentle reminder on your follow up visit will be on "+reviewDat+".";
    		
    		messageService.sendMessage(msg, phoneNumber);
           
    		return new ResponseEntity<>(HttpStatus.ACCEPTED);
    	}
    	else {
    		return new ResponseEntity<>(HttpStatus.CONFLICT);
    		}
    }
    
    	@Scheduled(cron="0 30 10 * * *")
    	public void remiderLogic() throws MessagingException{
    		LocalDate weekely = LocalDate.now().plusDays(7);
    		
    		List<AmsAppointmentsForReview> forReviewList = forReviewRepo.findAll().stream().filter(i-> i.getReviewDate().equals(weekely) && i.isActive() == true).collect(Collectors.toList());
    		
    		
    		
   
    		for(AmsAppointmentsForReview review: forReviewList) {
    		
    			 String subject ="Reminder !!!, Next Review Date on "+review.getReviewDate();
 	    		String text ="<html>"
 	    				+ "<body> <p> Hi Dear <b>"+review.getAppointment().getPatientName()+"</b> </p>"+"<p>Welcome to <b>BANGALORE NETHRALAYA</b> <br/> "
 	    						+ "<hr/>"
 	    						+ "Your Username for Login is : "+review.getAppointment().getEmailId()
 	    						+ "<br/>Thank You <br/>"
 	    						+ "Team <b>BANGALORE NETHRALAYA</b></p>" + "</body> </html>";
	
    			this.appointmentEmail(review.getAppointment().getEmailId(), subject, text);
    		}

    	}
    	
    	
    	public void appointmentEmail(String email, String subject, String text)throws MessagingException{
    		MimeMessage mail = javaMailSender.createMimeMessage();
    		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    		helper.setFrom(emailCfig.getUsername());
    		helper.setTo(email);
    		helper.setSubject(subject);
    		helper.setText(text, true);
    		javaMailSender.send(mail);
    	}


    	@PutMapping("/CompletedAppointment")
    	public @ResponseBody ResponseEntity<?> CompletedAppointment(@RequestBody String cappointment) throws JSONException, MessagingException{
    		JSONObject json = new JSONObject(cappointment);
    		
    		AmsAppointments capp = appointrepo.findById(json.getInt("cappointment"));
    		
    		if(capp != null) {
    			capp.setCompleted(true);
    			appointrepo.save(capp);
    			String csubject="Thank You";
    			String ctext="<html><body> Hi Dear "+capp.getPatientName()+" <br/>Thank you for Consulting Bangalore Nethralaya </body></html>";	
    			registerEmaill(capp.getEmailId(),csubject,ctext);

    			
    			return new ResponseEntity<>(HttpStatus.ACCEPTED);
    		}else {
    			return new ResponseEntity<>(HttpStatus.CONFLICT);
    		}
    }
    	public void registerEmaill(String emailId, String csubject, String ctext)throws MessagingException{
    		MimeMessage mail = javaMailSender.createMimeMessage();
    		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
    	helper.setFrom(emailCfig.getUsername());
    	helper.setTo(emailId);
    		
    		helper.setSubject(csubject);
    		helper.setText(ctext, true);
    		javaMailSender.send(mail);
    	}


    	// get list of review Date
    	@GetMapping("/ReviewDateList/{date}")
    	public Iterable<AmsAppointmentsForReview> getAll(@PathVariable("date") String date){
    		LocalDate reviewdate = LocalDate.parse(date);
    	return forReviewRepo.findAll().stream().filter(i->i.isActive()==true && i.getReviewDate().equals(reviewdate)).collect(Collectors.toList());
    	}

    	//delete for review date
    	@PutMapping("/deleteReviewDate")
    	public ResponseEntity<?> deleteReviewDate(@RequestBody String reviewdate)throws JSONException{
    	JSONObject jsonobj = new JSONObject(reviewdate);

    	AmsAppointmentsForReview reviews = forReviewRepo.findById(jsonobj.getInt("reviewss"));

    	if(reviews!=null) {
    	reviews.setActive(false);
    	// reviews.setStatus(false);
    	forReviewRepo.save(reviews);
    	return new ResponseEntity<>(HttpStatus.ACCEPTED);
    	}
    	else {
    	return new ResponseEntity<>(HttpStatus.CONFLICT);
    	}
    	}
    	
    	
        //completed review date
        @PutMapping("/CompletedReviewDate")
    	public ResponseEntity<?> CompletedReviewDate(@RequestBody String reviewlist)throws JSONException{
    		JSONObject jsonobj = new JSONObject(reviewlist);
    		
    		AmsAppointmentsForReview reviews = forReviewRepo.findById(jsonobj.getInt("reviewss"));
    		
    		if(reviews!=null) {
    		
    			boolean status = jsonobj.getBoolean("status");
    			
    			reviews.setCompleted(status);
    			forReviewRepo.save(reviews);
    			return new ResponseEntity<>(HttpStatus.ACCEPTED);
    		}
    		else {
    			return new ResponseEntity<>(HttpStatus.CONFLICT);
    		}
    	}
    	
        
    	
    	
    	@GetMapping("/getMonthwiseAppointment/{date}")
    	public Iterable<AmsAppointments> getMonthwiseAppointmentt(@PathVariable("date") String date)throws ParseException{		
          LocalDate entrydatee = LocalDate.parse(date);
    		int month = entrydatee.getMonthValue();
    		List<AmsAppointments> appoint = new ArrayList<AmsAppointments>();
    		Iterable<AmsAppointments> appo= appointrepo.findAll().stream().filter(i->(i.isActive()==true) && i.isCompleted()==false && (i.isRescheduled()==false)).collect(Collectors.toList());
    	for (AmsAppointments amsAppointments : appo) {
    		LocalDate obj = amsAppointments.getDate();
    		int mon=obj.getMonthValue();
    		if(mon==month) {
    			appoint.add(amsAppointments);
    		}
    	}
    	return appoint;
    	}
    	@PostMapping("/getBetweenDateAppiontment")
    	public ResponseEntity<?> getBetweenDateAppiontment(@RequestBody String data) throws JSONException{
    		JSONObject ob = new JSONObject(data);
    		LocalDate from = LocalDate.parse(ob.getString("fromDate"));
    		LocalDate to = LocalDate.parse(ob.getString("toDate"));
    		List<AmsAppointments> appoin = appointrepo.findAll().stream().filter(i->(i.isActive()==true) && i.isCompleted()==false && (i.isRescheduled()==false) && i.getDate().isAfter(from) && i.getDate().isBefore(to) || i.getDate().equals(from) || i.getDate().equals(to)).collect(Collectors.toList());
            return ResponseEntity.ok(appoin);
    		
    	}
    	
    	@GetMapping("/getfutureappointments")
    	public Iterable<AmsAppointments> getAppointments()throws ParseException{		
            LocalDate entrydate = LocalDate.now();
    		return appointrepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getDate().equals(entrydate)||i.getDate().isAfter(entrydate)) && (i.isRescheduled()==false)).collect(Collectors.toList());
    	}
    	
    	@GetMapping("/getfutureappointmentsBranchWise/{id}")
    	public Iterable<AmsAppointments> getfutureappointmentsBranchWise(@PathVariable int id)throws ParseException{
    		AmsHospitalBranch branch = branchRepo.findById(id);
            LocalDate entrydate = LocalDate.now();
    		return appointrepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getDate().equals(entrydate)||i.getDate().isAfter(entrydate)) && (i.isRescheduled()==false) && i.getSlot().getBranch() == branch).collect(Collectors.toList());
    	}
    	
    	@GetMapping("/getAppointmentbranchwise/{date}/{id}")
    	public Iterable<AmsAppointments> getAppointmentbranchwise(@PathVariable("date") String date,@PathVariable int id)throws ParseException{		
            LocalDate entrydate = LocalDate.parse(date);
            AmsHospitalBranch branch = branchRepo.findById(id);
    		return appointrepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getDate().equals(entrydate)) && (i.isRescheduled()==false) && i.getSlot().getBranch() == branch).collect(Collectors.toList());
    	}
    	
    	
}
