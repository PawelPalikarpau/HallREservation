import {Injectable} from '@angular/core';
import {Http} from "@angular/http";
import {Hall, Customer, Day} from "./app.component";
import 'rxjs/add/operator/map';

@Injectable()
export class AppService {

  private baseUrl = 'http://localhost:8080';
  private headers = new Headers({
    'Content-Type': 'application/json'
  });

  constructor(private http: Http) {}

  getTable(): Promise<Day[]> {
    return this.http.get(this.baseUrl + '/generateTable')
      .toPromise()
      .then(response => response.json() as Day[])
      .catch(this.handleError);
  }

  getCustomers(): Promise<Customer[]> {
    return this.http.get(this.baseUrl + '/getCustomers')
      .toPromise()
      .then(response => response.json() as Customer[])
      .catch(this.handleError);
  }

  getHalls(): Promise<Hall[]> {
    return this.http.get(this.baseUrl + '/getHalls')
      .toPromise()
      .then(response => response.json() as Hall[])
      .catch(this.handleError)
  }

  getAllReservations(hallSelected: Hall): Promise<string[]> {
    return this.http.post(this.baseUrl + '/getAllReservations', hallSelected)
      .toPromise()
      .then(response => {
        return response.json() as string[];
      })
      .catch(this.handleError)
  }

  getCustomReservations(reservation): Promise<any[]> {
    return this.http.post(this.baseUrl + '/getCustomReservations', reservation)
      .toPromise()
      .then(response => {
        return response.json() as any[];
      })
      .catch(this.handleError)
  }

  getBetweenDates(reservation): Promise<any> {
    return this.http.post(this.baseUrl + '/getBetweenDates', reservation)
      .toPromise()
      .then( response => {
        let arr = response.json() as any[];
        let betweenDates: Date[] = [];
        arr.forEach( item => {
          betweenDates.push(this.convertLocalDateTimeToDate(item));
        });
        return betweenDates;
      })
      .catch(this.handleError)
  }

  updateDatabase(): Promise<any> {
    return this.http.get(this.baseUrl +'/updateDatabase')
      .toPromise()
      .then(response => {
        return response;
      })
      .catch(this.handleError)
  }

  makeReservation(reservation): Promise<any> {
    return this.http.post(this.baseUrl + '/makeReservation', reservation)
      .toPromise()
      .then(response => {
        return response.json();
      })
      .catch(this.handleError)
  }

  removeReservation(reservation): Promise<any> {
    return this.http.post(this.baseUrl + '/removeReservation', reservation)
      .toPromise()
      .then( response => {
        return response.json();
      })
      .catch(this.handleError)
  }

  private handleError(error: any): Promise<any> {
    console.error('Some error occured', error);
    return Promise.reject(error.message || error);
  }

  private convertLocalDateTimeToDate(date) : Date {
    let year = date.year;
    let month = date.monthValue;
    let day = date.dayOfMonth;
    let hour = date.hour;
    let minute = date.minute;
    return new Date(year, month, day, hour, minute)
  }
}
