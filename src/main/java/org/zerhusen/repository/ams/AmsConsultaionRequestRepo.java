package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerhusen.model.ams.ConsultationRequests;

public interface AmsConsultaionRequestRepo extends JpaRepository<ConsultationRequests, Integer> {
	ConsultationRequests findById(int id);
}
