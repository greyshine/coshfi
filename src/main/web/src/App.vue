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
import user from "@/assets/js/user";

export default {

  name: 'App',

  components: {
    Burger,
    Sidebar
  },

  data: ()=>({
    intervalTime: 2 * 60 * 1_000,
    login: null
  }),

  created() {

    console.log('usr', this.$user);

    this.$eventbus.$on('login', user => this.login = user.login);
    this.$eventbus.$on('logout', () => {
      this.login = null;
      this.$router.push('/login');
    });

    this.login = this.$user.login;
  },

  mounted() {

    axios.get('/info')
        .then(
            response => {
              this.$info = response.data;
              this.$eventbus.$emit('info', response.data);
              //console.log('server.info', response.data);
            },
            error => {
              console.log('error', error);
            }
        );

    setInterval(() => {

      axios.get('/api/ping')
          .then(response => {

            // console.log('PING', user.token, response.data);

            if (response.data === false && user.token != null) {

              console.log('ping received logout command');

              this.login = null;
              user.logout();

              this.$eventbus.$emit('logout');
            }
          });
    }, this.intervalTime);
  },

  methods: {

    reload() {
      document.location.href = '/';
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
