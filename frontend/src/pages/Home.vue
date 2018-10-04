<template>
<div>
  <header>
    <nav class="navbar navbar-expand-lg navbar-dark fixed-top scrolling-navbar">
      <div class="container">
        <a href="#" class="navbar-brand">
            <span class="font-weight-bold">
              TOP MANAGER
              <span class="tm-dark-color ">Oil Tycoon</span>
            </span>

          </a>
      </div>

      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navId" aria-controls="navId"
        aria-expanded="false" aria-label="Toggle Navigation">
          <span class="navbar-toggler-icon"></span>
        </button>

      <div class="collapse navbar-collapse" id="navId">
        <ul class="navbar-nav mr-auto smooth-scroll">
          <li class="nav-item">
            <a href="#" class="nav-link waves-effect waves-light">Главная</a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link waves-effect waves-light">Об игре</a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link waves-effect waves-light">Контакты</a>
          </li>
        </ul>
      </div>
    </nav>

    <div class="view" id="main">
      <div class="mask rgba-teal-strong">
        <div class="container-fluid d-flex align-items-center justify-content-center h-100">
          <div class="row ">
            <div class="col-md-12">
              <h2 class="h1 font-weight-bold white-text pt-5 mb-2">
                  ЭКОНОМИЧЕСКИЙ СИМУЛЯТОР
              </h2>
              <h4 class="white-text mb-5 pt-2">
                Попробуй себя в роли нефтяного магната
              </h4>

              <img src="/img/bg.jpg" alt="">


              <button class="btn btn-lg waves-effect waves-light" id="startBtn" @click="start">
                  СТАРТ
              </button>

              </div>
            </div>
          </div>
        </div>
      </div>
  </header>
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
      if(this.$store.getters.profile === null) {
        this.$router.push('signup');
      } else {
        this.$router.push('rooms');
      }
    }
  }
}
</script>
<style>
header,
#main {
  height: 100vh;
}

#main {
  background-image: url("./../assets/img/bg.jpg");
  background-size: cover;
}

.navbar-nav li {
  margin-right: 20pt;
}

.tm-dark-color {
  color: #202020;
}

#startBtn {
  background-color: #202020;
}

nav {
  background-color: #0c453e;
}
</style>
