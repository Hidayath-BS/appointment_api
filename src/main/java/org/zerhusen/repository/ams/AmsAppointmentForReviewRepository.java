package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerhusen.model.ams.AmsAppointments;
import org.zerhusen.model.ams.AmsAppointmentsForReview;

@Repository
public interface AmsAppointmentForReviewRepository extends JpaRepository<AmsAppointmentsForReview, Integer>{

 	public AmsAppointmentsForReview findByAppointment(AmsAppointments appointment);
 	
 	public AmsAppointmentsForReview findById(int id);
}
