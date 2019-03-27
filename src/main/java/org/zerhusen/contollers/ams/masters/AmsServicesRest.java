package org.zerhusen.contollers.ams.masters;

import java.text.ParseException;
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
import org.zerhusen.model.ams.Ams_Services_available;
import org.zerhusen.repository.ams.AmsServicesRepository;

@CrossOrigin(origins="*")
@RestController
@RequestMapping(value="/masters")
public class AmsServicesRest {

	@Autowired
	private AmsServicesRepository amsserverepo;
	
	@GetMapping("/getAllServices")
	public Iterable<Ams_Services_available> getAllServices(){
		return amsserverepo.findAll().stream().filter(i->i.isActive()==true).collect(Collectors.toList());
	}
	
	@GetMapping("/getServices/{id}")
		public Ams_Services_available getServices(@PathVariable("id") int id)
		{
			return amsserverepo.findById(id);
		}
	
	
	@PostMapping("/addServices")
	public ResponseEntity<?> addServices(@RequestBody String service)throws JSONException,ParseException{
		
		JSONObject jsonobj = new JSONObject(service);
		Ams_Services_available services = new Ams_Services_available(jsonobj.getString("services"),true);
		amsserverepo.save(services);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/editServices")
	public ResponseEntity<?> editServices(@RequestBody String request)throws JSONException{
		JSONObject req = new JSONObject(request);
		
		int id = req.getInt("serveId");
		
		String service = req.getString("services");
		
		Ams_Services_available serve = amsserverepo.findById(id);
		
		if(serve!=null) {
			serve.setServices(service);
			amsserverepo.save(serve);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@PutMapping("/deleteServices")
	public ResponseEntity<?> deleteServices(@RequestBody String services)throws JSONException{
		JSONObject jsonobj = new JSONObject(services);
		
		Ams_Services_available service = amsserverepo.findById(jsonobj.getInt("services"));
		
		if(service!=null) {
			service.setActive(false);
			amsserverepo.save(service);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

}
