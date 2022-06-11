<template>

  <div>

    <h1 v-text="lang.page.register.h1"/>
    <hr/>

    <Messages ref="messages" />

    <b-form v-show="!showSentConfirmation" id="form" validation="form">

      <b-form-group :description="lang.form.register.name_description"
                    :invalid-feedback="lang.form.register.name_error"
                    :label="lang.form.register.name"
                    :state="name_state"
                    label-for="name">
        <b-form-input id="name" v-model="name" :placeholder="lang.form.register.name_placeholder" trim/>
      </b-form-group>

      <b-form-group :description="lang.form.register.login_description"
                    :invalid-feedback="lang.form.register.login_error"
                    :label="lang.form.register.login"
                    :state="login_state"
                    label-for="login">
        <b-form-input id="name" v-model="login" :placeholder="lang.form.register.login_placeholder" trim/>
      </b-form-group>

      <b-form-group :description="lang.form.register.password_description"
                    :invalid-feedback="lang.form.register.password_error"
                    :label="lang.form.register.password"
                    :state="password_state"
                    label-for="password">
        <b-form-input id="password" v-model="password" :placeholder="lang.form.register.password_placeholder" trim
                      type="password"/>
      </b-form-group>

      <b-form-group :invalid-feedback="lang.form.register.password2_error"
                    :label="lang.form.register.password2"
                    :state="password2_state"
                    label-for="password2">
        <b-form-input id="password2" v-model="password2" :placeholder="lang.form.register.password2_placeholder" trim
                      type="password"/>
      </b-form-group>

      <b-form-group :description="lang.form.register.email_description"
                    :invalid-feedback="lang.form.register.email_error"
                    :label="lang.form.register.email"
                    :state="email_state"
                    label-for="email">
        <b-form-input id="email" v-model="email" :placeholder="lang.form.register.email_placeholder" trim type="email"/>
      </b-form-group>

      <div>
        <p class="spaceTopLeft" v-html="lang.form.register.preSubmitInfo"/>
      </div>

      <div>
        <b-button @click="testFill">TESTfill</b-button>
        <b-button :disabled="isSubmitDisabled" class="spaceTop" @click="submit">Register</b-button>
      </div>
    </b-form>

    <div v-show="showSentConfirmation" ref="confirmation">
      <p v-html="lang.page.register.confirmation"/>
    </div>

  </div>

</template>

<script>
import axios from 'axios';
import utils from '@/assets/js/utils.js';
import lang from '@/assets/js/lang/lang.js';

import Messages from '@/components/Messages.vue'

export default {

  name: 'Register',

  components: {
    Messages
  },

  data: () => ({

    lang: lang,

    name: null,
    login: null,
    password: null,
    password2: null,
    email: null,

    disableSubmit: true,

    showSentConfirmation: false,


  }),

  mounted() {
    console.log('mounted()');

    if (this.$user.isLoggedIn()) {
      this.$router.push('/');
    }
  },

  computed: {

    name_state() {
      return this.name == null || this.name.length > 1;
    },

    login_state() {
      return this.login == null || this.login.length > 1;
    },

    password_state() {

      if (this.password == null) {
        return true;
      }

      let result = this.password.length >= 6;
      result = result && /.*[0-9].*/.test(this.password);
      result = result && /.*[a-z].*/.test(this.password);
      result = result && /.*[A-Z].*/.test(this.password);
      result = result && /[!"§$&/\\()=?+*#'<>,;.:\-_@\\[\]]/.test(this.password);
      result = result && !/[\s]/.test(this.password);
      return result;
    },

    email_state() {
      return this.email == null || utils.isEmail(this.email);
    },

    password2_state() {
      return this.password2 == null || this.password == this.password2;
    },

    isSubmitDisabled() {

      //console.log('isSubmitDisabled()');

      const l = this.login == null || !this.login_state;
      const p = this.password == null || !this.password_state;
      const p2 = this.password2 == null || !this.password2_state;
      const e = this.email == null || !this.email_state;

      const r = l || p || p2 || e;

      //console.log('isSubmitDisabled. -> '+ r, 'login=',l,'|| password=',p,'|| password2=',p2,'|| email=',e);

      return r;
    }
  },

  methods: {

    testFill(event) {
      console.log('testFill', event);
      this.name = 'Huckbert';
      this.login = 'Querbert';
      this.password = 'aA1!aa';
      this.password2 = this.password;
      this.email = 'kuemmel.dss@gmx.de';
    },

    submit(event) {

      console.log('submit()', event);

      const requestBody = {
        login: this.login,
        password: this.password,
        name: this.name,
        email: this.email
      };

      axios.post('/api/register', requestBody)
          .then(
              () => {
                this.showSentConfirmation = true;
              },
              error => {

                console.log('error', error);
                this.message = 'There are errors';
                console.log('response', error.response);
              }
          );
    }
  }
}

</script>

<style scoped lang="scss">

div#confirmation {
  display: none;
}

</style>