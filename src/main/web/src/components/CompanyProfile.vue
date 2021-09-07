<template>
  <h1>Register</h1>
  <hr/>

  <div class="error" :style="{display: message != null ? 'inline' : 'none'}">
    {{message}}
  </div>

  <div>

    <p class="formFieldDescription">
      Enter your legal (company) name.
    </p>
    <b-form-input v-model="name" placeholder="Name / legal name"></b-form-input>

    <p class="formFieldDescription">
      Enter a login name you want to user in future. You will be able to create one or several stores for your name:
    </p>
    <b-form-input class="error" v-model="login" @blur="validate" placeholder="Enter your login"></b-form-input>

    <p class="formFieldDescription">
      Enter a password at least 6 chars. Have an lower- and uppercase letter, a number and at least 1 special char in there (i.e. !&quot;§$%&/\()=?-_#+*&lt;&gt;,;.:).<br/>
      Exactly repeat that password in the second field.
    </p>
    <b-form-input type="password" v-model="password" placeholder="Enter your password"></b-form-input>
    <b-form-input type="password" v-model="password2" placeholder="Repeat password"></b-form-input>

    <p class="formFieldDescription">
      Enter your legal (company) address.
    </p>
    <b-form-input v-model="name" placeholder="Name / legal name"></b-form-input>

    <p class="formFieldDescription">
      Enter your legal (company) name and address.
    </p>
    <b-form-input v-model="name" placeholder="Name / legal name"></b-form-input>
    <b-form-input v-model="additionalLine" placeholder="additional line"></b-form-input>
    <b-form-input v-model="streetno" placeholder="Street + Number / Postbox"></b-form-input>
    <b-form-input v-model="zipcity" placeholder="Zip + City"></b-form-input>
    <b-form-input v-model="country" placeholder="country"></b-form-input>

    <p class="formFieldDescription">
      Enter your email address.
    </p>
    <b-form-input type="email" v-model="email" placeholder="Enter your email" class="spaceTop"></b-form-input>
  </div>

  <div>
    <b-button @click="submit" class="spaceTop">Change</b-button>
  </div>
</template>

<script>

import utils from "@/assets/utils";

export default {

  data: ()=>({
    additionalLine: null,
    streetno: null,
    zipcity: null,
    country: null,
  }),

  mount() {},

  methods: {}
}

</script>

<style scoped lang="scss">

</style>