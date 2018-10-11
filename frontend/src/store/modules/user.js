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
  getData({
    commit,
    store
  }) {
    axios.get(store.getters.getUrls.profile, {
      data: {}
    }).then(response => {
      commit(types.SET_PROFILE, response.data);
    }).catch(error => {
      console.log("error: ", error.response);
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
          commit(types.SET_LOGGED_IN, true);
          resolve();
        }).catch(() => {
          reject("Network error");
        })
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
