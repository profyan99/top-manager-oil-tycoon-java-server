<template>
<div>
  <notifications
  group="signin-notifications"
  position="bottom right"
  width="50vh"
  >
  </notifications>
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
                    <mdb-input type="text" label="Логин" icon="user grey-text" v-model="signinForm.login" />
                  </div>
                  <div class="md-form mt-5">
                    <mdb-input type="password" label="Пароль" icon="lock grey-text" v-model="signinForm.pass" />
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

import { isSignInValid } from '../validators.js'

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
        login: '',
        pass: '',
        rememberMe: false
      }
    }
  },
  created() {
    if (this.isLoggedIn == true) {
      this.$router.push('rooms');
    } else {
      this.getDataProfile()
        .then(() => {
          this.$router.push('rooms');
        }).catch((error) => {
          console.log('error', error);
        });
    }
  },
  methods: {
    ...mapGetters([
      'isLoggedIn',
      'profile'
    ]),
    ...mapActions([
      'signIn',
      'getDataProfile'
    ]),
    login() {
      let errs = isSignInValid(this.signinForm);
      if (!errs.isValid) {
        this.$notify({
          group: 'signin-notifications',
          title: 'Ошибка',
          type: 'error',
          text: errs.text
        });
      } else {
        this.signIn(this.signinForm)
          .then(() => {
            this.$router.push('rooms');
          }).catch(error => {
            console.log('Error: ' + error);
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
