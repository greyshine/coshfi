<template>
  <div>

    <div v-show="showId==='menu'" id="menu">
      <h1 v-html="lang.get('page.account.h1', this.login)"></h1>
      <hr/>
      <div class="alignRight">
        <b-button size="sm" v-html="lang.get('global.logout')"/>
      </div>
      <ul>
        <li>
          <b-link @click="show('profile')">Profil</b-link>
        </li>
        <li>
          <b-link @click="show('bookkeeping')">Abrechnungen</b-link>
        </li>
      </ul>

      <hr/>
      <div class="alignRight">
        <b-button size="sm">Quit Account</b-button>
      </div>
      <div class="small">version: {{ version }}</div>
      <div class="small">tkn: {{ token }}</div>
    </div>

    <div v-show="showId==='profile'" id="profile">

      <h1 v-html="lang.get('page.account.h1', this.login)"/>

      <hr/>

      <div>
        <b-dropdown :text="language" size="sm" @click="changeLanguage">
          <b-dropdown-item><span v-html="lang.get('global.lang.de')"/></b-dropdown-item>
          <b-dropdown-item><span v-html="lang.get('global.lang.en')"/></b-dropdown-item>
          <b-dropdown-item><span v-html="lang.get('global.lang.es')"/></b-dropdown-item>
          <b-dropdown-item><span v-html="lang.get('global.lang.it')"/></b-dropdown-item>
          <b-dropdown-item><span v-html="lang.get('global.lang.nl')"/></b-dropdown-item>
        </b-dropdown>
      </div>


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
import lang from '@/assets/js/lang/lang.js';

export default {

  data: () => ({

    lang: lang,

    login: null,
    showId: 'menu',

    language: '',

    token: null,
    version: '?'
  }),

  created() {

    this.$eventbus.$on('info', info => this.version = info.version);

    this.language = lang.global.lang.en;
  },

  mounted() {

    this.version = this.$info.version;

    console.log('this.$user', this.$user);

    this.login = this.$user.login;

    if (this.$user.token == null) {
      this.$router.push('/login');
      return;
    }

    this.token = this.$user.token
  },

  methods: {

    show(sectionId = 'menu') {

      switch (sectionId) {

        case 'profile':
          this.loadProfile(() => this.showId = sectionId);
          break;

        case 'menu':
        default:
          this.showId = sectionId;
          return;
      }
    },

    loadProfile(callback) {

      callback();
    },

    changeLanguage(event) {
      console.log('change lang: ', event, this.language);
      this.lang.set(this.language);
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