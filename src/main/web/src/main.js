import Vue from 'vue'
import VueRouter from 'vue-router'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import axios from "axios";
import user from '@/assets/js/user';

import App from '@/App.vue'
import Main from '@/components/Main.vue'
import DrugInformations from '@/components/DrugInformations.vue'
import Login from '@/components/forms/Login.vue'
import Register from '@/components/forms/Register.vue'
import Infos from '@/components/Infos.vue'
import Location from '@/components/Location.vue'
import Account from '@/components/forms/Account';

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)

Vue.use(VueRouter)

Vue.config.productionTip = false;

Vue.prototype.$info = {};

Vue.prototype.$eventbus = new Vue([]);

Vue.prototype.$user = user;

// do use name (named routes)!!!
const routes = [
  {name: 'home', path: '', component: Main},
  {name: 'drugInformations', path: '/legal-infos', component: DrugInformations},
  {name: 'register', path: '/register', component: Register},
  {name: 'login', path: '/login', component: Login},
  {name: 'infos', path: '/infos', component: Infos},
  {name: 'location', path: '/location/:locationId', component: Location},
  {name: 'account', path: '/account', component: Account}
];

const router = new VueRouter({
  // Check this, it removes # from the urls
  // https://stackoverflow.com/q/34623833/845117
  mode: 'history',
  routes // short for `routes: routes`
});

//Vue.prototype.$router = router;

new Vue({
  router: router,
  render: h => h(App)
}).$mount('#app')

axios.interceptors.request.use(function (config) {

  config.headers.TOKEN = user.token == null ? '' : user.token;
  //console.log('config.headers.TOKEN', typeof config.headers.TOKEN, config.headers.TOKEN);

  return config;
});




