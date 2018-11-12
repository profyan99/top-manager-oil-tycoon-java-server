import * as types from '../util/mutationTypes.js'
import Vue from 'vue'

const state = {
  connected: false
};

const mutations = {
  [types.SET_SOCKET_CONNECTED](state, connected) {
    state.connected = connected;
  }
};

const actions = {

};

const getters = {
  isConnected: (state) => state.connected
};

export default {
  state,
  mutations,
  actions,
  getters
}
