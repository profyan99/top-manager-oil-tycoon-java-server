<template>
<div>

</div>
</template>
<script>
import {
  mapGetters
} from 'vuex';

export default {
  name: 'Rooms',
  data() {
    return {

    }
  },
  created() {
    if(this.$store.getters.profile.roles.includes('UNVERIFIED')) {
      console.log('INNN');
      this.$notify({
        group: 'system-notifications',
        title: 'Подтверждение регистрации',
        type: 'warn',
        text: 'На ваш электронный адрес <b>'+this.$store.getters.profile.email+'</b> \
        было выслано письмо с ссылкой на подтверждение регистрации аккаунта.',
        duration: -1,
      });
    }
  },
  methods: {
    ...mapGetters([
      'profile'
    ])
  }
}
</script>
<style scoped>
</style>
