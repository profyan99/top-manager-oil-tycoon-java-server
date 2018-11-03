<template lang="html">
<div class="">
  <div class="view" id="main">
    <div class="mask rgba-teal-strong">
      <div class="container-fluid h-100 d-flex mt-5">
        <div class="flex-row w-100 d-flex justify-content-center mt-5">
          <div class="col-md-2 d-flex justify-content-center align-self-start">
            <div class="card w-100 animated fadeInUp">
              <div class="card-body">
                <list-group>
                  <list-group-item v-for="(item, index) in settingsItems"
                  :action="true" :active="item.active" @click.native="settingsSelect(index)">
                    {{ item.title }}
                  </list-group-item>

                </list-group>
              </div>
            </div>
          </div>
          <div class="col-md-8 d-flex h-75">
            <div class="card w-100 animated fadeInDown fast">
              <div class="card-body">
                <h2 class="card-title pl-5 pb-2"><strong>Настройки</strong></h2>
                <hr>
                <div class="pt-2">
                  <component :is="currentComponent"></component>
                </div>
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
  ListGroup,
  ListGroupItem
} from "mdbvue";

export default {
  name: 'Settings',
  data() {
    return {
      currentComponent: null,
      currentEl: 0,
      settingsItems: [
        {
          title: "Аккаунт",
          active: true
        },
        {
          title: "Безопасность",
          active: false
        },
        {
          title: "Игровые настройки",
          active: false
        },
        {
          title: "Уведомления",
          active: false
        },
        {
          title: "Социальные сети",
          active: false
        }
      ]
    }
  },
  components: {
    ListGroup,
    ListGroupItem
  },
  methods: {
    settingsSelect(selected) {
      this.settingsItems[this.currentEl].active = false;
      this.settingsItems[selected].active = true;
      this.currentEl = selected;
    }
  }
}
</script>

<style lang="css" scoped>

.list-group-item.active {
  background-color: #0c453e !important;
  border-color: #0c453e !important;
}




</style>
