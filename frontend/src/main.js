import 'bootstrap-css-only/css/bootstrap.min.css';
import 'mdbvue/build/css/mdb.css';
import Vue from 'vue'
import App from './components/App.vue'
import 'vue-awesome/icons'
import store from './store'
import 'font-awesome/css/font-awesome.css';
import Notifications from 'vue-notification'
import router from './router'

Vue.use(Notifications);
Vue.config.productionTip = false;


new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');
