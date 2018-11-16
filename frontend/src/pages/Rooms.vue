<template>
<div>
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex align-items-center justify-content-center">
        <div class="row row d-flex justify-content-center w-100">
          <div class="col-md-8">
            <div class="card mt-5 animated fadIn">
              <div class="card-body text-center">
                <h3 class="card-title"><strong>Игровые комнаты</strong></h3>
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
    socket.socketConnect().then(() => {
      socket.sendMessage("Hello from frontend!");
    });
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
