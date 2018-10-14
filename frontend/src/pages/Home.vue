<template>
<div>
  <header>
    <navbar position="top" expand="large" dark>
      <mdb-navbar-brand href="#">
        <span class="font-weight-bold">
          TOP MANAGER
          <span class="tm-dark-color ">Oil Tycoon</span>
        </span>
      </mdb-navbar-brand>
      <navbar-collapse>
        <navbar-nav>
          <navbar-item href="#" active>Главная</navbar-item>
          <navbar-item href="#">Об игре</navbar-item>
          <navbar-item href="#">Контакты</navbar-item>
        </navbar-nav>
        <div class="text-inline mr-5 p-0">
          <div v-if="isLoggedIn">

            <dropdown>
              <dropdown-toggle slot="toggle">
                {{ userProfile.name }} {{ userProfile.surname }}
              </dropdown-toggle>
              <dropdown-menu>
                  <dropdown-item>Профиль</dropdown-item>
                  <dropdown-item>Настройки</dropdown-item>
                  <dropdown-item>Выйти</dropdown-item>
              </dropdown-menu>
            </dropdown>
          </div>
          <div v-else>
            <navbar-nav>
              <router-link :to="{ name: 'signin'}">
                <navbar-item>Вход</navbar-item>
              </router-link>
              <router-link :to="{ name: 'signup'}">
                <navbar-item>Регистрация</navbar-item>
              </router-link>
            </navbar-nav>
          </div>
        </div>
      </navbar-collapse>
    </navbar>
  </header>
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
import {
  Navbar,
  NavbarItem,
  NavbarNav,
  NavbarCollapse,
  mdbNavbarBrand,
  Footer,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownToggle
} from 'mdbvue';


export default {
  name: 'home',
  components: {
    Navbar,
    NavbarItem,
    NavbarNav,
    NavbarCollapse,
    mdbNavbarBrand,
    Footer,
    Dropdown,
    DropdownItem,
    DropdownMenu,
    DropdownToggle
  },
  data() {
    return {
      bg: ''
    }
  },
  computed: {
    isLoggedIn: function() {
      return this.$store.getters.isLoggedIn;
    },
    userProfile: function() {
      if(this.isLoggedIn) {
        return this.$store.getters.profile;
      }
    }
  },
  methods: {
    start() {
      // this.$notify({
      //   group: 'system-notifications',
      //   title: 'Ошибка',
      //   type: 'warn',
      //   text: 'WHAT??',
      //   duration: -1,
      // });
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

nav {
  background-color: #0c453e;
}
</style>
