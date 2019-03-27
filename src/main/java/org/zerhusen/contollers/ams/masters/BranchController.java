package org.zerhusen.contollers.ams.masters;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zerhusen.model.ams.AmsHospitalBranch;
import org.zerhusen.repository.ams.AmsHospitalBranchRepository;

@RestController
@RequestMapping("/masters")
@CrossOrigin(origins="*")
public class BranchController {
	
	@Autowired
	public AmsHospitalBranchRepository branchRepo;
	
	@GetMapping("/getBranches")
	public @ResponseBody Iterable<AmsHospitalBranch> getAllBranches(){
		return branchRepo.findAll().stream().filter(i-> i.isActive()==true).collect(Collectors.toList());
	}
	
	
//	To add a branch
	@PostMapping(value="/addBranch")
	public ResponseEntity<?> addNewBranch(@RequestBody String branch)throws JSONException{
		JSONObject jsonobj= new JSONObject(branch);
		AmsHospitalBranch branch1 = new AmsHospitalBranch(jsonobj.getString("branchName"),true);
		branchRepo.save(branch1);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	 @GetMapping("/getBranch/{id}")
	    public AmsHospitalBranch getBranch(@PathVariable("id") int id)
	    {
	    	return branchRepo.findById(id);
	    }
	    
	    

	@PutMapping("/editbranches")
	    public ResponseEntity<?> editbranches(@RequestBody String request)throws JSONException
	    {
	    	JSONObject req = new JSONObject(request);
	    	
	    	int id = req.getInt("branchid");
	    	
	    	String branchNa = req.getString("branchName");
	    	
	    	AmsHospitalBranch branc = branchRepo.findById(id);
	    	
	    	if(branc!=null) {
	    		branc.setBranchName(branchNa);
	    		branchRepo.save(branc);
	    		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	    	}
	    	else {
	    		return new ResponseEntity<>(HttpStatus.CONFLICT);
	    	}
	    }


//	To delete a branch
	@PutMapping("/deleteBranch")
	public @ResponseBody ResponseEntity<?> deleteBranch(@RequestBody String branch) throws JSONException{
		JSONObject json = new JSONObject(branch);
		
		AmsHospitalBranch branch_type = branchRepo.findById(json.getInt("branchName"));
		
		if(branch_type != null) {
			branch_type.setActive(false);
			branchRepo.save(branch_type);
			
			return new ResponseEntity<>(HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
}
}