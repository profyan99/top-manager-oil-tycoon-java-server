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
                  <li v-for="mess in messages" class="animated slideInDown" :key="mess.key">
                    <room-preview-list v-bind="mess"></room-preview-list>
                  </li>
                </ul>
                <router-link :to="{ name: 'home'}">
                  <button class="btn waves-effect waves-light animated slideInUp" id="startBtn">
                    НА ГЛАВНУЮ
                  </button>
                </router-link>
                <button class="btn waves-effect waves-light animated slideInUp" id="startBtn" @click="addNewRoom">
                    СОЗДАТЬ
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
  mapGetters,
  mapActions
} from 'vuex';

import RoomPreviewList from '../components/RoomPreviewList.vue'

import * as socket from '../socket'

export default {
  name: 'Rooms',
  components: {
    RoomPreviewList
  },
  data() {
    return {
      number: 10
    }
  },
  computed: {
    messages() {
      return this.$store.getters.getMessages;
    }
  },
  created() {
    if (!this.$store.getters.isConnected) {
      socket.socketConnect().then(() => {
        socket.sendMessage("Hello from frontend!");
      });
    }
  },
  methods: {
    ...mapGetters([
      'profile'
    ]),
    ...mapActions([
      'addRoom'
    ]),
    addNewRoom() {
      this.number++;
      this.addRoom(this.number).then(() => {
        console.log("Success");
      }).catch(error => {
        console.log('Error adding room: ', error);
      });
    }
  }
}
</script>
<style scoped>
#startBtn {
  background-color: #202020;
}
</style>
