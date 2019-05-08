package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerhusen.model.ams.ConsultationRequests;


@Repository
public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequests, Integer>{

	public ConsultationRequests findById(int id);
}
