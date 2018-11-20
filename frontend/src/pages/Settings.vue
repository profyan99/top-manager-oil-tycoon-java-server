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
                  :action="true" :active="item.active"
                   @click.native="settingsSelect(index)"
                   :key="item.title">
                    {{ item.title }}
                  </list-group-item>

                </list-group>
              </div>
            </div>
          </div>
          <div class="col-md-8 d-flex h-75">
            <div class="card w-100 animated fadeInDown fast px-5 py-2">
              <div class="card-body h-100">
                <h2 class="card-title pb-2"><strong>Настройки</strong></h2>
                <hr>
                <div class="pt-2">
                  <h4 class="dark-grey-text"><strong>{{ settingsItems[currentEl].title }}</strong></h4>
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

import AccountSettings from '../components/settings/AccountSettings'
import GameSettings from '../components/settings/GameSettings'
import NotificationSettings from '../components/settings/NotificationSettings'
import SecuritySettings from '../components/settings/SecuritySettings'
import SocialSettings from '../components/settings/SocialSettings'

export default {
  name: 'Settings',
  data() {
    return {
      currentComponent: AccountSettings,
      currentEl: 0,
      settingsItems: [{
          title: "Аккаунт",
          active: true,
          component: AccountSettings
        },
        {
          title: "Безопасность",
          active: false,
          component: SecuritySettings
        },
        {
          title: "Игровые настройки",
          active: false,
          component: GameSettings
        },
        {
          title: "Уведомления",
          active: false,
          component: NotificationSettings
        },
        {
          title: "Социальные сети",
          active: false,
          component: SocialSettings
        }
      ]
    }
  },
  components: {
    ListGroup,
    ListGroupItem,
    AccountSettings,
    GameSettings,
    NotificationSettings,
    SecuritySettings,
    SocialSettings
  },
  methods: {
    settingsSelect(selected) {
      this.settingsItems[this.currentEl].active = false;
      this.settingsItems[selected].active = true;
      this.currentEl = selected;
      this.currentComponent = this.settingsItems[selected].component;
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
