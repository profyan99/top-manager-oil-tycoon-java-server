import * as types from '../util/mutationTypes.js'
import Vue from 'vue'

const state = {
  user: null,
  isLoggedIn: false,
  accessToken: null,
  refreshToken: null
};

const mutations = {
  [types.SET_PROFILE](state, profile) {
    state.user = profile;
    state.isLoggedIn = true;
  },
  [types.SET_LOGGED_IN](state, value) {
    state.isLoggedIn = value;
  },
  [types.LOG_OUT](state) {
    state.isLoggedIn = false;
    state.accessToken = null;
    state.refreshToken = null;
    state.user = null;
  },
  [types.SET_ACCESS_TOKEN](state, token) {
    state.accessToken = token;
  },
  [types.SET_REFRESH_TOKEN](state, token) {
    state.refreshToken = token;
  }
};

const actions = {
  getDataProfile({
    commit,
    getters
  }) {
    return new Promise((resolve, reject) => {
      Vue.http.get(getters.getUrls.profile)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }, response => {
          reject(response);
        });
    });
  },
  authenticate({
    commit,
    getters,
    dispatch
  }, credentials) {
    return new Promise((resolve, reject) => {
      let cred = {
        username: credentials.username,
        password: credentials.password,
        grant_type: 'password'
      };
      Vue.http.post(getters.getUrls.authenticate, cred, {
        headers: {
          Authorization: 'Basic ' + getters.getAuthSecret
        },
        emulateJSON: true
      }).then(response => {
        commit(types.SET_ACCESS_TOKEN, response.data.access_token);
        commit(types.SET_REFRESH_TOKEN, response.data.refresh_token);
      }).then(() => {
        return dispatch('getDataProfile')
      }).then(() => {
        resolve();
      }).catch((error) => {
        console.log("error", error);
        let err = '';
        if (error.body.error !== undefined) {
          if (error.body.error_description == "Bad credentials") {
            err = 'Неверный пароль'
          } else if (error.body.error_description == "Account not found") {
            err = 'Аккаунт не найден'
          } else {
            err = 'Неизвестная ошибка'
          }
        } else if (error.body.errors !== undefined) {
          err = getters.getErrors[error.body.errors[0].errorCode];
        }
        reject(err);
      });
    });
  },
  signUp({
    getters
  }, registerForm) {
    return new Promise((resolve, reject) => {
      let data = {
        firstName: registerForm.name,
        lastName: registerForm.surname,
        userName: registerForm.nickname,
        email: registerForm.email,
        password: registerForm.pass
      };
      Vue.http.put(getters.getUrls.signUp, data)
        .then(() => {
          resolve();
        }, (error) => {
          console.log("Error: ", error);
          var err = '';
          if (error.body.errors !== undefined) {
            err = error.body.errors[0].message;
          }
          reject(err);
        });
    });
  },
  verifyUser({
    getters,
    dispatch
  }, tokenInput) {
    return new Promise((resolve, reject) => {
      Vue.http.get(getters.getUrls.verification, {
          params: {
            token: tokenInput
          }
        })
        .then(() => {
          dispatch('getDataProfile')
            .catch((error) => {
              reject(error.body.errors);
            });
          resolve();
        }, response => {
          reject(response.body.errors);
        });
    });
  },
  logOut({
    commit
  }) {
    commit(types.LOG_OUT);
  },
  checkExpiredToken({
    dispatch
  }, response, request) {
    return new Promise((resolve, reject) => {
      if (response.status === 401 && response.data.error.code === 'GEN-TOKEN-EXPIRED') {
        dispatch('refreshToken', request)
          .then((response) => resolve(response));
      }
      resolve(response);
    });
  },
  refreshToken({
    getters,
    commit
  }, request) {
    return new Promise((resolve, reject) => {
      let data = {
        grant_type: 'refresh_token',
        refresh_token: getters.refreshToken
      };
      Vue.http.post(getters.getUrls.authenticate, data, {
        headers: {
          Authorization: 'Basic ' + getters.getAuthSecret
        },
        emulateJSON: true
      }).then(response => {
        commit(types.SET_ACCESS_TOKEN, response.data.access_token);
        Vue.http(request).then((newResponse) => {
          resolve(newResponse);
        }, error => {
          reject(error);
        });
      })
    });
  },
  updateProfile({
    getters,
    commit
  }, form) {
    return new Promise((resolve, reject) => {
      let data = {
        userName: getters.profile.userName,
        firstName: (form.name == '') ? getters.profile.firstName : form.name,
        lastName: (form.surname == '') ? getters.profile.lastName : form.surname,
        oldPassword: form.oldPass,
        newPassword: form.newPass
      };

      Vue.http.post(getters.getUrls.profile, data)
        .then(response => {
          commit(types.SET_PROFILE, response.data);
          resolve();
        }, (error) => {
          reject(error.body.errors);
        });
    });
  },
  forgotPassword({getters}, emailInput) {
    return new Promise((resolve, reject) => {
      Vue.http.post(getters.getUrls.forgotPassword, {}, {
          params: {
            email: emailInput
          }
        })
        .then(() => {
          resolve();
        }, response => {
          reject(response.body.errors);
        });
    });
  },
  resetPassword({
    getters
  }, form) {
    return new Promise((resolve, reject) => {
      let data = {
        newPassword: form.pass,
        newPasswordConfirm: form.confirmPass,
        token: form.token
      };
      console.log("Data: ", data);

      Vue.http.post(getters.getUrls.resetPassword, data)
        .then(() => {
          resolve();
        }, (error) => {
          reject(error.body.errors[0].message);
        });
    });
  },
};

const getters = {
  profile: (state) => state.user,
  isLoggedIn: (state) => state.isLoggedIn,
  accessToken: (state) => state.accessToken,
  refreshToken: (state) => state.refreshToken
};

export default {
  state,
  mutations,
  actions,
  getters
}
