import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FullCalendarComponent } from '@fullcalendar/angular';
import {
  Calendar,
  CalendarOptions, DateInput,
  EventSourceInput,
} from '@fullcalendar/core';
import frLocale from '@fullcalendar/core/locales/fr';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { Absence } from 'src/app/shared/model/absence';
import { AbsenceService } from 'src/app/shared/service/absence.service';
import {DatePipe} from "@angular/common";
import bootstrap5Plugin from '@fullcalendar/bootstrap5';
import {EmployeService} from "../../../shared/service/employe.service";
@Component({
  selector: 'app-departements',
  templateUrl: './calendrier.component.html',
  styleUrls: ['./calendrier.component.css'],
})
export class CalendrierComponent implements OnInit {

  showForm = false;
  showButton = false;
  showError = false;
  isDelete:boolean = false;
  absences: Absence[] = [];
  event:any = {}
  statut:string="";
  editable = false;
  soldeConge: number | undefined = 0;
  soldeRtt: number | undefined = 0;
  errorMsg="";

  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    locale: frLocale,
    plugins: [dayGridPlugin, interactionPlugin, bootstrap5Plugin],
    dateClick: this.handleDateClick.bind(this),
    eventClick: this.handleEventClick.bind(this),
    events: [],
    themeSystem: 'bootstrap5',
    weekends:true,
  }

  constructor(private _absenceService: AbsenceService,private _datePipe: DatePipe, private employeService:EmployeService) {}

  ngOnInit(): void {
    this._init();
  }

  private _init() {
    this._absenceService.findAllByEmploye().subscribe((absencesReceived) => {
      this.absences = absencesReceived;
      this.calendarOptions.events = this.absences.map((abs) => {
        let color = '#ffd2e1';
        if(abs.typeAbsence =='RTT_EMPLOYEUR') color = '#77baed';
        if (abs.statut == 'EN_ATTENTE') color = '#2B1B7B';
        if (abs.statut == 'VALIDEE' && abs.typeAbsence =='RTT') color = '#FEDD00';
        if (abs.statut == 'VALIDEE' && abs.typeAbsence =='CONGE_PAYE') color = '#35FDBE';
        if (abs.statut == 'REJETEE') color = '#f00020';

        const id:string = abs.id+'';

        let dateFin = abs.dateDebut as DateInput;

        if(abs.dateFin != abs.dateDebut){
          let endDate = new Date(abs.dateFin as Date)
          endDate.setDate(endDate.getDate() + 1)
          dateFin = this._datePipe.transform(endDate, 'yyyy-MM-dd') as DateInput ;
        }

        return {
          id: id,
          start:abs.dateDebut,
          end: dateFin ,
          type:abs.typeAbsence,
          motif:abs.motif,
          display: 'background',
          color: color,
          statut:abs.statut
        };
      });
    });
    this.employeService.findActive().subscribe(employe=>{
        this.soldeConge=employe.soldeConge
        this.soldeRtt=employe.soldeRtt
    })
  }

  handleEventClick(info:any){
    info.jsEvent.preventDefault();
    this.event = info.event;

  }

  handleDateClick(arg: any) {

    let clickedDate = new Date(arg.dateStr).setHours(0, 0, 0, 0);
    let dateNow = new Date().setHours(0, 0, 0, 0)

    if( clickedDate < dateNow ){
      this.showError = true;
      if(this.event.id){
        this.errorMsg = "Vous ne pouvez pas modifier une demande d'absence passée"
      }else{
        this.errorMsg = "Vous ne pouvez pas faire une demande d'absence aux jours passés"
      }
      return
    }

    if(this.event.extendedProps?.type == 'RTT_EMPLOYEUR'){
      this.showError = true;
      this.errorMsg = "Vous ne pouvez pas modifier une demande d'absence du type RTT_EMPLOYEUR";
      return
    }

    if(this.event.id){
      this.showButton = true;
      if((this.event.extendedProps.statut == 'INITIALE' || this.event.extendedProps.statut == 'REJETEE') && this.event.extendedProps.type != 'RTT_EMPLOYEUR'){
        this.editable = true;
      }
    }else{
      this.event.start = arg.dateStr;
      this.showForm = true;
    }
  }

  handleShowForm(e:Event){
    e.stopPropagation();
    this.showForm = true;
    this.showButton = false;
  }


 handleAnnulation(){
   this.showForm = false;
   if(this.event.id && this.isDelete){
     this.isDelete = false;
   }
   this.event = {};
   this.editable =false;
 }

  annulerDemandeAbs(e:Event){
    e.stopPropagation();
    this.isDelete = true;
    this.showForm = true;
    this.showButton = false;
  }


  traitementdeNuit(){
    this._absenceService.traitementNuit().subscribe(()=>{
      this._init()
    })
  }

  handleSubmit(data:any){

    // ajoute une absence
    if(!this.event.id){
      let motif = '';
      if(data.value.motif && data.value.motif.replace(/ /g,'') != ''){
        motif = data.value.motif;
      }

      const absence = {
        dateDebut:data.value.start,
        dateFin:data.value.end,
        typeAbsence:data.value.type,
        motif:motif,
        statut:"INITIALE"
      }

      this._absenceService.create(absence).subscribe({
        next: ()=> { this._init()},
        error :(res)=>{
          this.errorMsg = res.error;
          this.showError = true;
          },
        complete : ()=> {}
      })

      this.showForm = false;
      this.event = {};
      return
    }

    // annuler une absence
    if(this.isDelete){
      this._absenceService.delete(this.event.id)
        .subscribe({
          next:()=>{this._init() },
          error:(res)=>{
            this.errorMsg = res.error;
            this.showError = true;}
        })
      this.showForm = false;
      this.event = {};
      this.isDelete = false;
      this.editable = false;
      return
    }



    // modifier une absence
    const absence = {
      id:this.event.id,
      dateDebut:data.value.start,
      dateFin:data.value.end,
      typeAbsence:data.value.type,
      motif:data.value.motif,
      statut:"INITIALE"
    }

    this._absenceService.modify(absence).subscribe(
      {
        next:()=>{this._init()},
        error:(res)=> {
          this.errorMsg = res.error;
          this.showError = true; }
      }
    )

    this.showForm = false;
    this.editable = false;
    this.event = {};
  }

  annulerAction(){
    this.showForm = false;
    this.showButton = false;
    this.editable =false;
    this.isDelete = false;
    this.showError = false;
    this.event = {};
  }

}
