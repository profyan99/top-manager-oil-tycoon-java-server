<template>
<div>
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex mt-5">
        <div class="flex-row w-100 d-flex justify-content-center mt-5">
          <div class="col-md-8 h-100">
            <div class="card w-100 animated fadeIn fast">
              <div class="card-body text-center">
                <h3 class="card-title pb-2"><strong>Игровые комнаты</strong></h3>
                <hr>
                <ul class=" mb-5 mt-4">
                  <li v-for="mess in messages" class="animated slideInDown" :key="mess.key">{{ mess }}</li>
                </ul>
                <button class="btn waves-effect waves-light animated slideInUp"
                id="startBtn"
                @click="sendMessage">
                    НА ГЛАВНУЮ
                </button>
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
import {
  mapGetters
} from 'vuex';

import * as socket from '../socket'

export default {
  name: 'Rooms',
  data() {
    return {

    }
  },
  computed: {
    messages() {
      return this.$store.getters.getMessages;
    }
  },
  created() {
    if(!this.$store.getters.isConnected) {
      socket.socketConnect().then(() => {
        socket.sendMessage("Hello from frontend!");
      });
    }
  },
  methods: {
    ...mapGetters([
      'profile'
    ]),
    sendMessage() {
      socket.sendMessage("New test message");
    }
  }
}
</script>
<style scoped>
#startBtn {
  background-color: #202020;
}
</style>
