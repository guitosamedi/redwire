import {Departement} from "./departement";

export interface Employe{

  id?:number;
firstName?:string;
lastName?:string;
password?:string;
soldeConge?:number;
soldeRtt?:number;
email?:string;

departement?:Departement

  manager?:Employe;

roles?:string[];

}
