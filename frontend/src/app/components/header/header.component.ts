import {Component, OnInit} from '@angular/core';
import {LoginService} from "../../shared/service/login.service";
import {Employe} from "../../shared/model/employe";
import {EmployeService} from "../../shared/service/employe.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent  implements OnInit{
  // Create a property to track whether the menu is open.
  // Start with the menu collapsed so that it does not
  // appear initially when the page loads on a small screen!
  isMenuCollapsed = true;
  event:any = {}
  roles: string [] | undefined = [];

  employe:Employe={}


  constructor(private loginService:LoginService, private employeService:EmployeService) {
  }

  ngOnInit(): void {
    this.roles =  this.loginService.roles ;
    this.employeService.findActive().subscribe(t=>this.employe=t)
  }

  logOut(){
    this.isMenuCollapsed = true;
    this.loginService.logout();
  }

}


