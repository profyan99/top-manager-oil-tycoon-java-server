import Vue from 'vue'
import 'bootstrap/dist/css/bootstrap.min.css';
import 'mdbvue/build/css/mdb.css';
import App from './components/App.vue'
import VueRouter from 'vue-router'
import routes from './routes.js'
import 'vue-awesome/icons'
import Icon from 'vue-awesome/components/Icon'
import store from './store/index'
import { sync } from 'vuex-router-sync'
import 'font-awesome/css/font-awesome.css';

Vue.component('icon', Icon);

import login from './pages/Login.vue'

Vue.use(VueRouter);


Vue.config.productionTip = false;



const router = new VueRouter({
    mode: 'history',
    routes: routes
});

sync(store, router);


new Vue({
    router,
    store,
    components: {
        login
    },
    render: h => h(App)
}).$mount('#app');
