import 'bootstrap-css-only/css/bootstrap.min.css';
import 'mdbvue/build/css/mdb.css';
import Vue from 'vue'
import App from './components/App.vue'
import 'vue-awesome/icons'
import store from './store'
import 'font-awesome/css/font-awesome.css';
import Notifications from 'vue-notification'
import router from './router'
import VueResource from 'vue-resource'
import { backendUrl } from './store/modules/misc.js'


Vue.use(VueResource);
Vue.use(Notifications);
Vue.config.productionTip = false;

Vue.http.options.root = backendUrl;
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

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
