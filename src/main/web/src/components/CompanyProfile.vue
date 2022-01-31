<template>

  <div>

    <h1>Register</h1>
    <hr/>

    <div :style="{display: message != null ? 'inline' : 'none'}" class="error">
      {{ message }}
    </div>

    <div>

      <p class="formFieldDescription">
        Enter your legal (company) name.
      </p>
      <b-form-input v-model="name" placeholder="Name / legal name"></b-form-input>

      <p class="formFieldDescription">
        Enter a login name you want to user in future. You will be able to create one or several stores for your name:
      </p>
      <b-form-input v-model="login" class="error" placeholder="Enter your login" @blur="validate"></b-form-input>

      <p class="formFieldDescription">
        Enter a password at least 6 chars. Have an lower- and uppercase letter, a number and at least 1 special char in
        there (i.e. !&quot;§$%&/\()=?-_#+*&lt;&gt;,;.:).<br/>
        Exactly repeat that password in the second field.
      </p>
      <b-form-input v-model="password" placeholder="Enter your password" type="password"></b-form-input>
      <b-form-input v-model="password2" placeholder="Repeat password" type="password"></b-form-input>

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
      <b-form-input v-model="email" class="spaceTop" placeholder="Enter your email" type="email"></b-form-input>
    </div>

    <div>
      <b-button class="spaceTop" @click="submit">Change</b-button>
    </div>

  </div>

</template>

<script>

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