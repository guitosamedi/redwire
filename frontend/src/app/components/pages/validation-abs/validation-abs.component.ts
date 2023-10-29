import { Component } from '@angular/core';
import { Absence } from 'src/app/shared/model/absence';
import { AbsenceService } from 'src/app/shared/service/absence.service';


@Component({
  selector: 'app-validation-abs',
  templateUrl: './validation-abs.component.html',
  styleUrls: ['./validation-abs.component.css']
})
export class ValidationAbsComponent {
  absences: Absence[] = [];
  okMessage: string = "";
  triDateCreationAscendant: boolean = false;
  triDateDebutAscendant: boolean = false;

  constructor(private _absenceService: AbsenceService) { }

  ngOnInit(): void {
    this._init()
  }

  private _init() {
    this._absenceService
      .findAllByManager()
      .subscribe(absenceReceived => {
        this.absences = absenceReceived.filter(absence => absence.statut=="EN_ATTENTE");
        ;
      })
  }
  
  validStatut(absence: Absence) {
    absence.statut = "VALIDEE";
  }
  rejectStatut(absence: Absence) {
    absence.statut = "REJETEE"
  }
  eraseStatut(absence: Absence) {
    absence.statut = "EN_ATTENTE"
  }

  DateCreationSort() {
    this.absences.sort((a, b) => {
      if (a.dateCreation && b.dateCreation) {
        const dateA = new Date(a.dateCreation);
        const dateB = new Date(b.dateCreation);

        return this.triDateCreationAscendant
          ? dateA.getTime() - dateB.getTime()
          : dateB.getTime() - dateA.getTime();
      } else {
        return 0;
      }
    });
    this.triDateCreationAscendant = !this.triDateCreationAscendant;
  }

  DateDebutSort() {
    this.absences.sort((a, b) => {
      if (a.dateDebut && b.dateDebut) {
        const dateA = new Date(a.dateDebut);
        const dateB = new Date(b.dateDebut);

        return this.triDateDebutAscendant
          ? dateA.getTime() - dateB.getTime()
          : dateB.getTime() - dateA.getTime();
      } else {
        return 0;
      }
    });
    this.triDateDebutAscendant = !this.triDateDebutAscendant;
  }

  clearMessages() {
    setTimeout(() => {
      this.okMessage = '';
    }, 3000);
  }

  updateAbs() {
      this.absences.forEach(absence => {
        this._absenceService.update(absence).subscribe(() => this._init());
        this.okMessage = "Les absences ont bien été enregistrées.";
        this.clearMessages();
      });
  }
}



