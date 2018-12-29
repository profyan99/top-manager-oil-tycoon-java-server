import * as types from '../util/mutationTypes.js'
import Vue from 'vue'

const state = {
  connected: false,
  messages: []
};

const mutations = {
  [types.SET_SOCKET_CONNECTED](state, connected) {
    state.connected = connected;
  },
  [types.SET_NEW_MESSAGE](state, msg) {
    state.messages = msg;
  }
};

const actions = {
  addRoom({
    getters
  }, number) {
    return new Promise((resolve, reject) => {
      let data = {
        name: "Test Room "+number,
        maxPlayers: 8,
        isLocked: false,
        isTournament: false,
        isScenario: false,
        scenario: "",
        requirement: {
          minHoursInGameAmount: 0,
          requireAchievements: [],
          requireRoles: [
            "PLAYER"
          ]
        },
        maxRounds: 6,
        password: "",
        roomPeriodDelay: 10
      };
      console.log("Data: ", data);

      Vue.http.post(getters.getUrls.roomAdd, data)
        .then(() => {
          resolve();
        }, (error) => {
          reject(error.body.errors);
        });
    });
  },
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
