package my.project.hallreservation.controllers;

import java.time.LocalDateTime;
import java.util.List;

import my.project.hallreservation.domain.*;
import my.project.hallreservation.repositories.CustomerRepository;
import my.project.hallreservation.repositories.HallRepository;
import my.project.hallreservation.service.DatabaseOperator;
import my.project.hallreservation.service.TableGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RestWebController {

    private final TableGenerator tableGenerator;
    private final HallRepository hallRepository;
    private final CustomerRepository customerRepository;
    private final DatabaseOperator databaseOperator;

    @Autowired
    public RestWebController(TableGenerator tableGenerator,
                             HallRepository hallRepository,
                             CustomerRepository customerRepository,
                             DatabaseOperator databaseOperator) {
        this.tableGenerator = tableGenerator;
        this.hallRepository = hallRepository;
        this.customerRepository = customerRepository;
        this.databaseOperator = databaseOperator;
    }

    @RequestMapping(value = "/getHalls", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Hall>> getHalls() {
        return new ResponseEntity<>(hallRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/getCustomers", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Customer>> getCustomers() {
        return new ResponseEntity<>(customerRepository.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = "/generateTable", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<List<Day>> getDayArray(){
        return new ResponseEntity<>(tableGenerator.generateDayArray(), HttpStatus.OK);
    }

    @RequestMapping(value= "/getAllReservations", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public List<String> getAllReservations(@RequestBody Hall hall) {
        return databaseOperator.getAllReservations(hall);
    }

    @RequestMapping(value= "/getCustomReservations", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public List<ReservationEntity> getAllReservations(@RequestBody ReservationEntity entity) {
        return databaseOperator.getCustomReservations(entity);
    }

    @RequestMapping(value = "/makeReservation", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public ReservationEntity makeReservation(@RequestBody ReservationEntity entity) {
        databaseOperator.makeReservation(entity);
        return entity;
    }

    @RequestMapping(value = "/removeReservation", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public ReservationEntity removeReservation(@RequestBody ReservationEntity entity) {
        databaseOperator.removeReservation(entity);
        return entity;
    }

    @RequestMapping(value = "/getBetweenDates", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public List<LocalDateTime> getNearestDate(@RequestBody ReservationEntity entity) {
        return databaseOperator.getBetweenDates(entity);
    }

    @RequestMapping(value = "/updateDatabase", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Void> updateDatabase() {
        databaseOperator.updateDatabase();
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}