<template>

  <div>

    <h1>Register</h1>
    <hr/>

    <div class="spaceBottomLeft error" :style="{display: message != null ? 'inline' : 'none'}">
      {{message}}
    </div>

    <div>
      <b-form-input v-model="login" @blur="validate" placeholder="Enter your login"></b-form-input>

      <b-form-input type="password" v-model="password" placeholder="Enter your password" class="spaceTop"></b-form-input>
      <b-form-input type="password" v-model="password2" placeholder="Repeat password"></b-form-input>

      <b-form-input v-model="name" placeholder="Name / legal name" class="spaceTop"></b-form-input>
      <b-form-input v-model="additionalLine" placeholder="additional line"></b-form-input>
      <b-form-input v-model="streetno" placeholder="Street + Number / Postbox"></b-form-input>
      <b-form-input v-model="zipcity" placeholder="Zip + City"></b-form-input>
      <b-form-input v-model="country" placeholder="country"></b-form-input>

      <b-form-input type="email" v-model="email" placeholder="Enter your email" class="spaceTop"></b-form-input>
    </div>

    <div>
      <p class="spaceTopLeft">
        We will send you an email with a confirmation code. Click the link or paste the url in order to confirm your
        registration.
      </p>
    </div>

    <div>
      <b-button @click="submit" @mouseover="validate" :disabled="disableSubmit" class="spaceTop">Register</b-button>
    </div>
  </div>

</template>

<script>
import axios from 'axios'
import utils from '@/assets/utils.js'

export default {

  data: ()=>({
    message: null,

    login: null,

    password: null,
    password2: null,

    name: null,

    additionalLine: null,
    streetno: null,
    zipcity: null,
    country: null,

    email: null,

    disableSubmit: true
  }),

  mount() {
    this.validate();
  },

  methods: {

    validate() {

      let v = true;
      let message = '';

      this.login = utils.trimToNull(this.login);

      this.password = utils.trimToNull(this.password);
      this.password2 = utils.trimToNull(this.password2);

      this.name = utils.trimToNull(this.name);

      this.additionalLine  = utils.trimToNull(this.additionalLine);
      this.streetno = utils.trimToNull(this.streetno);

      this.zipcity = utils.trimToNull(this.zipcity);
      this.country = utils.trimToNull(this.country);

      this.email = utils.trimToNull(this.email);

      if (utils.isBlank(this.login)) {
        v = false;
        message += 'Login is blank\n';
      }

      if (utils.isBlank(this.name)) {
        v = false;
        message = 'Name is blank\n';
      }

      if (utils.isBlank(this.password)) {
        v = false;
        message = 'Bad password\n';
      }

      if (v == true && this.password !== this.password2) {
        v = false;
        message = 'Bad password repeat\n';
      }

      if (utils.isBlank(this.email)) {
        v = false;
        message = 'Bad email\n';
      }

      //this.disableSubmit = !v;
      this.disableSubmit = false;
      this.message = utils.trimToNull( message );

      return v;
    },

    submit() {

      if ( !this.validate() ) {
        return;
      }

      const requestBody = {
        name: utils.trimToNull(this.name),
        email: utils.trimToNull(this.email),
        address: [],
        password: utils.trimToNull(this.password),
      };

      axios.post('/api/register', requestBody)
          .then(
              response => {
                console.log('ok', response);
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

div {

  .error {
    color: red;
  }

}

</style>