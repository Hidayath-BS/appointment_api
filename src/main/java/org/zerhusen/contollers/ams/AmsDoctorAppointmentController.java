package org.zerhusen.contollers.ams;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.security.User;
import org.zerhusen.repository.ams.AmsAppointmentsRepository;
import org.zerhusen.security.JwtTokenUtil;
import org.zerhusen.security.repository.UserRepository;



@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins="*")
public class AmsDoctorAppointmentController {
	@Autowired
	private AmsAppointmentsRepository appointmentrepo;
	
   

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private AmsAppointmentsRepository appointmentRepo;


	
	@GetMapping("/getmyappoinntments/{date}")
	public Iterable<AmsAppointments> getAppointmentDoctorwise(@PathVariable("date") String date,HttpServletRequest req)throws ParseException{		
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByEmail(username);
		LocalDate entrydate = LocalDate.parse(date);
		return appointmentRepo.findAll().stream().filter(i->(i.isActive()==true)&& i.getSlot().getDoctor() == user &&(i.getDate().equals(entrydate)) && (i.isRescheduled()==false)).collect(Collectors.toList());
	}
	
	
	@GetMapping("/getMonthwiseAppointmentDoctorwise/{date}")
	public Iterable<AmsAppointments> getMonthwiseAppointmentDoctorwise(@PathVariable("date") String date,HttpServletRequest req)throws ParseException{		
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByEmail(username);
		LocalDate entrydatee = LocalDate.parse(date);
		int month = entrydatee.getMonthValue();
		List<AmsAppointments> appoint = new ArrayList<AmsAppointments>();
		Iterable<AmsAppointments> appo= appointmentRepo.findAll().stream().filter(i->(i.isActive()==true)&& i.getSlot().getDoctor() == user && i.getSlot().getDoctor() == user && i.isCompleted()==false && (i.isRescheduled()==false)).collect(Collectors.toList());
	for (AmsAppointments amsAppointments : appo) {
		LocalDate obj = amsAppointments.getDate();
		int mon=obj.getMonthValue();
		if(mon==month) {
			appoint.add(amsAppointments);
		}
	}
	return appoint;
	}
	
	@PostMapping("/getBetweenDateAppiontmentDoctorwise")
	public ResponseEntity<?> getBetweenDateAppiontmentDoctorwise(@RequestBody String data,HttpServletRequest req) throws JSONException{
		JSONObject ob = new JSONObject(data);
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByEmail(username);
		LocalDate from = LocalDate.parse(ob.getString("fromDate"));
		LocalDate to = LocalDate.parse(ob.getString("toDate"));
		List<AmsAppointments> appoin = appointmentRepo.findAll().stream().filter(i->(i.isActive()==true) && i.isCompleted()==false && (i.isRescheduled()==false) && i.getDate().isAfter(from) && i.getDate().isBefore(to) || i.getDate().equals(from) || i.getDate().equals(to)).collect(Collectors.toList());
        return ResponseEntity.ok(appoin);
		
	}
	
	@GetMapping("/getfuturemyappointments")
	public Iterable<AmsAppointments> getFutureMyAppointments(HttpServletRequest req)throws ParseException{		
		String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByEmail(username);
		LocalDate entrydate = LocalDate.now();
		return appointmentRepo.findAll().stream().filter(i->(i.isActive()==true)&& i.getSlot().getDoctor() == user &&(i.getDate().isAfter(entrydate)|| i.getDate().equals(entrydate))  && (i.isRescheduled()==false)).collect(Collectors.toList());
	}


	
}
