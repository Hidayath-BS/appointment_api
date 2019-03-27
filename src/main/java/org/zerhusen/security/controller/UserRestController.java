package org.zerhusen.security.controller;


import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.security.Authority;
import org.zerhusen.model.security.User;

import org.zerhusen.security.JwtTokenUtil;
import org.zerhusen.security.repository.AuthorityRepository;
import org.zerhusen.security.repository.Rolerepository;
import org.zerhusen.security.repository.UserRepository;
import org.zerhusen.service.PasswordEncoderCustom;

@CrossOrigin(origins="*")
@RestController
public class UserRestController {
	
	@Autowired
	private UserRepository userrepo;

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
    private Rolerepository rolerepo;
    
    @Autowired
	PasswordEncoderCustom pswd;
    
    @Autowired
    AuthorityRepository authority;

    
    @GetMapping("/User")
    public User getCurruntUser(HttpServletRequest req) {
    	String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByEmail(username);
    	return user;
    }

    
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public @ResponseBody Iterable<User> getAllUsers() {
    	return userrepo.findAll().stream().filter(i-> i.getEnabled()==true).collect(Collectors.toList());
    }
    
    @GetMapping("/getUsers")
	public Iterable<User> getUsers(){
	return userRepo.findAll().stream().filter(i-> i.getEnabled()==true).collect(Collectors.toList());
}

@GetMapping(value="/getRoles")
public Iterable<Authority> getRoles(){
	return rolerepo.findAll(); 
}

@PostMapping(value="/addUsers")
public ResponseEntity<?> addUser(@RequestBody String users)throws JSONException, ParseException{
	
	JSONObject jsonobj = new JSONObject(users);
	
	Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	User userExists = userRepo.findByMobilenumber(jsonobj.getString("email"));
	if(userExists!=null) {
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	else {
		Authority role = authority.findById(jsonobj.getLong("role"));
		
		User admin = new User(jsonobj.getString("mobilenumber"),pswd.passwordEncoder().encode(jsonobj.getString("password")),
				     true,jsonobj.getString("username"),true,jsonobj.getString("email"), timestamp);
		if(role!=null) {
			admin.setAuthorities(new HashSet<Authority>(Arrays.asList(role)));
			userRepo.save(admin);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}
}

@PutMapping("/deleteUsers")
public ResponseEntity<?> deleteUsers(@RequestBody String user)throws JSONException{
	
	JSONObject jsonobj = new JSONObject(user);
	
	User userExists = userRepo.findById(jsonobj.getLong("userId"));
	
	if(userExists!=null) {
		userExists.setEnabled(false);;
		userRepo.save(userExists);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	else {
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
}


}
