import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {JoursOff} from "../model/jours-off";
import {environment} from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})

export class JoursOffService {

  private _baseUrl = environment.urlApi.joursoff;

  constructor(private _http: HttpClient) {
  }

  public findAll() {
    return this._http.get<JoursOff[]>(this._baseUrl,{withCredentials:true})
  }

  public findById(id?: number) {
    return this._http.get(`${this._baseUrl}/${id}`);
  }

  public create(created: JoursOff, ) {
    const headers = { 'content-type': 'application/json'}
    created.typeJour = "0";//0 = rtt employeur, 1 = jour ferie
    return this._http.post(this._baseUrl, created,{headers: headers,withCredentials:true})
  }

  public update(updated: JoursOff) {
    const headers = { 'content-type': 'application/json'}
      return this._http
        .put(`${this._baseUrl}/${updated.id}`, updated,{headers: headers,withCredentials:true})
    }
  public delete(id?: number) {
    const headers = { 'content-type': 'application/json'}
    return this._http.delete(`${this._baseUrl}/${id}`,{headers: headers,withCredentials:true})
  }
}
