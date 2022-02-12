<template>
  <div id="profile">
    <h1 v-html="lang.get('page.profile.h1')"/>
    <hr/>
    Bla bla
  </div>
</template>

<script>
import lang from '@/assets/lang.js';

export default {

  data: () => ({
    lang: lang,
  }),

  mounted() {

    console.log('WORKS', lang.page.login.h1);
  },

  methods: {}
}

</script>

<style lang="scss" scoped>

div#profile {

  h1 {
  }

}

</style>