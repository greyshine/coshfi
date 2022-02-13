<template>

  <div id="app">

    <Sidebar />

    <nav class="main-nav">
      <img src="@/assets/logo.png" alt="CoffeeShopFinder" class="logo" @click="reload"/>
      <div class="logo" @click="reload">CoffeeShopFinder</div>
      <div v-if="login!=null">{{ login }}&nbsp;-&nbsp;</div>
      <Burger/>
    </nav>

    <hr/>

    <router-view/>

  </div>
</template>

<script>
import axios from 'axios';
import Burger from '@/components/Menu/Burger.vue';
import Sidebar from '@/components/Menu/Sidebar.vue';

export default {

  name: 'App',

  components: {
    Burger,
    Sidebar
  },

  data: ()=>({
    login: null
  }),

  created() {

    this.$eventbus.$on('login', (login, token) => {
      console.log('App login', login, token);
      this.login = login;
    });

    this.$eventbus.$on('logout', () => {
      //console.log('App logout', this.login);
      this.login = null;
    });

    this.login = this.$getLogin();
    console.log('login?', this.login);

    this.fetchInfo();
  },

  mounted() {

  },

  methods: {

    reload() {
      document.location.href = '/';
    },

    fetchInfo() {

      // fetch version
      axios.get('/info')
      .then(
          response => {
            console.log('server.info', response.data);
          },
          error => {
            console.log('error', error);
            console.log('response', error.response);
          }
      );
    }

  }
}
</script>

<style lang="scss">

#app {

  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: left;
  margin: 10px;

  nav.main-nav {
    display: flex;
  }

  img.logo {
    width: 50px;
    height: 50px;
    cursor: pointer;
  }

  div.logo {
    width: 98%;
    color: $colorFg;
    padding-left: 10px;
    padding-top: 10px;
    font-size: x-large;
    cursor: pointer;
  }
}

</style>
