import Vue from 'vue'
import VueRouter from 'vue-router'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import App from '@/App.vue'
import Start from '@/components/Start.vue'
import DrugInformations from '@/components/DrugInformations.vue'
import Login from '@/components/Login.vue'
import Register from '@/components/Register.vue'
import Infos from '@/components/Infos.vue'

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)

Vue.use(VueRouter)

Vue.config.productionTip = false;

// do use name (named routes)!!!
const routes = [
  { name: 'home', path: '', component: Start },
  { name: 'drugInformations', path: '/legal-infos', component: DrugInformations },
  { name: 'register', path: '/register', component: Register },
  { name: 'login', path: '/login', component: Login },
  { name: 'infos', path: '/infos', component: Infos }
];


const router = new VueRouter({
  // Check these, they remove # from the urls
  // https://stackoverflow.com/q/34623833/845117
  // https://stackoverflow.com/a/51340156/845117
  mode: 'history',
  routes // short for `routes: routes`
});


new Vue({
  router: router,
  render: h => h(App)
}).$mount('#app')



