package my.project.hallreservation.service;

import my.project.hallreservation.domain.*;
import my.project.hallreservation.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hibernate.boot.model.relational.Namespace.ComparableHelper.compare;

@Service
public class DatabaseOperator {

    private final ReservationRepository reservationRepository;

    @Autowired
    public DatabaseOperator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<String> getAllReservations(Hall hall) {
        return generateStringArray(reservationRepository.findAllByHallId(hall.getId()));
    }

    public List<ReservationEntity> getCustomReservations(ReservationEntity entity) {
        List<Reservation> result = reservationRepository.findAllByHallIdAndCustomerId(entity.getHall().getId(), entity.getCustomer().getId());
        List<ReservationEntity> output = new ArrayList<>();

        if (result != null && result.size() > 0) {
            result.sort(Comparator.comparing(Reservation::getStartDate));
            for (Reservation reservation : result) {
                ReservationEntity item = new ReservationEntity();
                item.setStartDate(convertLocalDateTimeToBadDate(reservation.getStartDate()));
                item.setEndDate(convertLocalDateTimeToBadDate(reservation.getEndDate()));
                output.add(item);
            }
        }
        return output;
    }

    public List<LocalDateTime> getBetweenDates(ReservationEntity entity) {
        LocalDateTime clickedDate = convertBadDateToLocalDateTime(entity.getStartDate());
        LocalDateTime tempClickedDate = LocalDateTime.of(clickedDate.toLocalDate(), clickedDate.toLocalTime().plusMinutes(120));

        LocalDateTime nearestDate = LocalDateTime.of(clickedDate.toLocalDate(), clickedDate.toLocalTime().withHour(22). withMinute(0));

        List<Reservation> result = reservationRepository.findAllByStartDateGreaterThanAndHallId(clickedDate, entity.getHall().getId());

        if (result != null && result.size() > 0) {
            result.sort(Comparator.comparing(Reservation::getStartDate));
            LocalDateTime resultStartDate = result.get(0).getStartDate();
            if (compare(nearestDate, resultStartDate) > 0) {
                nearestDate = resultStartDate;
            }
        }

        if (compare(tempClickedDate, nearestDate) < 0) {
            nearestDate = tempClickedDate;
        }

        List<LocalDateTime> betweenDates = new ArrayList<>();
        while (compare(clickedDate, nearestDate) < 0) {
            clickedDate = clickedDate.plusMinutes(15);
            betweenDates.add(clickedDate);
        }

        return betweenDates;
    }

    public void makeReservation(ReservationEntity entity) {
        LocalDateTime tempStartDate = convertBadDateToLocalDateTime(entity.getStartDate());
        LocalDateTime tempEndDate = convertBadDateToLocalDateTime(entity.getEndDate());

        List<Reservation> first = reservationRepository.findAllByStartDateBetweenAndHallId(tempStartDate, tempEndDate.minusMinutes(15), entity.getHall().getId());
        List<Reservation> second = reservationRepository.findAllByEndDateBetweenAndHallId(tempStartDate.plusMinutes(15), tempEndDate, entity.getHall().getId());

        if (first.size() < 1 && second.size() < 1) {
            Reservation reservation = convertEntityToReservation(entity);
            reservationRepository.save(reservation);
        }
    }

    public void removeReservation(ReservationEntity entity) {
        entity.getStartDate().setMonth(entity.getStartDate().getMonth() + 1);
        entity.getEndDate().setMonth(entity.getEndDate().getMonth() + 1);

        Reservation reservation = new Reservation();
        reservation.setCustomerId(entity.getCustomer().getId());
        reservation.setHallId(entity.getHall().getId());
        reservation.setStartDate(convertBadDateToLocalDateTime(entity.getStartDate()));
        reservation.setEndDate(convertBadDateToLocalDateTime(entity.getEndDate()));

        Reservation toRemove = reservationRepository.findByHallIdAndCustomerIdAndStartDateAndEndDate(
                reservation.getHallId(),
                reservation.getCustomerId(),
                reservation.getStartDate(),
                reservation.getEndDate()
        );

        if (toRemove != null) reservationRepository.delete(toRemove);
    }

    public void updateDatabase() {
        List<Reservation> oldReservations = reservationRepository.findAllByEndDateIsLessThan(LocalDateTime.now());
        if (oldReservations.size() > 0) {
            reservationRepository.delete(oldReservations);
        }
    }

    private List<String> generateStringArray(List<Reservation> reservations) {
        List<String> stringReservations = new ArrayList<>();

        for (Reservation reservation : reservations) {
            LocalDateTime tempStartDate = reservation.getStartDate();
            LocalDateTime tempEndDate = reservation.getEndDate();

            while (compare(tempStartDate, tempEndDate) < 0) {
                stringReservations.add(tempStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                tempStartDate = tempStartDate.plusMinutes(15);
            }
        }

        return stringReservations;
    }

    private Reservation convertEntityToReservation(ReservationEntity entity) {
        Reservation reservation = new Reservation();
        reservation.setCustomerId(entity.getCustomer().getId());
        reservation.setHallId(entity.getHall().getId());
        reservation.setStartDate(convertBadDateToLocalDateTime(entity.getStartDate()));
        reservation.setEndDate(convertBadDateToLocalDateTime(entity.getEndDate()));
        return reservation;
    }

    private LocalDateTime convertBadDateToLocalDateTime(BadDate date) {
        Integer year = date.getYear();
        int month = date.getMonth();
        Integer day = date.getDay();
        Integer hour = date.getHour();
        Integer minute = date.getMinute();
        return LocalDateTime.of(year, month, day, hour, minute, 0, 0);
    }

    private BadDate convertLocalDateTimeToBadDate(LocalDateTime ldt) {
        BadDate newDate = new BadDate();
        newDate.setYear(ldt.getYear());
        newDate.setMonth(ldt.getMonthValue());
        newDate.setDay(ldt.getDayOfMonth());
        newDate.setHour(ldt.getHour());
        newDate.setMinute(ldt.getMinute());
        return newDate;
    }
}
