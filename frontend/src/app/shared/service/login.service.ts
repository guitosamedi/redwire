import {HttpClient} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {environment} from 'src/environments/environment';
import {Router} from "@angular/router";


@Injectable({
  providedIn: 'root'
})

export class LoginService {

  private _baseUrl = environment.urlApi.login;
  private _baseUrlOut = environment.urlApi.logout;

  roles: string[] | undefined = [];

  constructor(private http: HttpClient, private router:Router) {
  }

  public login(username: string, password: string) {


    const logindata = {
      email: username,
      password: password
    }
    const headers = { 'content-type': 'application/json'}
    const body = JSON.stringify(logindata);

    return this.http.post(this._baseUrl, body, {observe: "response", headers: headers, withCredentials: true})
  }
//   return this.http.post(this._baseUrl, body,{'headers': headers})
//       .subscribe((response)=> console.log(response));

  logout() {
    localStorage.clear()
    this.http.post(`${this._baseUrlOut}`,{},{withCredentials: true})
      .subscribe()
  }
}


