import * as types from '../util/mutationTypes.js'
import Vue from 'vue'

const state = {
  user: null,
  isLoggedIn: false
};

const mutations = {
  [types.SET_PROFILE](state, profile) {
    state.user = profile;
    state.isLoggedIn = true;
  },
  [types.SET_LOGGED_IN](state, value) {
    state.isLoggedIn = value;
  },
  [types.LOG_OUT](state) {
    state.isLoggedIn = false;
    state.user = null;
  }
};

const actions = {
  getDataProfile({
    commit,
    getters
  }) {
    return new Promise((resolve, reject) => {
      Vue.http.get(getters.getUrls.profile)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }, response => {
          reject(response);
        });
    });
  },
  signIn({
    commit,
    getters
  }, loginForm) {
    return new Promise((resolve, reject) => {
      Vue.http.post(getters.getUrls.profile, loginForm)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }, () => {
          reject("Network error");
        });
    });
  },
  signUp({
    commit,
    getters
  }, registerForm) {
    return new Promise((resolve, reject) => {
      Vue.http.post(getters.getUrls.signUp, registerForm)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }, () => {
          reject("Network error");
        });
    });
  },
  verifyUser({
    getters,
    dispatch
  }, tokenInput) {
    return new Promise((resolve, reject) => {
      Vue.http.get(getters.getUrls.verification, {
          params: {
            token: tokenInput
          }
        })
        .then(() => {
          dispatch('getDataProfile')
            .catch((error) => {
              reject(error.body.errors);
            });
          resolve();
        }, response => {
          reject(response.body.errors);
        });
    });
  },
  logOut({
    getters,
    commit
  }) {
    Vue.http.post(getters.getUrls.logout)
      .then(response => {
        commit(types.LOG_OUT);
      }, (error) => {
        console.log('Error: ', error);
      });
  }
};

const getters = {
  profile: (state) => state.user,
  isLoggedIn: (state) => state.isLoggedIn
};

export default {
  state,
  mutations,
  actions,
  getters
}
