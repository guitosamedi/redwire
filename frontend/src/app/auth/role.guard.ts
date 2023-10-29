import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {LoginService} from "../shared/service/login.service";

export const roleGuard: CanActivateFn = (route, state) => {

  const loginService= inject(LoginService);
  const router = inject(Router);

  let isAuthorized = loginService.roles?.includes(route.data['roles']);

  if(isAuthorized){
    return true;
  }
  // Redirect to the home page for other roles
  return router.parseUrl('/home');
};
