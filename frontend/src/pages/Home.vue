<template>
<div>
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid d-flex align-items-center text-center justify-content-center h-100">
        <div class="row ">
          <div class="col-md-12">
            <div class="animated slideInDown">
              <h2 class="h1 font-weight-bold white-text pt-5 mb-2">
                  ЭКОНОМИЧЕСКИЙ СИМУЛЯТОР
              </h2>
              <h4 class="white-text mb-5 pt-2">
                Попробуй себя в роли нефтяного магната
              </h4>
            </div>


            <button class="btn btn-lg waves-effect waves-light animated slideInUp" id="startBtn" @click="start">
                СТАРТ
            </button>

          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</template>
<script>



export default {
  name: 'home',
  data() {
    return {
      bg: ''
    }
  },
  methods: {
    start() {
      if (this.$store.getters.isLoggedIn == false) {
        this.$router.push('signin');
      } else {
        this.$router.push('rooms');
      }
    }
  }
}
</script>
<style>
header,
#main {}

#main {
  background-image: url("./../assets/img/bg.jpg");
  background-size: cover;
  height: 100vh;
}

/*
.navbar-nav li {
  margin-right: 20pt;
} */

.tm-dark-color {
  color: #202020;
}

#startBtn {
  background-color: #202020;
}

</style>
