<template lang="html">
<div class="">

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
      message: ''
    }
  },
  created() {
    this.verifyUser(this.$route.query.token)
    .then(() => {
      this.message = 'Успешно';
      console.log('Успешно:: ');
    }).catch((error) => {
      this.message = 'Провал';
      console.log('Ошибка:: ', error);
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
</style>
