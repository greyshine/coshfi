<template>
  <div class="sidebar">

    <div class="sidebar-backdrop"
         @click="closeSidebarPanel"
         v-if="isPanelOpen" />

    <transition name="slide">

      <div v-if="isPanelOpen" class="sidebar-panel">

        <div class="top">
          <img src="@/assets/logo.png" alt="CoffeeShopFinder" class="logo"/>
          CoffeeShopFinder
        </div>

        <ul class="sidebar">
          <li @click.prevent="closeSidebarPanel">
            <router-link :to="{name: 'home'}">Home</router-link>
          </li>
          <li v-if="!login" @click.prevent="closeSidebarPanel">
            <hr/>
            <router-link :to="{name: 'login'}">Login</router-link>
          </li>
          <li v-if="!login" @click.prevent="closeSidebarPanel">
            <router-link :to="{name: 'register'}">Register</router-link>
            <hr/>
          </li>
          <li @click.prevent="closeSidebarPanel">
            <router-link :to="{name: 'drugInformations'}">Legal-Infos</router-link>
          </li>
          <li @click.prevent="closeSidebarPanel">
            <router-link :to="{name: 'infos'}">Infos &amp; Imprint</router-link>
          </li>
          <li v-if="login" @click.prevent="closeSidebarPanel">
            <hr/>
            <router-link :to="{name: 'account'}">Account</router-link>
          </li>
          <li v-if="login" @click.prevent="closeSidebarPanel($event, 'logout')">
            Logout
          </li>
        </ul>

      </div>
    </transition>
  </div>
</template>

<script>
// import utils from "@/assets/utils";
import user from "@/assets/js/user";

export default {

  data: () => ({
    isPanelOpen: false,
    login: false
  }),

  created() {
    this.$eventbus.$on('main-menu', () => this.isPanelOpen = true);
    this.$eventbus.$on('login', () => this.login = true);
    this.$eventbus.$on('logout', () => this.login = false);
  },

  mounted() {

    this.login = this.$user.token != null;

  },

  methods: {

    closeSidebarPanel(target, hint) {

      console.log('closeSidebarPanel()', target, hint);

      switch (hint) {

        case 'logout':

          user.logout();
          this.$eventbus.$emit('logout');
          // will throw an exception if we are already on page /
          this.$router.push('/');
          break;
      }

      this.isPanelOpen = false;
    }
  }
}
</script>

<style lang="scss" scope>

div.sidebar {

  color: $colorBg;

  .slide-enter-active,
  .slide-leave-active {
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
    background-color: $colorFg;
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 9999;
    padding: 3rem 0px;
    width: 230px;
  }

  a {
    color: $colorBg;
    text-decoration: none;
  }

  a:hover {
    color: $colorBg;
    font-weight: bold;
  }

  a:active {
    color: $colorBg;
    font-size: larger;
  }

  ul {
    list-style: none;
  }

  li {
    color: $colorBg;
    cursor: pointer;
  }

  div.top {
    padding: 5px;
    margin-bottom: 10px;
    background-color: #183100;
    color: #A9D24E;
  }

}

</style>