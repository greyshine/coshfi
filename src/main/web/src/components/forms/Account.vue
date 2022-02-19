<template>
  <div>

    <div v-show="showId==='menu'" id="menu">
      <h1 v-html="lang.get('page.account.h1')"></h1>
      <hr/>
      <b-button size="sm">Logout</b-button>
      <ul class="hidden">
        <li>
          <b-link @click="show('profile')">Profil</b-link>
        </li>
        <li>
          <b-link @click="show('bookkeeping')">Abrechnungen</b-link>
        </li>
      </ul>

      <hr/>
      <b-button size="sm">Quit Account</b-button>
      <div class="small">version: {{ version }}</div>
      <div class="small">tkn: {{ token }}</div>
    </div>

    <div v-show="showId==='profile'" id="profile">

      <h1>PROFILE</h1>
      Language dropdown


      <hr/>
      <b-link @click="show('menu')">zurück</b-link>
    </div>

    <div v-show="showId==='bookkeeping'" id="bookkeeping">
      Bookkeeping
      <hr/>
      <b-link @click="show('menu')">zurück</b-link>
    </div>

  </div>
</template>

<script>
import lang from '@/assets/lang.js';

export default {

  data: () => ({
    lang: lang,

    showId: 'menu',

    token: null,
    version: '?'
  }),

  created() {
    this.$eventbus.$on('info', info => this.version = info.version);
  },

  mounted() {

    this.version = this.$info.version;

    console.log('this.$user', this.$user);

    if (this.$user.token == null) {
      this.$router.push('/login');
      return;
    }

    this.token = this.$user.token
  },

  methods: {

    show(sectionId = 'menu') {

      if (sectionId === 'profile') {
        this.loadProfile(() => this.showId = sectionId);
      }
    },

    loadProfile(callback) {

      callback();
    }

  }
}

</script>

<style lang="scss" scoped>

div#menu {

  h1 {
  }

}

</style>