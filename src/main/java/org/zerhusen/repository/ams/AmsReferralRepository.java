package org.zerhusen.repository.ams;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerhusen.model.ams.AmsReferralDoctors;

@Repository
public interface AmsReferralRepository extends JpaRepository<AmsReferralDoctors, Integer>{

	
	AmsReferralDoctors findById(int id);
	
	List<AmsReferralDoctors> findByFullName(String fullName);
}
