<template>

  <div>

    <div v-show="showLogin">

      <h1 v-html="lang.page.login.h1"/>
      <!--h1 v-html="lang.get('test', testreplace)" /-->
      <hr/>

      <Messages ref="messages"/>

      <b-form id="form">

        <b-form-invalid-feedback :state="formSubmitSuccess" v-html="lang.get('form.login.fail')"/>

        <b-form-group :label="lang.form.login.login"
                      :state="login_state"
                      label-for="login">
          <b-form-input id="login" v-model="login" :placeholder="lang.form.login.login_placeholder" required trim/>
        </b-form-group>

        <b-form-group :label="lang.form.login.password"
                      :state="password_state"
                      label-for="password">
          <b-form-input id="password" v-model="password" :placeholder="lang.form.login.password_placeholder"
                        required trim type="password"
                        @dblclick="dblClickTest"/>
        </b-form-group>

        <b-form-group v-if="confirmationcode != null"
                      :label="lang.form.login.confirmationcode"
                      label-for="confirmationcode">
          <b-form-input id="confirmationcode" v-model="confirmationcode"
                        :placeholder="lang.form.login.confirmationcode_placeholder" trim/>
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

        <b-form-group :label="lang.page.login.passwordForget.text" label-for="email">
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
import user from '@/assets/user.js';
import utils from '@/assets/utils.js';
import lang from '@/assets/lang.js';
import Messages from '@/components/Messages.vue'
import Vue from "vue";

export default {

  name: 'Login',

  components: {
    Messages
  },

  data: () => ({

    lang: lang,

    login: null,
    password: null,
    confirmationcode: null,

    email: null,

    showLogin: true,

    formSubmitSuccess: true
  }),

  created() {

    const usps = new URLSearchParams(window.location.search);
    const login = usps.get('login');
    const cc = usps.get('cc');

    this.login = login == null || login.trim() == '' ? null : login.trim();
    this.confirmationcode = cc == null ? null : cc.trim();
  },

  mounted() {

    //console.log('Login.mounted() router', this.$router);
    //new URLSearchParams(window.location.search);
    //console.log('Login MOUNTED user:', user);
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

                this.formSubmitSuccess = true;

                if (user.isToken()) {
                  // TODO logout if existing user is logged in
                  console.warn('user is already logged in', user);
                }

                console.log('login success', response.data);

                user.login = response.data.login;
                user.token = response.data.token;
                user.rrs = response.data.rrs;
                user.store();

                this.login = '';
                this.password = '';
                this.email = '';

                this.$router.push('/');

                console.log('post login user', user);

                Vue.prototype.$eventbus.$emit('login', user);
              },
              error => {

                if (error.response.status == 401) {

                  this.formSubmitSuccess = false;

                  this.password = '';
                  this.email = '';

                } else {

                  console.error(error.response);
                }
              }
          );
    },

    submitEmail() {

      axios.post('/api/login/renew', {email: this.email})
          .then(() /*(response)*/ => {

                //console.log(response);

                this.email = null;
                this.password = null;
                this.confirmationcode = null;

                this.showLogin = true;

                this.$eventbus.$emit('messages', 'add', 'FRANZ!');

              },
              error => {

                console.warn('login failed', error);
                console.warn('error.response', error.response);
              }
          );
    },

    dblClickTest() {
      if (this.login === 'test' && (this.password == null || this.password == '')) {
        this.password = 'Test_123';
      }
    }

  }
}

</script>

<style scoped lang="scss">


</style>