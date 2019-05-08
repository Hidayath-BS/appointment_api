package org.zerhusen.repository.ams;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerhusen.model.ams.ConsultationRequestResponses;
import org.zerhusen.model.ams.ConsultationRequests;


@Repository
public interface ConsultationRequestResponsesRepository extends JpaRepository<ConsultationRequestResponses, Integer> {

	public ConsultationRequestResponses findById(int id);
	
	public Collection<ConsultationRequestResponses> findByRequest(ConsultationRequests request);
}
