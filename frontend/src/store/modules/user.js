import axios from '../../http-common.js'

const state = {
	user: null
};

const mutations = {
	setProfile(state, profile) {
		state.user = profile;
	}
};

const actions = {
	getData({commit, store}) {
		axios.get(store.getters.profileLink, {
			data: {}
		}).then(response => {
			commit('setProfile', response.data);
		}).catch(error => {
			console.log("error: ", error.response);
		});
	},
	signIn({commit, getters}, loginForm) {
		return new Promise((resolve, reject) => {
			axios.post(getters.signInLink, loginForm)
			.then(response => {
				commit('setProfile', response.data);
				resolve();
			}).catch(() => {
				reject("Network error");
			})
		});
	}
};

const getters = {
	profile(state) {
		return state.user;
	}
};

export default {
	state,
	mutations,
	actions,
	getters
}
