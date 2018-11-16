<template>
<div>
  <div class="container-fluid" id="mains">
    <div class="row d-flex justify-content-center">
      <div class="col-md-4">
        <div class="card mt-5 animated fadeIn">
          <div class="card-body">
            <form class="p-3" @submit.prevent="register">
              <h2 class="dark-grey-text text-center"><strong>Регистрация</strong></h2>
              <div class="row justify-content-center animated slideInDown fast">
                <div class="col-md-10">
                  <div class="md-form mt-5">
                    <mdb-input type="text" label="Имя" icon="user grey-text" v-model="signupForm.name" />
                  </div>
                  <div class="md-form">
                    <mdb-input type="text" label="Фамилия" icon="user grey-text" v-model="signupForm.surname" />
                  </div>
                  <div class="md-form">
                    <mdb-input type="text" label="Никнейм" icon="user grey-text" v-model="signupForm.nickname" />
                  </div>
                  <div class="md-form">
                    <mdb-input type="text" label="Почта" icon="envelope grey-text" v-model="signupForm.email" />
                  </div>
                  <div class="md-form">
                    <mdb-input type="password" label="Пароль" icon="lock grey-text" v-model="signupForm.pass" />
                  </div>
                  <div class="md-form">
                    <mdb-input type="password" label="Пароль еще раз" icon="lock grey-text" v-model="signupForm.confirmPass" />
                  </div>
                </div>
              </div>

              <div class="row justify-content-center mt-2 text-center pb-2  animated slideInUp fast">
                <div class="col-md-10">
                  <button class="btn btn-primary btn-block mb-3">Отправить</button>
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
                  <hr>
                  <p>
                    Нажимая <em>Отправить</em>, вы соглашаетесь с правилами сервиса.
                  </p>
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
  isSignUpValid
} from '../validators.js'

import { showSuccessNotification, showErrorNotification } from '../store/util/functions'

export default {
  name: "Signup",
  components: {
    mdbInput
  },
  data() {
    return {
      vkontakte: this.$store.getters.getUrls.vk,
      google: this.$store.getters.getUrls.google,
      signupForm: {
        name: '',
        surname: '',
        nickname: '',
        email: '',
        pass: '',
        confirmPass: ''
      }
    }
  },
  created() {
    if (this.isLoggedIn == true) {
      this.$router.push('rooms');
    }
  },
  methods: {
    ...mapGetters([
      'isLoggedIn'
    ]),
    ...mapActions([
      'signUp',
      'getDataProfile'
    ]),
    register() {
      let errs = isSignUpValid(this.signupForm);
      if (!errs.isValid) {
        showErrorNotification(this, errs.text)
      } else {
        this.signUp(this.signupForm)
          .then(() => {
            showSuccessNotification(this, 'Регистрация прошла успешно.<br>Вы можете войти в свой аккаунт.');
            this.$router.push('signin');
          }).catch(error => {
            showErrorNotification(this, error);
          });
      }
    }
  }
}
</script>

<style scoped>
#mains {
  background: #1D766F;
  position: fixed;
  height: 100%;
}
</style>
