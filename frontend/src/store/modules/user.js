import axios from '../../http-common.js'
import * as types from '../util/mutationTypes.js'

const state = {
  user: null,
  isLoggedIn: false,
  authErr: null
};

const mutations = {
  [types.SET_PROFILE](state, profile) {
    state.user = profile;
  },
  [types.SET_LOGGED_IN](state, value) {
    state.isLoggedIn = value;
  }
};

const actions = {
  getDataProfile({commit, getters}) {
    return new Promise((resolve, reject) => {
      axios.get(getters.getUrls.profile)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          commit(types.SET_LOGGED_IN, true);
          resolve();
        }).catch((error) => {
          reject("Network error");
        });
    });
  },
  signIn({commit,getters}, loginForm) {
    return new Promise((resolve, reject) => {
      axios.post(getters.getUrls.signIn, loginForm)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          commit(types.SET_LOGGED_IN, true);
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
          commit(types.SET_LOGGED_IN, true);
          resolve();
        }).catch(() => {
          reject("Network error");
        })
    })
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
