import { DatePipe } from '@angular/common';
import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import bootstrap5Plugin from '@fullcalendar/bootstrap5';
import { CalendarOptions } from '@fullcalendar/core';
import frLocale from '@fullcalendar/core/locales/fr';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin, { DateClickArg } from '@fullcalendar/interaction';
import {NgbAlert, NgbOffcanvas} from '@ng-bootstrap/ng-bootstrap';
import { JoursOff } from "../../../shared/model/jours-off";
import { JoursOffService } from "../../../shared/service/jours-off.service";
import {LoginService} from "../../../shared/service/login.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-jours-off',
  templateUrl: './jours-off.component.html',
  styleUrls: ['./jours-off.component.css']
})

export class JoursOffComponent implements OnInit {
  @ViewChild('content') content: TemplateRef<any> | undefined;
  @ViewChild('staticAlert', { static: false }) staticAlert!: NgbAlert;
  @ViewChild('selfClosingAlert', { static: false }) selfClosingAlert!: NgbAlert;

  roles: string [] | undefined = [];
  joursOffs: JoursOff[] = [];
  jo: any = {};
  typesJour: string[] = [];
  showForm: boolean = false;
  showError = false;
  errorMsg="";
  selectedEvent: JoursOff | null = null;
  editable: boolean = false;

  calendarOptions: CalendarOptions = {
    locale: frLocale,
    plugins: [
      interactionPlugin,
      dayGridPlugin,
      bootstrap5Plugin
    ],
    themeSystem: 'bootstrap5',
    events: this.joursOffs.map(jourOff => ({
      id: jourOff.id?.toString() || '',
      title: jourOff.description || '',
      start: jourOff.jour || '',
      allDay: true,
    })),
    initialView: 'dayGridMonth',
    weekends: true,
    editable: true,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    dateClick: this.handleDateClick.bind(this),
  };

  constructor(
    private _jourOffService: JoursOffService,
    private loginService : LoginService,
    private datePipe: DatePipe,
    private offcanvasService: NgbOffcanvas,
    private toastrService: ToastrService
  ) {
  }

  ngOnInit(): void {
    this._init();
    this.roles =  this.loginService.roles ;
    //setTimeout(() => this.staticAlert.close(), 20000);
  }

  reInitJourOff() {
    this.jo = {};
  }

  showPanel() {
    this.offcanvasService.open(this.content, {position: 'end', backdrop: 'static', scroll: true});
  }
  hidePanel() {
    this.offcanvasService.dismiss();
    this.reInitJourOff();
  }

  handleDateClick(clickInfo: DateClickArg) {

    // Affecter la date cliquée et la formater
    const clickedDate = this.datePipe.transform(clickInfo.date, 'yyyy-MM-dd');

    // Affecter la date du jour dans la DB et la formater
    const existedJourOff = this.joursOffs.find(jourOff => {
      const joDate = this.datePipe.transform(jourOff.jour, 'yyyy-MM-dd');
      return joDate === clickedDate;
    });

    this.jo.jour = clickedDate

    // Vérifier si l'évènement seléctionné possède une donnée pour afficher le bon formulare Create/Update
    if (existedJourOff) {
      this.editable = true;
      this.selectedEvent = existedJourOff;
      this.jo = {...existedJourOff};
    } else {
      this.editable = false;
      this.selectedEvent = null;
    }

    // Afficher les éléments du formulaire
    this.showPanel();
    this.showForm = true;

  }

  createOrUpdateJourOff() {
    if (!this.jo.id) {
      this._jourOffService.create(this.jo)
        .subscribe({
          next: () => {
            this._init();
            this.reInitJourOff();
          },
          error: (res) => {
            this.errorMsg = res.error;
            this.showError = true;
          }
        })
    } else {
      this._jourOffService.update(this.jo)
        .subscribe({
          next: () => {
            this._init();
            this.reInitJourOff();
          },
          error: (res) => {
            this.errorMsg = res.error;
            this.showError = true;
          },
        })
    }
  }

  deleteJourOff(id?: number) {
    if (id) {
      this._jourOffService.delete(id)
        .subscribe({
          next: () => {
            this._init();
            this.reInitJourOff();
          },
          error:(res)=> {
            this.errorMsg = res.error;
            this.showError = true; }
        })
    }
  }

  private _init() {
    this._jourOffService.findAll().subscribe(jourOffReceived => {
        this.joursOffs = jourOffReceived;
        this.calendarOptions.events = this.joursOffs.map(jourOff => ({
          id: jourOff.id + '',
          title: jourOff.description || '',
          start: jourOff.jour || '',
          typeJour: jourOff.typeJour || '',
          allDay: true,
          display: 'background',
          color: this.getEventBackgroundColor(jourOff.typeJour),
        }));
        this.typesJour = [...new Set(jourOffReceived.map(jourOff => jourOff.typeJour || ''))];
        this.jo.typeJour = this.typesJour[0] || '';
      },
      (error) => {
        this.errorMsg = error;
        this.showError = true;
        console.error("Erreur lors de la récupération des jours officiels :", error);
      });
  }

  // Définir la couleur en fonction du Type Jour
  private getEventBackgroundColor(typeJour: string | undefined): string {
    if (typeJour) {
      switch (typeJour) {
        case 'JOUR_FERIE':
          return '#ff580f';
        case 'RTT_EMPLOYEUR':
          return '#caffff';
        default:
          return '';
      }
    } else {
      return '';
    }
  }
}
