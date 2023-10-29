// shared-data.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  private eventDataSubject = new BehaviorSubject<any>(null);
  eventData$ = this.eventDataSubject.asObservable();

  sendEventData(data: any) {
    this.eventDataSubject.next(data);
  }
}
