package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerhusen.model.ams.Ams_Procedures;


public interface AmsProceduresRepository extends JpaRepository<Ams_Procedures, Integer> {
	
	Ams_Procedures findById(int id);

}
