package org.zerhusen.repository.ams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zerhusen.model.ams.OtherAppointments;


@Repository
public interface OtherAppointmentsRepository extends JpaRepository<OtherAppointments, Integer>  {

	public OtherAppointments findById(int id);
}
