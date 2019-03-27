package org.zerhusen.contollers.ams;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsPatientQueries;
import org.zerhusen.model.ams.AmsQueryResponse;
import org.zerhusen.repository.ams.AmsPatientQueryRepository;
import org.zerhusen.repository.ams.AmsQueryResponseRepository;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/patientQueries")
public class PatientQueriesRest {

	@Autowired
	public AmsPatientQueryRepository queryRepo;
	
	@Autowired
	public AmsQueryResponseRepository queryResponseRepo;
	
	@GetMapping("/newQueries")
	public Iterable<AmsPatientQueries> getNewQueries(){
		return queryRepo.findAll().stream().filter(i-> i.getStatus() == (byte) 1  && i.isActive() == true).collect(Collectors.toList());
	}
	
	@GetMapping("/queryDetails/{id}")
	public AmsPatientQueries getQueryDetails(@PathVariable("id") int id) {
		AmsPatientQueries query = queryRepo.findById(id);
		return query;
	}
	
	@GetMapping("/answeredQueries")
	public Iterable<AmsPatientQueries> getAnsweredQuries() {
		return queryRepo.findAll().stream().filter(i-> i.getStatus() == (byte) 2  && i.isActive() == true).collect(Collectors.toList());
	}
	
	@GetMapping("/queryResponse/{query}")
	public Iterable<AmsQueryResponse> getResponseOnQuery(@PathVariable("query") int qid){
		AmsPatientQueries query = queryRepo.findById(qid);
		return queryResponseRepo.findAll().stream().filter(i-> i.getQuery().equals(query) && i.isActive()==true).collect(Collectors.toList());
	}
	
	@PutMapping("/deletQuery/{id}")
	public ResponseEntity<?> deleteQuery(@PathVariable("id") int id){
		AmsPatientQueries query = queryRepo.findById(id);
		if(query != null) {
			
			query.setActive(false);
			queryRepo.save(query);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@PostMapping("/newResponse")
	public ResponseEntity<?> uploadResponse(@RequestBody String request) throws JSONException{
		JSONObject json = new JSONObject(request);
		
		AmsPatientQueries query = queryRepo.findById(json.getInt("query"));
		
		LocalDate responseDate = LocalDate.now();
		
		LocalTime responseTime = LocalTime.now();
		
		if(query != null) {
			
			query.setStatus((byte) 2);
			queryRepo.save(query);
			AmsQueryResponse response = new AmsQueryResponse(json.getString("response"), responseDate, responseTime, true);
			response.setQuery(query);
			queryResponseRepo.save(response);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
}
