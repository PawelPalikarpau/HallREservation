package my.project.hallreservation.service;

import my.project.hallreservation.domain.Customer;
import my.project.hallreservation.domain.Day;
import my.project.hallreservation.domain.Hall;
import my.project.hallreservation.repositories.CustomerRepository;
import my.project.hallreservation.repositories.HallRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class TableGenerator {

    private final HallRepository hallRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public TableGenerator(HallRepository hallRepository, CustomerRepository customerRepository) {
        this.hallRepository = hallRepository;
        this.customerRepository = customerRepository;
    }

    public List<Day> generateDayArray() {

        fillDatabase();

        List<Day> days = new ArrayList<>();
        Boolean alreadyAdded = false;

        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
            days.add(generateNewDay(dayIndex));

            for (int hourIndex = 0; hourIndex < 12; hourIndex++) {

                for (int minutesIndex = 0; minutesIndex < 60; minutesIndex += 15) {
                    LocalDateTime temp = LocalDateTime.of(
                            days.get(dayIndex).getDay().toLocalDate(),
                            days.get(dayIndex).getDay().toLocalTime().plusHours(hourIndex).plusMinutes(minutesIndex)
                    );

                    days.get(dayIndex).getHours().add(temp);
                    if (!alreadyAdded) days.get(dayIndex).getLeftInfo().add(addLeftInfo(temp));
                }
            }
            days.get(dayIndex).setTopInfo(addTopInfo(days.get(dayIndex).getDay()));
            alreadyAdded = true;
        }

        return days;
    }

    private Day generateNewDay(int dayIndex) {
        LocalDateTime now = LocalDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        Day newDay = new Day();
        newDay.setDay(now.plusDays(dayIndex));
        newDay.setHours(new ArrayList<>());
        newDay.setLeftInfo(new ArrayList<>());
        return newDay;
    }

    private String addLeftInfo(LocalDateTime date) {
        String output = "";

        output += (date.format(DateTimeFormatter.ofPattern("HH:mm"))) + " - ";
        output += ((date.plusMinutes(15)).format(DateTimeFormatter.ofPattern("HH:mm")));

        return output;
    }

    private String addTopInfo(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + " " + date.getDayOfWeek();
    }

    private void fillDatabase() {
        if (hallRepository.count() < 2) {
            hallRepository.save(new Hall("First Hall"));
            hallRepository.save(new Hall("Second Hall"));
        }

        if (customerRepository.count() < 2) {
            customerRepository.save(new Customer("John"));
            customerRepository.save(new Customer("Marry"));
        }
    }
}
