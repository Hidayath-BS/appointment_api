package org.zerhusen.security.controller;


import java.text.ParseException;

import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import org.zerhusen.model.security.User;

import org.zerhusen.security.JwtTokenUtil;

import org.zerhusen.security.repository.UserRepository;

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


    
    @GetMapping("/User")
    public User getCurruntUser(HttpServletRequest req) {
    	String token = req.getHeader(tokenHeader).substring(7);
    	String username = jwtTokenUtil.getUsernameFromToken(token);
    	User user = userRepo.findByMobilenumber(username);
    	return user;
    }

    
    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody Iterable<User> getAllUsers() {
    	return userrepo.findAll().stream().filter(i-> i.getEnabled()==true).collect(Collectors.toList());
    }

}
