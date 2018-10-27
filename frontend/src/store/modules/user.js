import * as types from '../util/mutationTypes.js'
import Vue from 'vue'

const state = {
  user: null,
  isLoggedIn: false,
  accessToken: null,
  refreshToken: null
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
    state.accessToken = null;
    state.refreshToken = null;
    state.user = null;
  },
  [types.SET_ACCESS_TOKEN](state, token) {
    state.accessToken = token;
  },
  [types.SET_REFRESH_TOKEN](state, token) {
    state.refreshToken = token;
  }
};

const actions = {
  async getDataProfile({
    commit,
    getters
  }) {
    return new Promise(async (resolve, reject) => {
      await Vue.http.get(getters.getUrls.profile)
        .then(async response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }, async response => {
          reject(response);
        });
    });
  },
  async authenticate({
    commit,
    getters,
    dispatch
  }, credentials) {
    return new Promise(async (resolve, reject) => {
      let cred = {
        username: credentials.username,
        password: credentials.password,
        grant_type: 'password'
      }
      Vue.http.post(getters.getUrls.authenticate, cred, {
        headers: {
          Authorization: 'Basic ' + getters.getAuthSecret
        },
        emulateJSON: true
      }).then(async response => {
        commit(types.SET_ACCESS_TOKEN, response.data.access_token);
        commit(types.SET_REFRESH_TOKEN, response.data.refresh_token);
        await dispatch('getDataProfile')
          .then(() => {
            console.log("RESOLVE after: ", getters.profile.username);
            resolve();
          })
          .catch((error) => {
            console.log("REJECT getDataProfile");
            reject(error.body.errors);
          });
      }, async (error) => {
        console.log("REJECT post");
        console.log(error);
        reject();
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
    commit
  }) {
    commit(types.LOG_OUT);
  }
};

const getters = {
  profile: (state) => state.user,
  isLoggedIn: (state) => state.isLoggedIn,
  accessToken: (state) => state.accessToken,
  refreshToken: (state) => state.refreshToken
};

export default {
  state,
  mutations,
  actions,
  getters
}
