import Vue from 'vue'
import router from './router'
import store from "./store"


Vue.http.interceptors.push((request, next) => {
    request.credentials = true;
    next();
});
Vue.http.interceptors.push(function(request) {
  return function(response) {
    if (response.status == 403 &&
      response.body.errors[0].errorCode == 'AUTHENTICATION_ERROR') {
      store.dispatch("logOut");
      router.push('signin');
    }
  };
});
