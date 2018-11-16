import * as types from '../util/mutationTypes.js'


const state = {
  connected: false,
  messages: []
};

const mutations = {
  [types.SET_SOCKET_CONNECTED](state, connected) {
    state.connected = connected;
  },
  [types.SET_NEW_MESSAGE](state, msg) {
    state.messages.push(msg);
  }
};

const actions = {

};

const getters = {
  isConnected: (state) => state.connected,
  getMessages: (state) => state.messages
};

export default {
  state,
  mutations,
  actions,
  getters
}
