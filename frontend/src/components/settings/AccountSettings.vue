<template lang="html">
  <div class="">
    <div class="row">
      <div class="col-md-6">
        <form @submit.prevent="editProfile">
          <div class="row justify-content-center animated slideInDown fast">
            <div class="col">
              <div class="md-form mt-5">
                <mdb-input type="text" label="Имя" :placeholder="profile.firstName" icon="user grey-text" v-model="form.name" />
              </div>
              <div class="md-form">
                <mdb-input type="text" label="Фамилия" :placeholder="profile.lastName" icon="user grey-text" v-model="form.surname" />
              </div>
              <div class="md-form">
                <mdb-input type="password" label="Старый пароль" icon="lock grey-text" v-model="form.oldPass" />
              </div>
              <div class="md-form">
                <mdb-input type="password" label="Новый пароль" icon="lock grey-text" v-model="form.newPass" />
              </div>
            </div>
          </div>
          <div class="row mt-5">
            <div class="col-md-4">
              <button class="btn btn-primary btn-block mb-3 animated slideInUp global-dark-btn">Изменить</button>
            </div>
            <div class="col">
              <h6 class="mt-4 text-muted" v-show="success">Успешно</h6>
            </div>
          </div>
        </form>
      </div>
      <div class="col ml-2">
        <div class="row">
          <div class="col">
            <blockquote class="blockquote  animated slideInRight fast">
              <p class="bq-title">Info notification</p>
              <p>
                Lorem ipsum dolor sit amet, consectetur adipisicing elit. Maiores quibusdam dignissimos itaque harum illo!
                Quidem, corporis at quae tempore nisi impedit cupiditate perferendis nesciunt, ex dolores doloremque!
                Sit, rem, in?
                <br>
                Lorem ipsum dolor sit amet, consectetur adipisicing elit. Maiores quibusdam dignissimos itaque harum illo!
                Quidem, corporis at quae tempore nisi impedit cupiditate perferendis nesciunt, ex dolores doloremque!
                Sit, rem, in?
              </p>

            </blockquote>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  mdbInput
} from "mdbvue";

import {
  mapGetters,
  mapActions
} from 'vuex'

import {
  isEditProfileValid
} from '../../validators.js'

import {
  showErrorNotification
} from '../../store/util/functions'


export default {
  name: 'AccountSettings',
  components: {
    mdbInput
  },
  data() {
    return {
      form: {
        name: '',
        surname: '',
        oldPass: '',
        newPass: ''
      },
      success: false
    }
  },
  computed: {
    ...mapGetters([
      'profile'
    ])
  },
  methods: {
    ...mapActions([
      'updateProfile'
    ]),
    editProfile() {
      let errs = isEditProfileValid(this.form);
      if (!errs.isValid) {
        showErrorNotification(this, errs.text);
      } else {
        this.updateProfile(this.form)
          .then(() => {
            this.success = true;
          }).catch(error => {
            showErrorNotification(this, error);
            console.log('Error: ', error);
          });
      }
    }
  }
}
</script>

<style lang="css" scoped>

</style>
