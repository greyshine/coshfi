<template>
  <div id="app">

    <nav class="main-nav">
      <div class="logo">CoffeeShopFinder</div>
      <Burger />
    </nav>

    <Sidebar />

    <hr/>

    <HelloWorld />

  </div>
</template>

<script>
import Burger from '@/components/Menu/Burger.vue';
import Sidebar from '@/components/Menu/Sidebar.vue';
import HelloWorld from '@/components/HelloWorld.vue'

export default {

  name: 'App',

  components: {
    Burger,
    Sidebar,
    HelloWorld
  }
}
</script>

<style>
html {
  height: 100%;
  overflow:hidden;
}

body {
  border: 0; margin: 0; padding: 0;
  font-family: 'Lato';
  height: 100%;
  background: #fdfdfe;
  /*background: linear-gradient(45deg, rgba(101,31,87,1) 0%, rgba(225,113,87,1) 48%, rgba(249,248,113,1) 100%);*/
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 10px;
}

.main-nav {
  display: flex;
}

div.logo {
  width: 98%;
}

</style>
