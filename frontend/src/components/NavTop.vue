<template>
<div>
  <header>
    <navbar position="top" expand="large" dark>

      <router-link :to="{ name: 'home'}" tag="div">
        <mdb-navbar-brand href="#">
          <span class="font-weight-bold">
              TOP MANAGER {{selected}}
              <span class="tm-dark-color ">Oil Tycoon</span>
          </span>
        </mdb-navbar-brand>
      </router-link>

      <navbar-collapse>
        <navbar-nav>
          <router-link :to="{ name: 'home'}" tag="div">
            <navbar-item href="#" active>Главная</navbar-item>
          </router-link>
          <navbar-item href="#">Об игре</navbar-item>
          <navbar-item href="#">Контакты</navbar-item>
        </navbar-nav>
        <div class="text-inline mr-5 p-0">
          <div v-if="isLoggedIn">

            <dropdown>
              <dropdown-toggle slot="toggle">
                {{ userProfile.firstName }} {{ userProfile.lastName }}
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
  name: 'NavTop',
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
      selected: -1
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
    menu(selected) {
      console.log("EXIT: ", selected);
      this.$store.actions.dispatch('logOut');
      this.$router.push('home');
    }
  }
}
</script>
<style scoped>
nav {
  background-color: #0c453e;
}
</style>
