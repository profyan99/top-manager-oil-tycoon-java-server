import Vue from 'vue'
import App from './components/App.vue'
import bootstrap from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import VueRouter from 'vue-router'
import routes from './routes.js'

import login from './pages/Login.vue'

Vue.use(bootstrap);
Vue.use(VueRouter);

Vue.config.productionTip = false;



const router = new VueRouter({
    mode: 'history',
    routes: routes
});


new Vue({
    router,
    components: {
        login
    },
    render: h => h(App)
}).$mount('#app');
