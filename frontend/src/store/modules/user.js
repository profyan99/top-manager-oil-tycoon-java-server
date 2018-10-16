import axios from '../../http-common.js'
import * as types from '../util/mutationTypes.js'

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
      axios.get(getters.getUrls.profile)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }).catch((error) => {
          reject(error);
        });
    });
  },
  signIn({
    commit,
    getters
  }, loginForm) {
    return new Promise((resolve, reject) => {
      axios.post(getters.getUrls.signIn, loginForm)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }).catch(() => {
          reject("Network error");
        });
    });
  },
  signUp({
    commit,
    getters
  }, registerForm) {
    return new Promise((resolve, reject) => {
      axios.post(getters.getUrls.signUp, registerForm)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }).catch(() => {
          reject("Network error");
        });
    });
  },
  verifyUser({
    getters,
    dispatch
  }, tokenInput) {
    return new Promise((resolve, reject) => {
      axios.get(getters.getUrls.verification, {
          params: {
            token: tokenInput
          },
          validateStatus: function (status) {
            return status == 200;
          }
        })
        .then((response) => {
          dispatch('getDataProfile');
          // .then((response) => {
          //   console.log('Error: ', response);
          // }).catch((error) => {
          //   console.log('Error: ',error, ' ', error.response);
          //   reject(error);
          // });
          resolve();
        }).catch((error) => {
          console.log('Error: ',error);
          reject(error);
        });
    });
  },
  logOut({commit}) {
    commit(types.LOG_OUT);
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
