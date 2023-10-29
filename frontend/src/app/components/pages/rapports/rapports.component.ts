import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-rapports',
  templateUrl: './rapports.component.html',
  styleUrls: ['./rapports.component.css']
})
export class RapportsComponent {

  constructor(private router:Router) {
  }

  gotoTableau(){
    this.router.navigate(['/rapports/tableau'])


  }
    gotoHisto(){
    this.router.navigate(['/rapports/histogramme'])
  }


  selectOption(event: Event) {
    const selectedValue = (event.target as HTMLSelectElement).value;
    if (selectedValue === "histogramme") {
      this.gotoHisto();
    } else if (selectedValue === "tableau") {
      this.gotoTableau();
    }
    else{
      this.gotoHisto();
    }
  }
}
