<template>

  <div>

    <h1 v-text="texts.page.register.h1" />
    <hr/>

    <Messages ref="messages" />

    <div ref="form" id="form">

      <div>
        <p class="formFieldDescription" v-html="texts.form.register.name" :class="{error: errorName}"/>
        <b-form-input v-model="name" :placeholder="texts.form.register.name_placeholder" @blur="validate('name')" :class="{error: errorName}"/>

        <p class="formFieldDescription" v-html="texts.form.register.login" :class="{error: errorLogin}"/>
        <b-form-input v-model="login" :placeholder="texts.form.register.login_placeholder" @blur="validate('login')" :class="{error: errorLogin}"/>

        <p class="formFieldDescription" v-html="texts.form.register.password" />
        <b-form-input type="password" v-model="password" :placeholder="texts.form.register.password_placeholder" @blur="validate('password')" :class="{error: errorPassword}" />
        <b-form-input type="password" v-model="password2" :placeholder="texts.form.register.password2_placeholder" @blur="validate('password2')" :class="{error: errorPassword2}" />

        <p class="formFieldDescription" v-html="texts.form.register.email" :class="{error: errorEmail}"/>
        <b-form-input type="email" v-model="email" :placeholder="texts.form.register.email_placeholder" @blur="validate('email')" :class="{error: errorEmail}"/>
      </div>

      <div>
        <p class="spaceTopLeft" v-html="texts.form.register.preSubmitInfo" />
      </div>

      <div>
        <b-button @click="submit" @mouseover="validate" :disabled="disableSubmit" class="spaceTop">Register</b-button>
        <!--b-button @click="showConfirmation">ShowConfirmation</b-button-->
      </div>
    </div>

    <div ref="confirmation" id="confirmation">
      <div>
        <p v-html="texts.page.register.confirmation" />
      </div>
    </div>
  </div>

</template>

<script>
import axios from 'axios'
import utils from '@/assets/utils.js'
import lang from '@/assets/lang.js'

import Messages from '@/components/Messages.vue'

export default {

  components: {
    Messages
  },

  data: ()=>({

    name: null,
    errorName: false,
    login: null,
    errorLogin: false,

    password: null,
    errorPassword: false,
    password2: null,
    errorPassword2: false,

    email: null,
    errorEmail: false,

    disableSubmit: true,

    texts: lang.getTexts(),

    error: {
      errorPassword: ''
    }
  }),

  methods: {

    validate(field) {

      field = typeof field != 'string' ? null : field;
      console.log('validate()', field, field == null);

      if ( field != null ) {
        this.$refs.messages.clear( field ); // leave other error messages as they are
        // leave this.disableSubmit as it is. Only thing can happen is that the state becomes false due to one failre
      } else {
        this.$refs.messages.clear(); // will be reset on error
        this.disableSubmit = false;
      }

      if (field == null || 'name'===field) {

        this.name = utils.trimToNull(this.name);
        this.errorName = false;

        if (!utils.matches('[a-zA-Z0-9].+', this.name)) {
          this.errorName = true;
          this.$refs.messages.error(this.texts.form.register.name_error);
          this.disableSubmit = true;
        }
      }

      if (field == null || 'login'===field) {

        this.login = utils.trimToNull(this.login);
        this.errorLogin = false;

        if (!utils.matches('[a-zA-Z0-9].+', this.login)) {
          this.errorLogin = true;
          this.$refs.messages.error(this.texts.form.register.login_error);
          this.disableSubmit = true;
        }
      }

      if (field == null || 'password'==field) {

        this.password = utils.trimToNull(this.password);
        this.errorPassword = false;

        if (utils.isBlank(this.password)) {

          this.errorPassword = true;
          this.$refs.messages.error(this.texts.form.register.password_error);
          this.disableSubmit = true;

        } else {

          utils.validateRemote( { 'global.password': this.password }, ()=>{
            this.errorPassword = true;
            this.$refs.messages.error(this.texts.form.register.password_error);
            this.disableSubmit = true;
          } );
        }
      }

      if (field == null || 'password2'==field) {

        this.password2 = utils.trimToNull(this.password2);
        this.errorPassword2 = false;

        if ( this.password2 == null || this.password2 !== this.password ) {
          this.errorPassword2 = true;
          this.$refs.messages.error(this.texts.form.register.password2_error);
          this.disableSubmit = true;
        }
      }

      if ( field==null || 'email'===field ) {

        this.email = utils.trimToNull(this.email);
        this.errorEmail = false;

        if (utils.isBlank(this.email)) {
          this.errorEmail = true;
          this.$refs.messages.error(this.texts.form.register.email_error);
          this.disableSubmit = true;
        }
      }

      console.log('disableSubmit', this.disableSubmit);
      if ( this.field == null && this.disableSubmit == false) {
        this.$refs.messages.clear();
      }
    },

    submit() {

      console.log('submit()');

      this.validate()

      if ( this.disableSubmit ) {
        console.log('validation failure.');
        return;
      }

      const requestBody = {
        login: utils.trimToNull(this.login),
        password: utils.trimToNull(this.password),
        name: utils.trimToNull(this.name),
        email: utils.trimToNull(this.email),
      };

      axios.post('/api/register', requestBody)
          .then(
              response => {
                console.log('ok', response);
                this.showConfirmation();
              },
              error => {
                console.log('error', error);
                this.message = 'There are errors';
                console.log('response', error.response);
              }
          );
    },

    showConfirmation() {
      this.$refs.form.style.display='none';
      this.$refs.confirmation.style.display='inline';
    }
  }
}

</script>

<style scoped lang="scss">

div#confirmation {
  display: none;
}

</style>