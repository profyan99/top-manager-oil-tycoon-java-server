<template lang="html">
<div class="">
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex mt-5">
        <div class="flex-row w-100 d-flex justify-content-center mt-5">
          <div class="col-md-6 d-flex justify-content-center align-self-start">
            <div class="card w-100 animated fadeInUp">
              <div class="card-body text-center">
                <h3 class="card-title"><strong>{{ profile.firstName }} {{ profile.lastName }}</strong></h3>
                <p class=" mb-5 mt-4">Никнейм: {{ profile.userName }}</p>
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
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'Profile',
  data() {
    return {

    }
  },
  created() {
    this.getDataProfile().catch((error) => {
      console.log(error);
    });
  },
  computed: {
    ...mapGetters([
      'profile'
    ])
  },
  methods: {
    ...mapActions([
      'getDataProfile'
    ])
  }
}
</script>

<style lang="css" scoped>
#startBtn {
  background-color: #202020;
}
</style>
