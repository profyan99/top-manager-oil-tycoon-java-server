<template>
<div>
  <div class="container-fluid" id="signin">
    <div class="row d-flex justify-content-center">
      <div class="col-md-4">
        <div class="card mt-5 animated fadeIn">
          <div class="card-body">
            <form class="p-4" @submit.prevent="login">
              <h2 class="dark-grey-text text-center mb-5"><strong>Авторизация</strong></h2>
              <div class="row justify-content-center animated slideInDown fast">
                <div class="col-md-10">
                  <div class="md-form mt-">
                    <mdb-input type="text" label="Логин" icon="user grey-text" v-model="signinForm.username" />
                  </div>
                  <div class="md-form mt-5">
                    <mdb-input type="password" label="Пароль" icon="lock grey-text" v-model="signinForm.password" />
                  </div>
                  <div class="form-row justify-content-around mt-2">
                    <div>
                      <div class="custom-control custom-checkbox">
                        <input type="checkbox" class="custom-control-input" id="defaultLoginFormRemember" v-model="signinForm.rememberMe">
                        <label class="custom-control-label" for="defaultLoginFormRemember">Запомнить</label>
                      </div>
                    </div>
                    <div>
                      <router-link :to="{ name: 'forgotPass'}">Забыли пароль?</router-link>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row justify-content-center mt-5 text-center pb-3 animated slideInUp fast">
                <div class="col-md-10">
                  <button class="btn btn-primary btn-block mb-3 animated slideInDown fast">Войти</button>
                  <div class="mt-4">
                    <p>Не зарегистрированы?
                      <router-link :to="{ name: 'signup'}">Регистрация</router-link>
                    </p>
                    <p>или</p>
                    <div class="row">
                      <div class="col-md-6">
                        <a :href="vkontakte">
                        <button type="button" class="btn btn-indigo btn-block"><i class="fa fa-vk "></i></button>
                      </a>
                      </div>
                      <div class="col-md-6">
                        <a :href="google">
                        <button type="button" class="btn btn-danger btn-block"><i class="fa fa-google-plus "></i></button>
                      </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import {
  mapActions,
  mapGetters
} from 'vuex';
import {
  mdbInput
} from "mdbvue";

import {
  isSignInValid
} from '../validators.js'

import {
  showVerifyEmailNotification,
  showErrorNotification
} from '../store/util/functions'


export default {
  name: 'Login',
  components: {
    mdbInput
  },
  data() {
    return {
      vkontakte: this.$store.getters.getUrls.vk,
      google: this.$store.getters.getUrls.google,
      signinForm: {
        username: '',
        password: '',
        rememberMe: false
      }
    }
  },
  computed: {
    ...mapGetters([
      'isLoggedIn',
      'profile'
    ])
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      let query = vm.$route.query;
      if(query.access_token && query.refresh_token) {
        vm.$store.commit('setAccessToken', query.access_token);
        vm.$store.commit('setRefreshToken', query.refresh_token);
        vm.$store.dispatch("getDataProfile")
          .then(() => {
            vm.$router.push('rooms');
          }).catch((error) => {
            console.log('error', error);
          });
      }
    })
  },
  methods: {
    ...mapActions([
      'authenticate',
      'getDataProfile'
    ]),
    login() {
      let errs = isSignInValid(this.signinForm);
      if (!errs.isValid) {
        showErrorNotification(this, errs.text);
      } else {
        this.authenticate(this.signinForm)
          .then(() => {
            if (this.profile.roles.includes('UNVERIFIED')) {
              showVerifyEmailNotification(this);
            }
            if (this.$store.getters.profile.roles.includes('UNVERIFIED')) {
              this.$notify({
                group: 'system-notifications',
                title: 'Подтверждение регистрации',
                type: 'warn',
                text: 'На ваш электронный адрес <b>' + this.$store.getters.profile.email + '</b> \
                было выслано письмо с ссылкой на подтверждение регистрации аккаунта.',
                duration: -1,
              });
            }
            this.$router.push('rooms');
          }).catch(error => {
            console.log('Error login: ', error);
            showErrorNotification(this, error);
          });
      }
    }
  }
}
</script>
<style scoped>
#signin {
  background: #1D766F;
  position: fixed;
  height: 100%;
}
</style>
