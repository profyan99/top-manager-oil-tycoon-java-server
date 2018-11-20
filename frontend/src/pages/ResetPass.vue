<template lang="html">
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex mt-5">
        <div class="flex-row w-100 d-flex justify-content-center mt-5">
          <div class="col-md-4 h-100">
            <div class="card w-100 animated fadeIn fast">
              <div class="card-body">
                <h3 class="dark-grey-text text-center mb-5 mt-3"><strong>Восстановление пароля</strong></h3>
                <form class="p-4" @submit.prevent="resetPass">
                  <div class="row justify-content-center animated slideInDown fast">
                    <div class="col-md-10">
                      <p>Введите, пожалуйста, новый пароль для вашего аккаунта.</p>
                      <div class="md-form mt-5">
                        <mdb-input type="password" label="Пароль" icon="lock grey-text" v-model="password" />
                      </div>
                      <div class="md-form mt-5">
                        <mdb-input type="password" label="Повторите пароль" icon="lock grey-text" v-model="confirmPassword" />
                      </div>
                    </div>
                  </div>

                  <div class="row justify-content-center mt-5 text-center pb-3 animated slideInUp fast">
                    <div class="col-md-10">
                      <button class="btn btn-primary btn-block mb-3">Изменить</button>
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
</template>

<script>
import {
  mapActions
} from 'vuex';
import {
  mdbInput
} from "mdbvue";
import {
  isPasswordValid
} from "../validators"
import {
  showErrorNotification,
  showSuccessNotification
} from '../store/util/functions'

export default {
  name: 'ResetPass',
  components: {
    mdbInput
  },
  data() {
    return {
      password: '',
      confirmPassword: '',
      success: false
    }
  },
  methods: {
    ...mapActions([
      'resetPassword'
    ]),
    resetPass() {
      let passModel = {
        pass: this.password,
        confirmPass: this.confirmPassword,
        token: this.$route.query.token
      };
      let error = isPasswordValid(passModel);
      if (!error.isValid) {
        showErrorNotification(this, error.text);
      } else {
        this.resetPassword(passModel)
          .then(() => {
            showSuccessNotification(this, 'Пароль успешно изменен.<br>Вы можете войти в свой аккаунт.')
            this.$router.push('signin');
          }).catch(error => {
            console.log('Error login: ', error);
            showErrorNotification(this, error);
          });
      }
    }
  }
}
</script>

<style lang="css" scoped>
</style>
