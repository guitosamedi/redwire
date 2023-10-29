import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Absence} from "../model/absence";
import { environment } from 'src/environments/environment';


@Injectable({
  providedIn: 'root'
})
export class AbsenceService{

  private _baseUrl = environment.urlApi.absences;

  constructor(private _http: HttpClient) {}

  public findAll(){
    return this._http.get<Absence[]>(this._baseUrl,{withCredentials:true})
  }

  public findAllByManager(){
    return this._http.get<Absence[]>(this._baseUrl + '/manager',{withCredentials:true})
  }

  public findAllByEmploye(){
    return this._http.get<Absence[]>(this._baseUrl + '/employe',{withCredentials:true})
  }

  public create(absence: Absence) {
    const headers = { 'content-type': 'application/json'}
    return this._http.post<Absence>(this._baseUrl, absence,{observe: "response",headers: headers,withCredentials:true})
  }

  public delete(id: string) {
    return this._http
      .delete<Absence>(this._baseUrl + "/" + id, {withCredentials:true})
  }

  public modify(absence: Absence) {
    return this._http.put<Absence>(this._baseUrl + "/" +  absence.id, absence,{withCredentials:true})
  }

  public traitementNuit(){
    return this._http.post(this._baseUrl+"/traitement",{},{withCredentials:true})
  }

  public update(updated: Absence) {

    const newAbsence={
      dateCreation:updated.dateCreation,
      dateDebut:updated.dateDebut,
      dateFin:updated.dateFin,
      motif:updated.motif,
      typeAbsence:updated.typeAbsence,
      statut:updated.statut,
      employeId:updated.employe?.id
    }

    const headers = { 'content-type': 'application/json'}
    return  this._http
   .put(`${this._baseUrl}/statut/${updated.id}`, newAbsence,{headers: headers,withCredentials:true,responseType: "text"})

  }

}
