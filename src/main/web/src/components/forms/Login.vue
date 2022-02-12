<template>

  <div>

    <div v-show="showLogin">

      <h1 v-html="lang.page.login.h1"/>
      <!--h1 v-html="lang.get('test', testreplace)" /-->
      <hr/>

      <b-form id="form">

        <b-form-group :label="lang.form.login.login"
                      :state="login_state"
                      label-for="login">
          <b-form-input id="login" v-model="login" :placeholder="lang.form.login.login_placeholder" trim/>
        </b-form-group>

        <b-form-group :label="lang.form.login.password"
                      :state="password_state"
                      label-for="password">
          <b-form-input id="password" v-model="password" :placeholder="lang.form.login.password_placeholder" trim
                        type="password"/>
        </b-form-group>

        <b-button :disabled="isSubmitDisabled" size="sm" @click="submitLogin">Login</b-button>
        &nbsp;
        <router-link :to="{name: 'register'}"><span v-html="lang.page.login.link.register"/></router-link>
        &nbsp;
        <b-link @click="showLogin=false"><span v-html="lang.page.login.link.forgot_password"/></b-link>

      </b-form>
    </div>

    <div v-show="!showLogin">
      <h1 v-html="lang.page.login.passwordForget.h1"/>
      <hr/>

      <b-form>

        <b-form-group :label="lang.page.login.passwordForget.text"
                      label-for="email">
          <b-form-input id="email" v-model="email" :placeholder="lang.form.login.email_placeholder" trim type="email"/>
        </b-form-group>

        <b-button :disabled="isSubmitEmailDisabled" size="sm" @click="submitEmail">Restore Login</b-button>
        &nbsp;
        <b-link @click="showLogin=true"><span v-html="lang.page.login.link.regular_login"/></b-link>
      </b-form>
    </div>

  </div>

</template>

<script>
import axios from 'axios';
import utils from '@/assets/utils.js';
import lang from '@/assets/lang.js';

export default {

  data: () => ({

    lang: lang,

    login: null,
    password: null,
    email: null,

    showLogin: true,
  }),

  mounted() {
    //console.log('Login.mounted() router', this.$router);
  },

  computed: {

    login_state() {
      return this.login == null || this.login.length > 0;
    },

    password_state() {
      return this.password == null || this.password.length > 0;
    },

    email_state() {
      return utils.isEmail(this.email);
    },

    isSubmitDisabled() {
      return this.login == null
          || this.password == null
          || !this.login_state
          || !this.password_state;
    },

    isSubmitEmailDisabled() {
      //console.log('email', this.email, 'state', this.email_state);
      return this.email == null || !this.email_state;
    }
  },

  methods: {

    submitLogin() {

      axios.post('/api/login', {login: this.login, password: this.password})
          .then(response => {

                this.$login(this.login, response.data);

                this.login = '';
                this.password = '';
                this.email = '';

                this.$router.push('/');

              },
              error => {
                console.log('error', error);
                this.$eventbus.$emit('logout');
              }
          );
    },

    submitEmail() {

      axios.post('/api/login/renew', {email: this.email})
          .then(response => {
                console.log(response);
              },
              error => {
                console.warn(error);
              }
          );
    }
  }
}

</script>

<style scoped lang="scss">


</style>