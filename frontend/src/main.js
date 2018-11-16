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
import VueCookies from 'vue-cookies'

Vue.use(VueCookies);
Vue.use(VueResource);
Vue.use(Notifications);
Vue.config.productionTip = false;

Vue.http.options.root = backendUrl;
Vue.http.interceptors.push(function(request) {
  if(store.getters.accessToken && !request.getUrl().includes("oauth")) {
    request.headers.set('Authorization', 'Bearer '+store.getters.accessToken);
  }
  return function(response) {
    if (response.status == 403 &&
      response.body.errors[0].errorCode == store.getters.getErrors.AUTHENTICATION_ERROR) {
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
