import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

import user from './modules/user'
import misc from './modules/misc'
import room from './modules/room'

Vue.use(Vuex);

const store = new Vuex.Store({
	modules: {
		user,
		misc,
		room
	},
	plugins: [createPersistedState({
		paths:['user']
	})]
});

export default store;
