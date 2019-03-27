package org.zerhusen.contollers.ams;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/getmyappoinntments/{date}")
	public Iterable<AmsAppointments> getAppointments(@PathVariable("date") String date, HttpServletRequest req) throws ParseException{
		String token = req.getHeader(tokenHeader).substring(7);
		    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByEmail(username);
		LocalDate getdate= LocalDate.parse(date);
		return appointmentrepo.findAll().stream().filter(i->i.getSlot().getDoctor().equals(user)&&i.getDate().equals(getdate)).collect(Collectors.toList());
		
	}
	

}
