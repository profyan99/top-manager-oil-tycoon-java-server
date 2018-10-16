<template>
<div id="app">
  <notifications group="system-notifications" position="top center" width="30%" classes="vue-notification-custom" :max="1">
  </notifications>
  <nav-top v-if="isNotFoundPage"></nav-top>
  <router-view>

  </router-view>
</div>
</template>

<script>
import NavTop from './NavTop.vue'
import axios from 'axios'

export default {
  name: 'app',
  components: {
    NavTop
  },
  computed: {
    isNotFoundPage() {
      return 'notFound' != this.$route.name;
    }
  }
};
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100%;
  background: #1D766F;
}

.vue-notification-custom {
  padding: 10px;
  margin: 0 5px 5px;

  font-size: 12px;

  color: #ffffff;
  background: #ff384d !important;
  border-left: 5px solid #d61d31;
}
</style>
