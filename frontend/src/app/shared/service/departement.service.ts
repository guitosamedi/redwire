import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Departement} from "../model/departement";
import {environment} from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DepartementService {

  private _baseUrl = environment.urlApi.departements;

  constructor(private http: HttpClient) {
  }


  public findAll() {
    return this.http.get<Departement[]>(this._baseUrl, {withCredentials: true})
  }


  public create(nom: string) {

    const logindata = {
      name: nom,

    }
    const head = {'content-type': 'application/json'}
    const body = JSON.stringify(logindata);

    this.http.post<string>(this._baseUrl, body, {observe: "response", headers: head, withCredentials: true})
      .subscribe(()=>{})
  }
}
