<template>
<div>
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex mt-5">
        <div class="flex-row w-100 d-flex justify-content-center mt-5">
          <div class="col-md-4 h-100">
            <div class="card w-100 animated fadeIn fast">
              <div class="card-body">
                <h3 class="dark-grey-text text-center mb-5 mt-3"><strong>Восстановление пароля</strong></h3>
                <div v-if="success" class="p-4">
                  <p class="mb-5" v-if="success">
                    На вашу почту отправлено письмо с ссылкой на восстановление пароля.
                    Время действия ссылки 1 день.
                  </p>
                  <div class=" text-center">
                    <router-link :to="{ name: 'home'}">
                      <button class="btn waves-effect waves-light animated slideInUp" id="startBtn">
                        НА ГЛАВНУЮ
                      </button>
                    </router-link>
                  </div>

                </div>
                <form class="p-4" @submit.prevent="forgotPass" v-else>
                  <div class="row justify-content-center animated slideInDown fast">
                    <div class="col-md-10">
                      <p>Введите, пожалуйста, адрес электронной почты от вашего аккаунта.
                        Вам будет отправлена ссылка на восстановление пароля от вашей учетной записи.
                      </p>
                      <div class="md-form mt-5">
                        <mdb-input type="text" label="Почта" icon="envelope grey-text" v-model="email" />
                      </div>
                    </div>
                  </div>

                  <div class="row justify-content-center mt-5 text-center pb-3 animated slideInUp fast">
                    <div class="col-md-10">
                      <button class="btn btn-primary btn-block mb-3">Восстановить</button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>
<script>
import {
  mapActions
} from 'vuex';
import {
  mdbInput
} from "mdbvue";

import {
  showErrorNotification
} from '../store/util/functions'

import {
  isEmailValid
} from "../validators"

export default {
  name: 'ForgotPass',
  components: {
    mdbInput
  },
  data() {
    return {
      email: '',
      success: false
    }
  },
  methods: {
    ...mapActions([
      'forgotPassword'
    ]),
    forgotPass() {
      let error = isEmailValid(this.email);
      if (!error.isValid) {
        showErrorNotification(this, error.text);
      } else {
        this.forgotPassword(this.email)
          .then(() => {
            this.success = true;
            //showSuccessNotification(this, 'На вашу почту отправлено письмо с ссылкой на восстановление пароля.');
          }).catch(error => {
            console.log('Error forgotPass: ', error);
            showErrorNotification(this, error);
          });
      }
    }
  }
}
</script>
<style scoped>
#startBtn {
  background-color: #202020;
}
</style>
