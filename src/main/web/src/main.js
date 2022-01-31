import Vue from 'vue'
import VueRouter from 'vue-router'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import App from '@/App.vue'
import Main from '@/components/Main.vue'
import DrugInformations from '@/components/DrugInformations.vue'
import Login from '@/components/forms/Login.vue'
import Register from '@/components/forms/Register.vue'
import Infos from '@/components/Infos.vue'
import Location from '@/components/Location.vue'

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)

Vue.use(VueRouter)

Vue.config.productionTip = false;

Vue.prototype.$eventbus = new Vue();

// do use name (named routes)!!!
const routes = [
  {name: 'home', path: '', component: Main},
  {name: 'drugInformations', path: '/legal-infos', component: DrugInformations},
  {name: 'register', path: '/register', component: Register},
  {name: 'login', path: '/login', component: Login},
  {name: 'infos', path: '/infos', component: Infos},
  {name: 'location', path: '/location/:locationId', component: Location}
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



