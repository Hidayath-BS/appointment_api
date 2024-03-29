package org.zerhusen.contollers.ams;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.config.EmailConfig;
import org.zerhusen.model.ams.AmsHospitalBranch;
import org.zerhusen.model.ams.Ams_Procedures;
import org.zerhusen.model.ams.Ams_patient_users;
import org.zerhusen.model.ams.OtherAppointments;
import org.zerhusen.model.security.User;
import org.zerhusen.repository.ams.AmsHospitalBranchRepository;
import org.zerhusen.repository.ams.AmsPatientUsersRepository;
import org.zerhusen.repository.ams.AmsProceduresRepository;
import org.zerhusen.repository.ams.OtherAppointmentsRepository;

import org.zerhusen.security.JwtTokenUtil;
import org.zerhusen.security.repository.AuthorityRepository;
import org.zerhusen.security.repository.UserRepository;
import org.zerhusen.service.MessageService;

@CrossOrigin(origins="*")
@RestController
public class OtherAppointmentRest {
	
	@Autowired
	public AuthorityRepository roleRepo;

	@Autowired
	public AmsPatientUsersRepository userrepo;
	
	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	@Qualifier("jwtUserDetailsService")
	private UserDetailsService userDetailsService;
	
	@Autowired
	private OtherAppointmentsRepository otherapprepo;
	
	@Autowired
	private AmsPatientUsersRepository patientrepo;
	
	@Autowired
	private AmsProceduresRepository procedureRepo;
	
	@Autowired
	private AmsHospitalBranchRepository branchRepo;
	
	@Autowired
	private UserRepository doctorRepo;
	
	
	@Autowired
	private JavaMailSender javamailSender;
	
	@Autowired
	private EmailConfig emailConfig;
	
	@Autowired
	private MessageService messageService;

	
	@GetMapping("/getOtherAppointment")
	public Iterable<OtherAppointments> getPatientWiseOtherAppointments(HttpServletRequest req)
	{
		String token = req.getHeader(tokenHeader).substring(7);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		Ams_patient_users user = userrepo.findByEmail(username);
		return otherapprepo.findAll().stream().filter(i->i.isActive()== true && i.getPatient() != null && i.getPatient().equals(user)).collect(Collectors.toList());
	}
	
	@GetMapping("/getOtherApp/{id}")
	public Iterable<OtherAppointments> getAllOtherAppointments(@PathVariable("id") int id)throws JSONException
	{
		AmsHospitalBranch branch = branchRepo.findById(id);
		LocalDate entrydate = LocalDate.now();
		return otherapprepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getAppointmentDate().equals(entrydate)||i.getAppointmentDate().isAfter(entrydate))&& i.getBranch()==branch).collect(Collectors.toList());
	}
	
	@GetMapping("/getSurgApp/{date}/{id}")
	public Iterable<OtherAppointments> getAllSurgicalAppointments(@PathVariable("date") String date, @PathVariable("id") int id)throws JSONException
	{
		AmsHospitalBranch branch = branchRepo.findById(id);
		LocalDate entrydate = LocalDate.parse(date);
		return otherapprepo.findAll().stream().filter(i->(i.isActive()==true)&&(i.getAppointmentDate().equals(entrydate))&& i.getBranch()==branch).collect(Collectors.toList());
	}
	
	@PutMapping("/deleteAppointment")
	public ResponseEntity<?> deleteAppointment(@RequestBody String appoint)throws JSONException
	{
		JSONObject jsonobj = new JSONObject(appoint);
		
		OtherAppointments app = otherapprepo.findById(jsonobj.getInt("appoint"));
		
		if(app!=null) {
			app.setActive(false);
			otherapprepo.save(app);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	
	@GetMapping("/getAllPatients")
	public Iterable<Ams_patient_users> getAllPatients()
	{
		return patientrepo.findAll().stream().filter(i->i.isActive()==true).collect(Collectors.toList());
	}
	
	@GetMapping("/getPatientDetail/{id}")
	public Ams_patient_users getPatientDetails(@PathVariable("id") int id) {
		Ams_patient_users patient = patientrepo.findById(id);
		
		if(patient != null) {
			return patient;
		}else {
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	@PostMapping("/createOtherAppointment")
	public ResponseEntity<?> createOtherAppointment(@RequestBody String request) throws JSONException, MessagingException{
		
		JSONObject json = new JSONObject(request);
		
		LocalDate appointmentDate = LocalDate.parse(json.getString("appointmentDate"));
		
		LocalTime appointmentTime = LocalTime.parse(json.getString("appointmentTime"));
		
		Ams_Procedures procedure = procedureRepo.findById(json.getInt("procedure"));
		
		AmsHospitalBranch branch = branchRepo.findById(json.getInt("branch"));
		
		Long patient = json.getLong("patient");
		
		 JSONArray doctors =  json.getJSONArray("doctors");
		
		List<User> doctorsList = new ArrayList<User>();
		
		for(int i = 0; i < doctors.length(); i++) {
			
			JSONObject doctor = (JSONObject) doctors.get(i);
			
			User doctorUser = doctorRepo.findById(doctor.getLong("doctor"));
			
			doctorsList.add(doctorUser);
		}
		
		if(procedure != null && branch != null) {
			

			OtherAppointments otherAppointment = new OtherAppointments(json.getString("fullName"), json.getString("emailId") ,  json.getString("mobileNumber"), json.getDouble("age"), (byte) 1, json.getString("mrdNumber"), appointmentDate, appointmentTime, true, (byte) 0, false, json.getString("remarks"));
			
	
			otherAppointment.setProcedure(procedure);
			otherAppointment.setBranch(branch);
			
//			send Mail to Doctors
			this.sendMailtoDoctors(doctorsList, branch, json.getString("fullName"), appointmentDate, appointmentTime, procedure, json.getString("mrdNumber"));
			
			otherAppointment.setDoctors(doctorsList);
			
			String msg = "Dear "+json.getString("fullName")+",\r\n" + 
					"your "+procedure.getProcedures()+" is scheduled on "+otherAppointment.getAppointmentDate()+" at "+otherAppointment.getAppointmentTime()+" at our "+otherAppointment.getBranch().getBranchName()+" branch,\r\n" + 
					"Kindly carry your investigation reports along with your file.";
			
			String phoneNumber = json.getString("mobileNumber");
			
			
			
			if(patient != 0) {
				Ams_patient_users patientUser = userrepo.findById(patient);
				
				otherAppointment.setPatient(patientUser);
				
				
				messageService.sendMessage(msg, phoneNumber);
				
				this.sendMailtoPatients(patientUser.getEmail(), json.getString("fullName"), doctorsList, branch, procedure, json.getString("mrdNumber"), appointmentDate, appointmentTime);
				
				otherapprepo.save(otherAppointment);
				
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
				
			}else{
				
				this.sendMailtoPatients(json.getString("emailId"), json.getString("fullName"), doctorsList, branch, procedure, json.getString("mrdNumber"), appointmentDate, appointmentTime);
				otherapprepo.save(otherAppointment);
				messageService.sendMessage(msg, phoneNumber);
				return new ResponseEntity<>(HttpStatus.ACCEPTED);
			}
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	public void sendMailtoDoctors(List<User> doctors, AmsHospitalBranch branch, String patientName, LocalDate date, LocalTime time, Ams_Procedures procedure, String mrdNumber)throws MessagingException{
		MimeMessage mail = javamailSender.createMimeMessage();
		
		String docListing = "";
		
		for(User doc: doctors) {
			docListing += "<li>"+doc.getUsername()+"</li>";
		}
		
		
		for(User doctor : doctors) {
			String email = doctor.getEmail();
			String subject = "Hi "+doctor.getUsername()+", Appointment of "+patientName+" on "+date;
			String text = "<html>"
					+"<body> <h2>GREETINGS !!! from BANGALORE NETHRALAYA</h2>"
					+ "<p>PATIENT NAME : <b> "+ patientName +" </b> <br/>"
					+ "MRD NUMBER : <b> "+ mrdNumber +" </b> <br/>"
					+ "PROCEDURE SUGGESTED : <b> "+ procedure.getProcedures() +" </b> <br/>"
					+ "DATE & TIME OF APPOINTMENT : <b> "+ date +" - "+time+" </b> <br/>"
					+ "BRANCH NAME : <b> "+ branch.getBranchName() +" </b> </p> <hr/>"
					+ "<h5>DOCTORS AT PROCEDURE</h5>"
					+ "<ul>"+docListing+ "</ul>"
					+ "</body>"
					+ "</html>";
			
			MimeMessageHelper helper = new MimeMessageHelper(mail, true);
			helper.setFrom(emailConfig.getUsername());
			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(text, true);
			javamailSender.send(mail);
		}
		
		
	}
	
	public void sendMailtoPatients(String email, String patientName, List<User> doctors, AmsHospitalBranch branch, Ams_Procedures procedure, String mrdNumber,LocalDate date, LocalTime time)throws MessagingException{
		
		String doctorsList = "";
		
		for(User doctor: doctors) {
			doctorsList += "<li>"+doctor.getUsername()+"</li>";
		}
		
		String subject = "Hi "+patientName+", your Appointment scheduled at : "+date+" - "+time +"At our Branch : "+branch.getBranchName();
		
		String text = "<html>"
				+"<body> <h2>GREETINGS !!! from BANGALORE NETHRALAYA</h2>"
				+ "<p>PATIENT NAME : <b> "+ patientName +" </b> <br/>"
				+ "MRD NUMBER : <b> "+ mrdNumber +" </b> <br/>"
				+ "PROCEDURE SUGGESTED : <b> "+ procedure.getProcedures() +" </b> <br/>"
				+ "DATE & TIME OF APPOINTMENT : <b> "+ date +" - "+time+" </b> <br/>"
				+ "BRANCH NAME : <b> "+ branch.getBranchName() +" </b> </p> <hr/>"
				+ "<h5> DOCTORS AT PROCEDURE</h5>"
				+ "<ul>"+doctorsList+ "</ul>"
				+ "</body>"
				+ "</html>";
		
		MimeMessage mail = javamailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mail, true);
		helper.setFrom(emailConfig.getUsername());
		helper.setTo(email);
		helper.setSubject(subject);
		helper.setText(text, true);
		javamailSender.send(mail);
	}
	
	
	@SuppressWarnings("unlikely-arg-type")
	@GetMapping("/DoctorWiseOtherAppoinrtments")
	public Iterable<OtherAppointments> DoctorWiseOtherAppoinrtments(HttpServletRequest req){
		String token = req.getHeader(tokenHeader).substring(7);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		User doctor = doctorRepo.findByEmail(username);
		return otherapprepo.findAll().stream().filter(i-> i.getDoctors().contains(doctor)).collect(Collectors.toList());
	}
	
	
	@GetMapping("/getdateWiseOtherAppointment/{date}")
	public Iterable<OtherAppointments> getdateWiseOtherAppointment(@PathVariable String date){
		LocalDate entryDate = LocalDate.parse(date);
	return otherapprepo.findAll().stream().filter(i->i.getAppointmentDate().equals(entryDate)).collect(Collectors.toList());
	}
	
	@GetMapping("/getdateWiseOtherAppointmentdoctorWise/{date}")
	public Iterable<OtherAppointments> getdateWiseOtherAppointmentdoctorWise(@PathVariable String date,HttpServletRequest req){
		LocalDate entryDate = LocalDate.parse(date);
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = doctorRepo.findByEmail(username);
	return otherapprepo.findAll().stream().filter(i->i.getAppointmentDate().equals(entryDate) && i.getDoctors() == user).collect(Collectors.toList());
	}
	
	@GetMapping("/getMonthlyOtherAppointmentdoctorWise/{date}")
	public Iterable<OtherAppointments> getMonthlyAppointmentdoctorWise(@PathVariable("date") String date,HttpServletRequest req)throws ParseException{		
        LocalDate entrydatee = LocalDate.parse(date);
        int month = entrydatee.getMonthValue();
        String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = doctorRepo.findByEmail(username);
        List<OtherAppointments> result = new ArrayList<OtherAppointments>();
     Iterable<OtherAppointments> app = otherapprepo.findAll().stream().filter(i->(i.isActive()==true) && i.getDoctors() == user ).collect(Collectors.toList());
	for (OtherAppointments amsAppointments : app) {
		LocalDate d1 = amsAppointments.getAppointmentDate();
		int m1 = d1.getMonthValue();
		if(m1 == month) {
			result.add(amsAppointments);
		}
	}
     
     return result;
	}
	
	@GetMapping("/getMonthlyOtherAppointment/{date}")
	public Iterable<OtherAppointments> getMonthlyAppointment(@PathVariable("date") String date)throws ParseException{		
        LocalDate entrydatee = LocalDate.parse(date);
        int month = entrydatee.getMonthValue();
        List<OtherAppointments> result = new ArrayList<OtherAppointments>();
     Iterable<OtherAppointments> app = otherapprepo.findAll().stream().filter(i->(i.isActive()==true) ).collect(Collectors.toList());
	for (OtherAppointments amsAppointments : app) {
		LocalDate d1 = amsAppointments.getAppointmentDate();
		int m1 = d1.getMonthValue();
		if(m1 == month) {
			result.add(amsAppointments);
		}
	}
     
     return result;
	}
	
	
	@PostMapping("/getInBetweenDatesOtherAppointment")
	public ResponseEntity<?> getInBetweenDatesOtherAppointment(@RequestBody String data) throws JSONException{
		JSONObject jsonObj = new JSONObject(data);
		LocalDate fromDate = LocalDate.parse(jsonObj.getString("FromDate"));
		LocalDate toDate = LocalDate.parse(jsonObj.getString("ToDate"));
		List<OtherAppointments> appointments = otherapprepo.findAll().stream().filter(i->i.isActive() == true &&  i.getAppointmentDate().isAfter(fromDate) && i.getAppointmentDate().isBefore(toDate) || i.getAppointmentDate().equals(fromDate) || i.getAppointmentDate().equals(toDate)).collect(Collectors.toList());
		return  ResponseEntity.ok(appointments);
	} 
	
	@PostMapping("/getInBetweenDatesOtherAppointmentdoctorWise")
	public ResponseEntity<?> getInBetweenDatesOtherAppointmentdoctorWise(@RequestBody String data,HttpServletRequest req) throws JSONException{
		JSONObject jsonObj = new JSONObject(data);
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = doctorRepo.findByEmail(username);
		LocalDate fromDate = LocalDate.parse(jsonObj.getString("FromDate"));
		LocalDate toDate = LocalDate.parse(jsonObj.getString("ToDate"));
		List<OtherAppointments> appointments = otherapprepo.findAll().stream().filter(i->i.isActive() == true && i.getDoctors() == user &&  i.getAppointmentDate().isAfter(fromDate) && i.getAppointmentDate().isBefore(toDate) || i.getAppointmentDate().equals(fromDate) || i.getAppointmentDate().equals(toDate)).collect(Collectors.toList());
		return  ResponseEntity.ok(appointments);
	} 
	
	@GetMapping("/getfutureOtherAppointment")
	public Iterable<OtherAppointments> getfutureOtherAppointment(){
		LocalDate entryDate = LocalDate.now();
	return otherapprepo.findAll().stream().filter(i->i.isActive()==true && i.getAppointmentDate().equals(entryDate)|| i.getAppointmentDate().isAfter(entryDate)).collect(Collectors.toList());
	}
	@GetMapping("/getFutureOtherAppointmentdoctorWise")
	public Iterable<OtherAppointments>getFutureOtherAppointmentdoctorWise(HttpServletRequest req){
		LocalDate entryDate = LocalDate.now();
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = doctorRepo.findByEmail(username);
	return otherapprepo.findAll().stream().filter(i->i.isActive()==true && i.getAppointmentDate().equals(entryDate)|| i.getAppointmentDate().isAfter(entryDate) && i.getDoctors() == user).collect(Collectors.toList());
	}

}
