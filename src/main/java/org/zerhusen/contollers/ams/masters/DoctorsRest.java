package org.zerhusen.contollers.ams.masters;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.security.Authority;
import org.zerhusen.model.security.User;
import org.zerhusen.security.repository.AuthorityRepository;
import org.zerhusen.security.repository.UserRepository;

@RestController
@RequestMapping("/masters")
@CrossOrigin(origins="*")
public class DoctorsRest {

	@Autowired
	public UserRepository doctorsRepo;
	
	@Autowired
	public AuthorityRepository role;
	
	@GetMapping("/getDoctors")
	public @ResponseBody Iterable<User> getDoctors(){	
		Authority auth = role.findByAuthority("DOCTOR");
		return doctorsRepo.findAll().stream().filter(i-> i.getAuthorities().contains(auth) && i.getEnabled().equals(true)).collect(Collectors.toList());
	}
}
