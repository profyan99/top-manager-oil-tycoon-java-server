<template>
<div class="container-fluid" id="mains">
  <div class="row d-flex justify-content-center">
    <div class="col-md-4">
      <div class="card mt-5">
        <div class="card-body">
          <form class="p-3">
            <h2 class="dark-grey-text text-center"><strong>Регистрация</strong></h2>
            <div class="row justify-content-center">
              <div class="col-md-10">
                <div class="form-row md-form mt-5">
                  <i class="fa fa-user prefix grey-text"></i>
                  <input type="text" id="inputIconEx1" class="form-control">
                  <label for="inputIconEx1">Имя</label>
                </div>
                <div class="form-row md-form">
                  <i class="fa fa-user prefix grey-text"></i>
                  <input type="text" id="inputIconEx2" class="form-control">
                  <label for="inputIconEx2">Фамилия</label>
                </div>
                <div class="form-row md-form">
                  <i class="fa fa-user prefix grey-text"></i>
                  <input type="text" id="inputIconEx3" class="form-control">
                  <label for="inputIconEx3">Никнейм</label>
                </div>
                <div class="form-row md-form">
                  <i class="fa fa-envelope prefix grey-text"></i>
                  <input type="text" id="inputIconEx4" class="form-control">
                  <label for="inputIconEx4">Почта</label>
                </div>
                <div class="form-row md-form">
                  <i class="fa fa-lock prefix grey-text"></i>
                  <input type="text" id="inputIconEx5" class="form-control">
                  <label for="inputIconEx5">Пароль</label>
                </div>
                <div class="form-row md-form">
                  <i class="fa fa-lock prefix grey-text"></i>
                  <input type="text" id="inputIconEx6" class="form-control">
                  <label for="inputIconEx6">Пароль еще раз</label>
                </div>
              </div>
            </div>

            <div class="row justify-content-center mt-2">
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
                  Нажимая <em>Отправить</em>, вы соглашаетесь с парвилами сервиса.
                </p>
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
export default {
  name: "Signup",
  data() {
    return {
      vkontakte: this.$store.getters.authLinks.vk,
      google: this.$store.getters.authLinks.google
    }
  }
}
</script>

<style scoped>
#mains {
  background: #1D766F;
  height: 100vh;
}
</style>
