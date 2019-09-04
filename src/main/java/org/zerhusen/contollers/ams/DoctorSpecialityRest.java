package org.zerhusen.contollers.ams;

import java.text.ParseException;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsDoctorSuggestion;
import org.zerhusen.model.ams.Ams_Services_available;
import org.zerhusen.model.security.User;
import org.zerhusen.repository.ams.AmsDoctorSuggestionRepository;
import org.zerhusen.repository.ams.AmsServicesRepository;
import org.zerhusen.security.repository.AuthorityRepository;
import org.zerhusen.security.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value="/masters")
public class DoctorSpecialityRest {
	
	@Autowired
	private AmsServicesRepository serrepo;
	
	@Autowired
	private AmsDoctorSuggestionRepository docrepo;
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private AuthorityRepository rolerepo;
	
	@PostMapping("/addDoctorSpeciality")
	public ResponseEntity<?> addDoctorSpeciality(@RequestBody String request)throws JSONException,ParseException
	{
		JSONObject jsonobj = new JSONObject(request);
		
	    User doc = userrepo.findById(jsonobj.getLong("doctors"));
	    
	    AmsDoctorSuggestion docsug = new AmsDoctorSuggestion(jsonobj.getString("speciality"), true);
	    
	    docsug.setDoctor(doc);
	       
	    docrepo.save(docsug);
	    
	    return new ResponseEntity<>(HttpStatus.ACCEPTED);
	
	}
	
	@GetMapping("/getAllDoctorSpeciality")
	public Iterable<AmsDoctorSuggestion> getDoctorSuggestion(){
		return docrepo.findAll().stream().filter(i->i.isActive()==true).collect(Collectors.toList());
	}
	
	@PutMapping("/deleteSpeciality")
	public ResponseEntity<?> deleteDoctorSpeciality(@RequestBody String req)throws JSONException
	{
		JSONObject jsonobj = new JSONObject(req);
		
		AmsDoctorSuggestion specialty = docrepo.findById(jsonobj.getInt("speciality"));
		
		if(specialty!=null) {
			specialty.setActive(false);
			docrepo.save(specialty);
			 return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	

}
