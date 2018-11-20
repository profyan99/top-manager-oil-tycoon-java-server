<template lang="html">
<div class="">
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex mt-5">
        <div class="flex-row w-100 d-flex justify-content-center mt-5">
          <div class="col-md-4 h-100">
            <div class="card w-100 animated fadeIn fast">
              <div class="card-body text-center">
                <h3 class="card-title mb-5 mt-3"><strong>Подтверждение регистрации</strong></h3>
                <p class=" mb-5 mt-4">{{ message[status] }}</p>
                <router-link :to="{ name: 'home'}">
                  <button class="btn waves-effect waves-light animated slideInUp" id="startBtn">
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
          this.status = 1;
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
