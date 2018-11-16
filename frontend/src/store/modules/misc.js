export const backendUrl = 'http://localhost:8088';
const baseSocketTopic = '/topic';
const baseSocketEndpoint = '/app';

const state = {
  googleAuthLink: backendUrl + '/auth/google',
  vkAuthLink: backendUrl + '/auth/vkontakte',
  profileUrl: 'api/profile',
  signInUrl: 'api/signin',
  signUpUrl: 'api/signup',
  verificationEmailUrl: 'api/verification',
  logOutUrl: 'api/logout',
  authenticateUrl: 'oauth/token',
  forgotPassword: 'api/forgot-password',
  resetPassword: 'api/reset-password',
  socketUrl: backendUrl+'/room',
  socketTopics: {
      room: baseSocketTopic + '/room'
  },
  socketEndpoints: {
      room: baseSocketEndpoint + '/room'
  },
  authSecret: 'dHJ1c3RlZC1jbGllbnQ6WFk3a216b056bDEwMA==',
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
      logout: state.logOutUrl,
      authenticate: state.authenticateUrl,
      socket: state.socketUrl,
      resetPassword: state.resetPassword,
      forgotPassword: state.forgotPassword
    };
  },
  getSocketTopics: (state) => state.socketTopics,
  getSocketEndpoints: (state) => state.socketEndpoints,
  getErrors(state) {
    return state.errors;
  },
  getAuthSecret(state) {
    return state.authSecret;
  }
};

export default {
  state,
  mutations,
  actions,
  getters
}
