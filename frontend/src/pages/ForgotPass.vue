<template>
<div>
  <div class="container-fluid" id="signin">
    <div class="row d-flex justify-content-center">
      <div class="col-md-4">
        <div class="card mt-5">
          <div class="card-body">
            <form class="p-4" @submit.prevent="forgotPass">
              <h3 class="dark-grey-text text-center mb-5"><strong>Восстановление пароля</strong></h3>
              <div class="row justify-content-center">
                <div class="col-md-10">
                  <p>Введите пожалуйста адрес электронной почты от вашего аккаунта. Вам будет отправлена ссылка на восстановление пароля от вашей учетной записи.</p>
                  <div class="md-form mt-5">
                    <mdb-input type="text" label="Почта" icon="envelope grey-text" v-model="email" />
                  </div>
                </div>
              </div>

              <div class="row justify-content-center mt-5 text-center pb-3">
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
</template>
<script>
import { mapActions } from 'vuex';
import { mdbInput } from "mdbvue";

export default {
  name: 'ForgotPass',
  components: {
    mdbInput
  },
  data() {
    return {
      email: ''
    }
  },
  methods: {
    forgotPass() {

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
