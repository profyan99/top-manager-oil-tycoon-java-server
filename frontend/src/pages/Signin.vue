<template>
<div class="container-fluid" id="signin">
  <div class="row d-flex justify-content-center">
    <div class="col-md-4">
      <div class="card mt-5">
        <div class="card-body">
          <form class="p-3" @submit.prevent="login">
            <h2 class="dark-grey-text text-center mb-5"><strong>Авторизация</strong></h2>
            <div class="row justify-content-center">
              <div class="col-md-10">
                <div class="form-row md-form mt-5">
                  <i class="fa fa-user prefix grey-text"></i>
                  <input type="text" id="inputLogin" class="form-control">
                  <label for="inputLogin">Логин</label>
                </div>
                <div class="form-row md-form">
                  <i class="fa fa-lock prefix grey-text"></i>
                  <input type="text" id="inputPass" class="form-control">
                  <label for="inputPass">Пароль</label>
                </div>
                <div class="form-row justify-content-around">
                  <div>
                    <div class="custom-control custom-checkbox">
                      <input type="checkbox" class="custom-control-input" id="defaultLoginFormRemember" v-model="signinForm.rememberMe">
                      <label class="custom-control-label" for="defaultLoginFormRemember">Запомнить</label>
                    </div>
                  </div>
                  <div>
                    <a href="">Забыли пароль?</a>
                  </div>
                </div>
              </div>
            </div>

            <div class="row justify-content-center mt-5">
              <div class="col-md-10">
                <button class="btn btn-primary btn-block mb-3">Войти</button>
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
</template>

<script>

import { mapActions } from 'vuex'

export default {
  name: 'Login',
  data() {
    return {
      vkontakte: this.$store.getters.authLinks.vk,
      google: this.$store.getters.authLinks.google,
      signinForm: {
        login: '',
        pass: '',
        rememberMe: false
      }
    }
  },
  methods: {
    ...mapActions([
      'signIn'
    ]),
    login() {
       this.signIn(this.signinForm)
      .then(() => {
        this.$router.push('rooms');
      }).catch(error => {
        console.log('Error: '+error);
      });
    }
  }
}
</script>
<style scoped>
#signin {
  background: #1D766F;
  height: 100vh;
}
</style>
