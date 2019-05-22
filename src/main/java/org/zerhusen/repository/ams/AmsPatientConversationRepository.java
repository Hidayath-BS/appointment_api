package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerhusen.model.ams.AmsPatientConversation;


@Repository
public interface AmsPatientConversationRepository extends JpaRepository<AmsPatientConversation, Integer> {

	
	public AmsPatientConversation findById(int id);
}
