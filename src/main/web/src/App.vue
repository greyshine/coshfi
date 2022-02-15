<template>

  <div id="app">

    <Sidebar />

    <nav class="main-nav">
      <img src="@/assets/logo.png" alt="CoffeeShopFinder" class="logo" @click="reload"/>
      <div class="logo" @click="reload">CoffeeShopFinder</div>
      <div v-if="login!=null">{{ login }}</div>
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
import user from "@/assets/user";

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
    this.fetchInfo();
  },

  mounted() {

    this.$eventbus.$on('login', user => this.login = user.login);
    this.$eventbus.$on('logout', () => this.login = null);

    setInterval(() => {

      axios.get('/api/ping')
          .then(response => {

            //console.log('PING', user.token, response.data);

            if (response.data === false && user.token != null) {

              console.log('ping received logout command');

              user.logout();
              this.$eventbus.$emit('logout');
            }
          });
    }, 5 * 60 * 1000);
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
