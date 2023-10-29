import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {JoursOffComponent} from "./components/pages/jours-off/jours-off.component";
import {LoginComponent} from "./components/pages/login/login.component";
import {NotFoundComponent} from "./components/pages/not-found/not-found.component";
import {ValidationAbsComponent} from "./components/pages/validation-abs/validation-abs.component";
import {DemandeAbsComponent} from "./components/pages/demande-abs/demande-abs.component";
import {RapportsComponent} from "./components/pages/rapports/rapports.component";
import {RapportsVueOneComponent} from "./components/pages/rapports/rapports-vue-one/rapports-vue-one.component";
import {RapportsVueTwoComponent} from "./components/pages/rapports/rapports-vue-two/rapports-vue-two.component";
import {CalendrierComponent} from "./components/pages/calendrier/calendrier.component";

import {HomeComponent} from "./components/pages/home/home.component";
import {authGuard} from "./auth/auth.guard";
import {roleGuard} from "./auth/role.guard";
import {SharedLayoutComponent} from "./components/layout/shared-layout/shared-layout.component";

const routes: Routes = [
  { path: 'login',component : LoginComponent,
  },


  {path:"", component:SharedLayoutComponent,
    canActivate:[authGuard],
    children:[
  { path: 'home',component : HomeComponent},
  { path: 'calendrier',component : CalendrierComponent},
  { path: 'demande',component : DemandeAbsComponent},
  { path: 'validation',
    component : ValidationAbsComponent,
    canActivate:[roleGuard],
    data:{roles:'MANAGER'}
  },
  { path: 'rapports',
    component : RapportsComponent,
    canActivate:[roleGuard],
    data:{roles:'MANAGER'},
    children:[
      {
        path:"",
        redirectTo:'histogramme',
        pathMatch: 'full'
      },
      {
        path:"histogramme",
        component:RapportsVueOneComponent
      },
      {
        path:"tableau",
        component:RapportsVueTwoComponent
      },
    ],

  },
  { path: 'jours-off',
    component : JoursOffComponent,
  }]},
  { path: '', redirectTo: 'login', pathMatch: 'full'},
  { path: '**',component : NotFoundComponent}
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
