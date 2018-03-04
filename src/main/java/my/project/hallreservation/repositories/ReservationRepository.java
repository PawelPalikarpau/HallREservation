package my.project.hallreservation.repositories;

import my.project.hallreservation.domain.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAllByHallId(Long hallId);
    List<Reservation> findAllByStartDateGreaterThanAndHallId(LocalDateTime date, Long hallId);
    List<Reservation> findAllByHallIdAndCustomerId(Long hallId, Long customerId);
    Reservation findByHallIdAndCustomerIdAndStartDateAndEndDate(Long hallId, Long customerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Reservation> findAllByStartDateBetweenAndHallId(LocalDateTime startDate, LocalDateTime endDate, Long hallId);
    List<Reservation> findAllByEndDateBetweenAndHallId(LocalDateTime startDate, LocalDateTime endDate, Long hallId);
    List<Reservation> findAllByEndDateIsLessThan(LocalDateTime now);
}
