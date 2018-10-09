import Vue from 'vue'
import Vuex from 'vuex'

import user from './modules/user'
import misc from './modules/misc'

Vue.use(Vuex);

const store = new Vuex.Store({
	modules: {
		user,
		misc
	}
});

export default store;
