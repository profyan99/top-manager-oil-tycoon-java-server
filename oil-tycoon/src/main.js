import Vue from 'vue'
import App from './components/App.vue'
import bootstrap from 'bootstrap-vue';
import axios from 'axios'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import VueRouter from 'vue-router'
import routes from './routes.js'

Vue.use(bootstrap);
Vue.use(VueRouter);

Vue.config.productionTip = false;


const router = new VueRouter({
    routes: routes
});

const vueAxios = axios.create({
    baseURL: `http://localhost:8088`,
    headers: {
        'Content-Type': 'application/json'
    }
});

new Vue({
    router,
    vueAxios,
    render: h => h(App)
}).$mount('#app');
