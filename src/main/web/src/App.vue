<template>

  <div id="app">

    <Sidebar />

    <nav class="main-nav">
      <img src="@/assets/logo.png" alt="CoffeeShopFinder" class="logo" @click="reload"/>
      <div class="logo" @click="reload">CoffeeShopFinder</div>
      <Burger/>
    </nav>

    <hr/>

    <router-view/>

  </div>
</template>

<script>
import Vue from 'vue';
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
    token: null
  }),

  created() {
    this.$root.bus = new Vue({});
    this.fetchInfo();
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
            console.log('response', response);
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
    color: #A9D24E;
    padding-left: 10px;
    padding-top: 10px;
    font-size: x-large;
    cursor: pointer;
  }
}

</style>
