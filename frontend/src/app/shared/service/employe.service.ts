import {HttpClient} from "@angular/common/http";
import {Employe} from "../model/employe";
import {Injectable} from "@angular/core";
import {environment} from 'src/environments/environment';
import {Departement} from "../model/departement";


@Injectable({
  providedIn: 'root'
})
export class EmployeService {

  private _baseUrl = environment.urlApi.employes;

  constructor(private http: HttpClient) {
  }

  public findAll() {
    return this.http.get<Employe[]>(this._baseUrl, {withCredentials: true});
  }

  public findById(id: number) {
    return this.http.get<Employe>(`${this._baseUrl}/${id}`, {withCredentials: true})
  }
  public findActive() {
    return this.http.get<Employe>(`${this._baseUrl}/active`, {withCredentials: true})
  }


  public create(firstName: string, lastName: string, password: string, soldeConge: number, soldeRTT: number, email: string, departement: Departement, manager: Employe, roles: string[]) {


    const newEmploye =
      {
        firstName: firstName,
        lastName: lastName,
        password: password,
        soldeConge: soldeConge,
        soldeRTT: soldeRTT,
        email: email,
        departement: departement,
        manager: manager,
        roles: roles
      }


    const headers = {'content-type': 'application/json'}
    return this.http
      .post(`${this._baseUrl}`, newEmploye, {headers: headers, withCredentials: true})

  }

  public update(employe: Employe) {

    const newEmploye =
      {
        firstName: employe.firstName,
        lastName: employe.lastName,
        password: employe.password,
        soldeConge: employe.soldeConge,
        soldeRTT: employe.soldeRtt,
        email: employe.email,

        departement: employe.departement,

        manager: employe.manager,

        roles: employe.roles
      }


    const headers = {'content-type': 'application/json'}
    return this.http
      .post(`${this._baseUrl}`, newEmploye, {headers: headers, withCredentials: true})

  }

}
