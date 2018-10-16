import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from 'vuex-persistedstate'

import user from './modules/user'
import misc from './modules/misc'

Vue.use(Vuex);

const store = new Vuex.Store({
	modules: {
		user,
		misc
	},
	plugins: [createPersistedState({
		paths:['user']
	})]
});

export default store;
