import { Component, OnInit } from '@angular/core';
import { AbsenceService } from "../../../../shared/service/absence.service";
import { Absence } from "../../../../shared/model/absence";
import { ChartConfiguration, ChartOptions } from "chart.js";
import { formatDate } from "@angular/common";
import { DepartementService } from "../../../../shared/service/departement.service";
import { EmployeService } from "../../../../shared/service/employe.service";
import { Departement } from "../../../../shared/model/departement";
import { Employe } from "../../../../shared/model/employe";
import { switchMap } from "rxjs";
import { JoursOffService } from "../../../../shared/service/jours-off.service";
import { JoursOff } from "../../../../shared/model/jours-off";
import * as XLSX from 'xlsx';

@Component({
  selector: 'app-rapports-vue-one',
  templateUrl: './rapports-vue-one.component.html',
  styleUrls: ['./rapports-vue-one.component.css']
})
export class RapportsVueOneComponent implements OnInit {

  //page de création de l'histogramme

  selectMonth: string = "";
  annees: number[] = []
  absences: Absence[] = []
  employes: Employe[] = []
  departements: Departement[] = []
  joursOffs: JoursOff[] = []
  departementGeneral: Departement = { id: 0, name: "tout le monde" }
  currentMonth: number = new Date().getMonth();
  currentYear: number = new Date().getFullYear()
  currentMonthString: string = "";
  months: number[] = [];
  lineChartData: any[] = []
  lineChartLabels: string[] = []


  constructor(private absenceService: AbsenceService,
    private departementService: DepartementService,
    private employeService: EmployeService,
    private jourOffService: JoursOffService) {
  }


  employeByDepartement(departementId: string) {
    //pour filtre les absences en fonction du département

    if (parseInt(departementId) == 0) {
      this.employeService.findAll().subscribe(list => {
        this.employes = list
        this.updateLineChartLabels()
      })
    } else {
      this.employeService.findAll().subscribe(list => {
        this.employes = list.filter(employe => employe.departement?.id == parseInt(departementId));
        this.updateLineChartLabels()
      })
    }

  }

  updateLineChartData() {
    this.lineChartData = this.employes.map((employe, index) => {
      const dataPoints = this.lineChartLabels.map((label) =>
        this.absencePerDayPerEmploye(label, employe)
        //on check si l'employe est absent ce jour là
      );
      const hue = (index / this.employes.length) * 360;//couleur aléatoire mais qui reste la meme à chaque refresh
      return {
        data: dataPoints, // Array of data points for the dataset
        label: employe.firstName,
        backgroundColor: `hsla(${hue}, 100%, 50%, 1)`,
        stack: 'stack 1',
      };
    });
  }
  absencePerDayPerEmploye(date: string, employe: Employe) {
    let nbTotal = 0;
    const absenceList=this.absences.filter(absence=>absence.statut=="VALIDEE")

    for (let absence of absenceList) {
      //@ts-ignore
      for (let dateTempo of this.getDates(absence.dateDebut, absence.dateFin)) {
        if (date == dateTempo && absence.employe?.id == employe.id) {
          nbTotal++;
        }
      }

    }
    return nbTotal
  }

  getDates(startDate: Date, endDate: Date) {

    //return le nombre de jours ouvrés entre deux dates, donc pas les weekend ni les jours officiels
    const dates: string[] = [];
    let currentDate = new Date(startDate);
    let maxDate = new Date(endDate)
    while (currentDate <= new Date(endDate)) {
      for (let jours of this.joursOffs) {

        if (jours.jour == currentDate) {
          //si c'est un jour férié ou un rtt employeur
          currentDate.setDate(currentDate.getDate() + 1);
        }
      }
      if (currentDate.getDay() == 0 || currentDate.getDay() == 6) {
        //si c'est le weekend, on ignore
        currentDate.setDate(currentDate.getDate() + 1);
      } else {
        dates.push(formatDate(currentDate, 'yyyy-MM-dd', 'en-US'));
        //pour éviter de trop s'embeter , toutes nos dates sont au fomrat américain
        currentDate.setDate(currentDate.getDate() + 1);
      }

    }
    return dates;
  }

  lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
        labels: {
          padding: 20,
        }
      }
    },
    scales: {
      y: {
        //par défaut, l'histogramme est haut de 5 unité ,s'il y a plus
        // d'absence sur une journée que 5, la hauteur changeen fonction
        position: 'left',
        suggestedMax: 5,
      },
    },
  };

  lineChartLegend = true;
  lineChartType = 'line';

  ngOnInit(): void {

    //au chargement de la page, on initialise toutes les valeurs obligatoires
    //l'ordre dans lequel les valeurs sont initialisés est très important
    //d'ou le double switchmap (chatgpt a pas mal aidé)
    this.employeService.findAll().pipe(
      switchMap((employes) => {
        this.employes = employes;
        return this.absenceService.findAll();
      }),
      switchMap((absences) => {
        this.absences = absences as Absence[];
        return this.jourOffService.findAll();
      })
    ).subscribe((joursOff) => {
      this.joursOffs = joursOff;
      this.updateLineChartLabels();
    });

    this.departementService.findAll().subscribe(t => {
      this.departements = t
      this.departements.push(this.departementGeneral)
    })

    for (let i = 1980; i < 2050; i++) {
      this.annees.push(i)
      //on propose d'aller jusqu'en 2050,
      // l'entreprise du client aura surement besoin de faire des mise a jour d'ici là
    }
  }



  updateLineChartLabels() {
    this.lineChartLabels = Array.from({ length: 31 }, (_, i) => {
      //On prend 31 jours pour les labels,
      //dans le rapport vue tableau, on prend le nombre de jour dans le mois,
      //mais la méthode fonctionnaient mal ici, ej ne sais toujours pas pourquoi
      //si le mois fait 30 jours, le 31eme jour correspond au premier jour du mois suivant
      const date = new Date();
      date.setMonth(this.currentMonth);
      date.setDate(i + 1);
      date.setFullYear(this.currentYear)
      return formatDate(date, 'yyyy-MM-dd', 'en-US');

    });

    this.updateLineChartData()
  }

  changeMonth(change: number) {
    this.currentMonth += change;

    if (this.currentMonth > 11) {
      this.currentMonth = 0;
      this.currentYear += 1
    } else if (this.currentMonth < 0) {
      this.currentMonth = 11;
      this.currentYear += -1
    }

    this.updateLineChartLabels()
  }


  getMonthName() {
    //un switch case ne fonctionnait pas ,
    //mettre === ne fonctionnait pas non plus
    //là encore, je ne sais pas pourquoi

      if (this.currentMonth == 0) {
        return "Janvier";
      } else if (this.currentMonth == 1) {
        return "Février";
      } else if (this.currentMonth == 2) {
        return "Mars";
      } else if (this.currentMonth == 3) {
        return "Avril";
      } else if (this.currentMonth == 4) {
        return "Mai";
      } else if (this.currentMonth == 5) {
        return "Juin";
      } else if (this.currentMonth == 6) {
        return "Juillet";
      } else if (this.currentMonth == 7) {
        return "Août";
      } else if (this.currentMonth == 8) {
        return "Septembre";
      } else if (this.currentMonth == 9) {
        return "Octobre";
      } else if (this.currentMonth == 10) {
        return "Novembre";
      } else if (this.currentMonth == 11) {
        return "Décembre";
      } else {
        return "Invalid Month";
      }
    }





  exportToExcel() {

    //méthode pour télécharger les données au format excel

    //c'est du chatGPT, je ne sais pas trop à quoi correspondent les "..." utilisés comme ça
    const excelData = [];

    // Add headers as the first row
    excelData.push(['Date', ...this.employes.map(employe => employe.firstName)]);

    // Add data rows
    for (let i = 0; i < this.lineChartLabels.length; i++) {
      const rowData = [this.lineChartLabels[i], ...this.lineChartData.map(dataset => dataset.data[i])];
      excelData.push(rowData);
    }

    // Create a worksheet
    const ws: XLSX.WorkSheet = XLSX.utils.aoa_to_sheet(excelData);

    // Create a workbook
    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, this.currentMonth+"_"+this.currentYear+"chartData");

    // Generate a Blob containing the Excel file and trigger download
    XLSX.writeFile(wb, this.currentMonth+"_"+this.currentYear+"chartData.xlsx");
  }


}
