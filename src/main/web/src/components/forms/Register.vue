<template>

  <div>

    <h1 v-text="texts.page.register.h1" />
    <hr/>

    <Messages ref="messages" />

    <b-form id="form" validation="form">

      <div>

        <p class="formFieldDescription" validation="name" v-html="texts.form.register.name"/>
        <b-form-input v-model="name" :placeholder="texts.form.register.name_placeholder" validation="name"/>

        <p class="formFieldDescription" validation="login" v-html="texts.form.register.login"/>
        <b-form-input v-model="login" :placeholder="texts.form.register.login_placeholder" validation="login"/>

        <p class="formFieldDescription" v-html="texts.form.register.password"/>
        <b-form-input ref="password" v-model="password" :placeholder="texts.form.register.password_placeholder"
                      type="password" validation="password"/>
        <b-form-input ref="password2" v-model="password2" :placeholder="texts.form.register.password2_placeholder"
                      type="password" validation="password2"/>

        <p class="formFieldDescription" validation="email" v-html="texts.form.register.email"/>
        <b-form-input v-model="email" :placeholder="texts.form.register.email_placeholder" type="email"
                      validation="email"/>

        <b-textarea name="text" placeholder="for testing..." validation="ta"></b-textarea>

        <b-form-select name="sel" style="width: 50px;">
          <b-select-option value="A">A</b-select-option>
          <b-select-option value="B">B</b-select-option>
        </b-form-select>

      </div>

      <div>
        <p class="spaceTopLeft" v-html="texts.form.register.preSubmitInfo"/>
      </div>

      <div>
        <b-button class="spaceTop" @click="submit">Register</b-button>
      </div>

    </b-form>

    <div ref="confirmation">
      <i style="color: white;">this is the answer being displayed on successful submit</i>
      <div>
        <p v-html="texts.page.register.confirmation"/>
      </div>
    </div>
  </div>

</template>

<script>
import axios from 'axios'
import utils from '@/assets/utils.js'
import lang from '@/assets/lang.js'
import v from '@/assets/validation.js'

import Messages from '@/components/Messages.vue'

export default {

  name: 'Register',

  components: {
    Messages
  },

  data: () => ({

    name: null,
    login: null,
    password: null,
    password2: null,
    email: null,

    disableSubmit: true,

    texts: lang.getTexts(),

  }),

  mounted() {

    v.init(this, {
      form: {
        name: {
          check: /[a-zA-Z0-9]{2,}/g,
          message: this.texts.form.register.name_error
        },
        login: {
          check: /[a-zA-Z0-9]{2,}/g,
          message: this.texts.form.register.login_error
        },
        password: {
          check: /[a-zA-Z0-9]{2,}/g,
          message: this.texts.form.register.password_error
        },
        password2: {
          check: v.checkEqualField('form', 'password'),
          message: this.texts.form.register.password2_error
        },
        email: {
          check: v.checkEmail,
          message: this.texts.form.register.email_error
        },
      }
    });
  },

  methods: {

    validate2(event) {
      if (!v.validate(event)) {
        this.disableSubmit = true;
      }
    },

    validate_x(field) {

      field = typeof field != 'string' ? null : field;
      console.log('validate()', field, field == null);

      if (field != null) {
        this.$refs.messages.clear(field); // leave other error messages as they are
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

    submit(event) {

      console.log('submit()', event);
      const isOk = v.validateSubmit(event);

      if (!isOk) {
        return;
      }

      const b = true;

      if (b) {
        console.log('temp EXIT');
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