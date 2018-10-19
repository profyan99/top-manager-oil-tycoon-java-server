<template lang="html">
<div class="">
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid d-flex align-items-center text-center justify-content-center h-100">
        <div class="row ">
          <div class="col-md-12">
            <div class="animated slideInDown">
              <h2 class="h1 font-weight-bold white-text pt-5 mb-2">
                  Подтверждение регистрации
              </h2>
              <h4 class="white-text mb-5 pt-2" v-show="status != -1">
                {{ message[status] }}
              </h4>
              <router-link :to="{ name: 'home'}">
                <button class="btn btn-lg waves-effect waves-light animated slideInUp" id="startBtn">
                  НА ГЛАВНУЮ
                </button>
              </router-link>
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

export default {
  name: 'VerificationEmail',
  data() {
    return {
      status: -1,
      message: [
        'Аккаунт подтвержден успешно!',
        'Ошибка. Неверный код подтверждения!',
        'Ошибка. Время подтверждения истекло!'
      ]
    }
  },
  created() {
    this.verifyUser(this.$route.query.token)
      .then(() => {
        this.status = 0;
        console.log('Успешно:: ');
      }).catch((error) => {
        let errorCode = error[0].errorCode;
        if (errorCode == this.$store.getters.getErrors.VERIFICATION_TOKEN_NOT_FOUND) {
          this.status = 1;
        } else if (errorCode == this.$store.getters.getErrors.CONFIRM_TIME_EXPIRED) {
          this.status = 2;
        } else {
          console.log('Ошибка:: ', errorCode, ' msg: ', error[0].message);
        }
      });
  },
  methods: {
    ...mapActions([
      'verifyUser'
    ])
  }
}
</script>

<style lang="css" scoped>
#main {
  background-image: url("./../assets/img/bg.jpg");
  background-size: cover;
  height: 100vh;
}
.tm-dark-color {
  color: #202020;
}

#startBtn {
  background-color: #202020;
}
</style>
