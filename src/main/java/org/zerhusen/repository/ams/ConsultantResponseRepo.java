package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerhusen.model.ams.ConsultationRequestResponses;


public interface ConsultantResponseRepo extends JpaRepository<ConsultationRequestResponses, Integer>{
	ConsultationRequestResponses findById(int id);
}
