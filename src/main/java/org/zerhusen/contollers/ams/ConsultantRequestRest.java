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
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsPatientQueries;
import org.zerhusen.model.ams.AmsQueryResponse;
import org.zerhusen.model.ams.ConsultationRequestResponses;
import org.zerhusen.model.ams.ConsultationRequests;
import org.zerhusen.repository.ams.AmsConsultaionRequestRepo;
import org.zerhusen.repository.ams.ConsultantResponseRepo;

@RestController
@CrossOrigin("*")

public class ConsultantRequestRest {

	@Autowired
	AmsConsultaionRequestRepo creqRepo;
	@Autowired
	ConsultantResponseRepo responseRepo;
	
	
	@GetMapping("/ConsultationRequest")
	public Iterable<ConsultationRequests> ConsultationRequest(){
		return creqRepo.findAll().stream().filter(i-> i.getStatus() == (byte) 1  && i.isActive() == true).collect(Collectors.toList());
	}
	@GetMapping("/ConsultationDetails/{id}")
   public ConsultationRequests ConsultationDetails(@PathVariable int id ) {
	return creqRepo.findById(id);
		
	}
	@GetMapping("/ConsultationDetailsResponse/{request}")
	public Iterable<ConsultationRequestResponses> ConsultationDetailsResponse(@PathVariable("request") int id){
		ConsultationRequests request = creqRepo.findById(id);
		return responseRepo.findAll().stream().filter(i-> i.getRequest().equals(request) && i.isActive()==true).collect(Collectors.toList());
	}

	
	@PostMapping(value="/AddResponse")
	public ResponseEntity<?> ConsultationRequestResponses (@RequestBody String reque)throws JSONException {
		JSONObject oj=new JSONObject(reque);
		LocalTime responseTime= LocalTime.now();
		LocalDate responseDate = LocalDate.now();
		ConsultationRequests request=creqRepo.findById(oj.getInt("request"));
          if(request != null) {
			
        	  request.setStatus((byte) 2);
		
		creqRepo.save(request);
		ConsultationRequestResponses respon = new ConsultationRequestResponses(oj.getString("response"), responseDate, responseTime, true);
		
		respon.setRequest(request);
		
		responseRepo.save(respon);
		return new ResponseEntity<>(HttpStatus.OK);
}
else {
	return new ResponseEntity<>(HttpStatus.CONFLICT);
}
	}

	@GetMapping("/AnsweredResponseList")
	public Iterable<ConsultationRequests> ConsultationAnswer() {
		return creqRepo.findAll().stream().filter(i-> i.getStatus() == (byte) 2  && i.isActive() == true).collect(Collectors.toList());
	}

}
