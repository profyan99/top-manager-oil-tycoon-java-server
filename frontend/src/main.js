import 'bootstrap-css-only/css/bootstrap.min.css';
import 'mdbvue/build/css/mdb.css';
import Vue from 'vue'
import App from './components/App.vue'
import VueRouter from 'vue-router'
import routes from './routes.js'
import 'vue-awesome/icons'
import Icon from 'vue-awesome/components/Icon'
import store from './store/index'
import { sync } from 'vuex-router-sync'
import 'font-awesome/css/font-awesome.css';
import Notifications from 'vue-notification'

Vue.use(VueRouter);
Vue.use(Notifications);

Vue.config.productionTip = false;



const router = new VueRouter({
    mode: 'history',
    routes: routes
});

sync(store, router);


new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');
