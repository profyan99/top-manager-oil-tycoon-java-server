export const backendUrl = 'http://localhost:8088';
const state = {
  googleAuthLink: backendUrl + '/auth/google',
  vkAuthLink: backendUrl + '/auth/vkontakte',
  profileUrl: 'api/profile',
  signInUrl: 'api/signin',
  signUpUrl: 'api/signup',
  verificationEmailUrl: 'api/verification',
  logOutUrl: 'api/logout',
  errors: {
    ERROR_WITH_DATABASE: "ERROR_WITH_DATABASE",
    ERROR_WITH_AUTHENTICATION: "ERROR_WITH_AUTHENTICATION",
    AUTHENTICATION_ERROR: "AUTHENTICATION_ERROR",
    AUTHORIZATION_ERROR: "AUTHORIZATION_ERROR",
    NOT_FOUND: "NOT_FOUND",
    ACCOUNT_NOT_FOUND: "ACCOUNT_NOT_FOUND",
    EMAIL_NOT_UNIQUE: "EMAIL_NOT_UNIQUE",
    USERNAME_NOT_UNIQUE: "USERNAME_NOT_UNIQUE",
    VERIFICATION_TOKEN_NOT_FOUND: "VERIFICATION_TOKEN_NOT_FOUND",
    CONFIRM_TIME_EXPIRED: "CONFIRM_TIME_EXPIRED",
    INVALID_OLD_PASSWORD: "INVALID_OLD_PASSWORD"
  }
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
      signUp: state.signUpUrl,
      verification: state.verificationEmailUrl,
      logout: state.logOutUrl
    };
  },
  getErrors(state) {
    return state.errors;
  }
};

export default {
  state,
  mutations,
  actions,
  getters
}
