import {Employe} from "./employe";

export interface Absence{
  id?:number;
  dateCreation?:Date;
  dateDebut?:Date;
  dateFin?:Date;
  motif?:string;
  typeAbsence?:string;
  statut?:string;
  employe?:Employe;
}


