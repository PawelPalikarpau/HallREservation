import {Component, OnInit} from '@angular/core';
import {Http} from "@angular/http";
import {AppService} from "./app.service";
import { format, startOfDay, compareAsc, addMinutes } from 'date-fns';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private appService: AppService) {}

  numberOfColumns: any = [];
  numberOfRows: any = [];

  startDate: any;
  endDate: any;
  betweenDates: Date[];

  reservedDates: any;
  showMyReservations: boolean;

  customers: Customer[];
  customerSelected: Customer;

  customReservations: any[];
  customReservationsBetweenDates: any[];

  halls: Hall[];
  hallSelected: Hall;

  table: Day[] = [];

  ngOnInit() {
    this.loadAll();
  }

  loadAll() {
    this.appService.getTable()
      .then (
        days => {
          this.table = days;
          for (let i = 0; i < this.table.length; i++) {
            this.numberOfColumns.push(i);
          }

          for (let j = 0; j < this.table[0].hours.length; j++) {
            this.numberOfRows.push(j);
          }
        }
      );
    this.appService.getCustomers()
      .then (customers => {
          this.customers = customers;
        }
      );
    this.appService.getHalls()
      .then (halls => {
          this.halls = halls;
        }
      );
  }

  getAllReservations() {
    this.startDate = null;
    this.endDate = null;
    this.betweenDates = [];
    this.customReservations = [];
    this.customReservationsBetweenDates = [];

    if (this.hallSelected != null) {
      this.appService.getAllReservations(this.hallSelected)
        .then(reservations => {
          this.reservedDates = reservations.reduce((map, item) => {
            map[item] = false;
            return map;
          }, {});
        })
    }
    if (this.hallSelected != null && this.customerSelected != null) {
      let reservation: ReservationEntity = {
        customer: this.customerSelected,
        hall: this.hallSelected,
        startDate: null,
        endDate: null
      };
      this.appService.getCustomReservations(reservation)
        .then(arr => {
          this.customReservations = [];
          if (arr.length > 0) {
            arr.forEach(item => {
              let reservation = {
                startDate: this.convertBadDateToDate(item.startDate),
                endDate: this.convertBadDateToDate(item.endDate)
              };
              this.customReservations.push(reservation);
            });
            this.customReservationsBetweenDates = this.customReservations.reduce((map, item) => {
              let tempStartDate = item.startDate;
              let tempEndDate = item.endDate;
              tempStartDate.setMonth(tempStartDate.getMonth() - 1);
              tempEndDate.setMonth(tempEndDate.getMonth() - 1);

              while (compareAsc(tempStartDate, tempEndDate) < 0) {
                let key = format(tempStartDate, "YYYY-MM-DD HH:mm");
                map[key] = false;
                tempStartDate = addMinutes(tempStartDate, 15);
              }
              return map;
            }, {});
          }
        });
    }
    this.appService.updateDatabase()
      .then(response => {});
  }

  showReservations() {
    this.showMyReservations = !this.showMyReservations;
  }

  fillBetweenDates(date) {
    this.startDate = this.convertLocalDateTimeToDate(date);
    let reservation: ReservationEntity = {
      customer: null,
      hall: this.hallSelected,
      startDate: this.convertLocalDateTimeToBadDate(date),
      endDate: null
    };
    this.appService.getBetweenDates(reservation)
      .then( arr => {
        this.betweenDates = arr;
      });
  }

  isActiveButton(date): boolean {
    let tempDate = this.convertLocalDateTimeToDate(date);
    tempDate.setMonth(tempDate.getMonth() - 1);

    let now = new Date();
    if(compareAsc(tempDate, now) < 0) return false;

    let isActive;
    if (this.reservedDates != null) isActive = this.reservedDates[format(tempDate, "YYYY-MM-DD HH:mm")];

    return !(isActive === false);
  }

  getStyles(date, outputType: string) {
    let tempDate = this.convertLocalDateTimeToDate(date);
    tempDate.setMonth(tempDate.getMonth() - 1);
    let tempDateString = format(tempDate, "YYYY-MM-DD HH:mm");
    let isActive;
    if (this.reservedDates != null) isActive = this.reservedDates[tempDateString];
    let output: string;

    if (isActive == false) {
      if (outputType == 'class') output = 'btn btn-outline-danger';
      if (outputType == 'text') output = 'Reserved';
    } else {
      if (outputType == 'class') output = 'btn btn-outline-primary';
      if (outputType == 'text') output = 'Reserve';
    }

    if (this.showMyReservations) {
      let isMine = this.customReservationsBetweenDates[tempDateString];
      if (isMine === false) {
        if (outputType == 'class') output = 'btn btn-outline-success';
        if (outputType == 'text') output = 'Mine';
      }
    }
    return output;
  }

  makeReservation() {
    let reservation: ReservationEntity = {
      customer: this.customerSelected,
      hall: this.hallSelected,
      startDate: this.convertDateToBadDate(this.startDate),
      endDate: this.convertDateToBadDate(this.endDate)
    };
    this.appService.makeReservation(reservation)
      .then(response => {
        let answer = response;
        this.getAllReservations();
      });
  }

  removeReservation(dates) {
    let reservation: ReservationEntity = {
      customer: this.customerSelected,
      hall: this.hallSelected,
      startDate: this.convertDateToBadDate(dates.startDate),
      endDate: this.convertDateToBadDate(dates.endDate)
    };
    this.appService.removeReservation(reservation)
      .then(response => {
        let answer = response;
        this.getAllReservations();
      });
  }

  private convertLocalDateTimeToDate(date) : Date {
    let year = date.year;
    let month = date.monthValue;
    let day = date.dayOfMonth;
    let hour = date.hour;
    let minute = date.minute;
    return new Date(year, month, day, hour, minute)
  }

  private convertLocalDateTimeToBadDate(date) : BadDate {
    return {
      year: date.year,
      month: date.monthValue,
      day: date.dayOfMonth,
      hour: date.hour,
      minute: date.minute
    };
  }

  private convertDateToBadDate(date): BadDate {
    return {
      year: date.getFullYear(),
      month: date.getMonth(),
      day: date.getDate(),
      hour: date.getHours(),
      minute: date.getMinutes()
    };
  }

  private convertBadDateToDate(date: BadDate): Date {
      let year = date.year;
      let month = date.month;
      let day = date.day;
      let hour = date.hour;
      let minute = date.minute;
      return new Date(year, month, day, hour, minute, 0);
  }
}

export class Day {
  day: any;
  hours: any[];
  leftInfo: string[];
  topInfo: string;
}

export class Customer {
  id: number;
  firstName: string;
}

export class Hall {
  id: number;
  name: string;
}

export class ReservationEntity{
  customer: Customer;
  hall: Hall;
  startDate: BadDate;
  endDate: BadDate;
}

export class BadDate {
  year: number;
  month: number;
  day: number;
  hour: number;
  minute: number;
}
