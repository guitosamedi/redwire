import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import {NgbCollapseModule, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {HttpClientModule} from "@angular/common/http";
import { FullCalendarModule } from '@fullcalendar/angular';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/pages/login/login.component';
import { CalendrierComponent } from './components/pages/calendrier/calendrier.component';
import { JoursOffComponent } from './components/pages/jours-off/jours-off.component';
import { NotFoundComponent } from './components/pages/not-found/not-found.component';
import { ValidationAbsComponent } from './components/pages/validation-abs/validation-abs.component';
import { DemandeAbsComponent } from './components/pages/demande-abs/demande-abs.component';
import { RapportsComponent } from './components/pages/rapports/rapports.component';
import { RapportsVueOneComponent } from './components/pages/rapports/rapports-vue-one/rapports-vue-one.component';
import { RapportsVueTwoComponent } from './components/pages/rapports/rapports-vue-two/rapports-vue-two.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { FormComponent } from './components/pages/calendrier/form/form.component';
import {DatePipe} from "@angular/common";
import { NgChartsModule } from 'ng2-charts';
import {HomeComponent} from "./components/pages/home/home.component";
import {SharedLayoutComponent} from "./components/layout/shared-layout/shared-layout.component";
import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NotFoundComponent,
    CalendrierComponent,
    JoursOffComponent,
    ValidationAbsComponent,
    DemandeAbsComponent,
    RapportsComponent,
    RapportsVueOneComponent,
    RapportsVueTwoComponent,
    HeaderComponent,
    FooterComponent,
    FormComponent,
    HomeComponent,
    SharedLayoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NgbModule,
    NgbCollapseModule,
    FormsModule,
    FullCalendarModule,
    DatePipe,
    NgChartsModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule
  ],
  providers: [DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
