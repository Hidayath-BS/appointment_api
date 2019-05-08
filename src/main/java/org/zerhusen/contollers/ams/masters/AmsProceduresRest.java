package org.zerhusen.contollers.ams.masters;

import java.text.ParseException;
import java.util.stream.Collectors;

import javax.websocket.server.PathParam;

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
import org.zerhusen.model.ams.Ams_Procedures;
import org.zerhusen.repository.ams.AmsProceduresRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/masters")
public class AmsProceduresRest {
	
	@Autowired
	private AmsProceduresRepository amsprorepo;
	
	@GetMapping(value="/getProcedures")
	public Iterable<Ams_Procedures> getAllProcedures(){
		return amsprorepo.findAll().stream().filter(i->i.isActive()==true).collect(Collectors.toList());
	}
	
	@GetMapping("/getAllProcedures/{id}")
	public Ams_Procedures getProcedures(@PathVariable("id") int id) {
		return amsprorepo.findById(id);
	}
	
	@PostMapping(value="/addProcedures")
	public ResponseEntity<?> addProcedures(@RequestBody String procedures)throws JSONException,ParseException
	{
		JSONObject jsonobj = new JSONObject(procedures);
		Ams_Procedures procedure = new Ams_Procedures(jsonobj.getString("procedures"),true);
		amsprorepo.save(procedure);
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	
	@PutMapping(value="/editProcedures")
	public ResponseEntity<?> editProcedures(@RequestBody String request)throws JSONException
	{
		JSONObject req = new JSONObject(request);
		
		int id = req.getInt("proId");
		
		String procedurre = req.getString("procedures");
		
		Ams_Procedures pro = amsprorepo.findById(id);
		
		if(pro!=null) {
			pro.setProcedures(procedurre);
			amsprorepo.save(pro);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@PutMapping(value="/deleteProcedures")
	public ResponseEntity<?> deleteProcedures(@RequestBody String request)throws JSONException
	{
		JSONObject jsonobj = new JSONObject(request);
		
		Ams_Procedures procedures = amsprorepo.findById(jsonobj.getInt("procedures"));
		
		if(procedures!=null) {
			procedures.setActive(false);
			amsprorepo.save(procedures);
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

}
