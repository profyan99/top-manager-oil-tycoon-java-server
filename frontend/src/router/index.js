import routes from './routes.js'
import VueRouter from 'vue-router'
import Vue from 'vue'
import {
  sync
} from 'vuex-router-sync'
import store from './../store/index.js'

Vue.use(VueRouter);

const router = new VueRouter({
  mode: 'history',
  routes: routes
});

sync(store, router);

router.beforeEach((to, from, next) => {
  if (!to.matched.length) {
    next('/notFound');
  } else {
    if (to.matched.some(record => record.meta.requiresAuth)) {
      if (!store.getters.isLoggedIn) {
        next({
          name: 'signin'
        });
      } else {
        next()
      }
    } else if (to.matched.some(record => record.meta.guest)) {
      if (!store.getters.isLoggedIn) {
        next()
      } else {
        next({
          name: 'rooms'
        })
      }
    } else {
      next()
    }
  }
});

export default router;
