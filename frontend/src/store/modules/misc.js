export const backendUrl = 'http://localhost:8088';
const state = {
  googleAuthLink: backendUrl + '/auth/google',
  vkAuthLink: backendUrl + '/auth/vkontakte',
  profileUrl: '/api/profile',
  signInUrl: '/api/signin',
  signUpUrl: '/api/signup'
};

const mutations = {

};

const actions = {

};

const getters = {
  getUrls(state) {
    return {
      google: state.googleAuthLink,
      vk: state.vkAuthLink,
      profile: state.profileUrl,
      signIn: state.signInUrl,
      signUp: state.signUpUrl
    };
  }
};

export default {
  state,
  mutations,
  actions,
  getters
}