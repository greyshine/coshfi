<template>
  <div class="sidebar">

    <div class="sidebar-backdrop"
         @click="closeSidebarPanel"
         v-if="isPanelOpen" />

    <transition name="slide">
      <div v-if="isPanelOpen" class="sidebar-panel">

        <div class="top">
          <img src="@/assets/logo.png" alt="CoffeeShopFinder" class="logo" />
          CoffeeShopFinder
        </div>

        <ul  @click.prevent="closeSidebarPanel">
          <li @click.prevent="closeSidebarPanel"><router-link :to="{name: 'home'}" @click.prevent="closeSidebarPanel">Home</router-link></li>
          <li @click.prevent="closeSidebarPanel"><router-link :to="{name: 'drugInformations'}" @click.prevent="closeSidebarPanel">Legal-Infos</router-link></li>
          <li @click.prevent="closeSidebarPanel"><router-link :to="{name: 'login'}">Login</router-link></li>
          <li @click.prevent="closeSidebarPanel"><router-link :to="{name: 'register'}">Register</router-link></li>
          <li @click.prevent="closeSidebarPanel"><router-link :to="{name: 'infos'}">Infos &amp; Imprint</router-link></li>
        </ul>

      </div>
    </transition>
  </div>
</template>

<script>
export default {

  data: () => ({
    isPanelOpen: false
  }),

  mounted() {
    this.$root.bus.$on( 'main-menu', ()=>
      this.isPanelOpen = true
     );
  },

  methods: {
    closeSidebarPanel() {
      this.isPanelOpen = false
    }
  }
}
</script>

<style lang="scss" scope>

div {

  color: #183100;

  .slide-enter-active,
  .slide-leave-active
  {
    transition: transform 0.2s ease;
  }

  .slide-enter,
  .slide-leave-to {
    transform: translateX(-100%);
    transition: all 150ms ease-in 0s
  }

  .sidebar-backdrop {
    background-color: rgba(0,0,0,.3);
    width: 100vw;
    height: 100vh;
    position: fixed;
    top: 0;
    left: 0;
    cursor: pointer;
    z-index: 9999;
  }

  .sidebar-panel {
    overflow-y: auto;
    background-color: #A9D24E;
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 9999;
    padding: 3rem 0px;
    width: 230px;
  }

  a {
    color: #183100;
    text-decoration: none;
  }

  a:hover {
    color: #183100;
    font-size: larger;
  }

  a:active {
    color: #183100;
    font-size: larger;
  }

  ul {
    list-style: none;
  }

  div.top {
    padding: 5px;
    margin-bottom: 10px;
    background-color: #183100;
    color: #A9D24E;
  }

}

</style>