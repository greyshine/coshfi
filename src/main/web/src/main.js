import Vue from 'vue'
import VueRouter from 'vue-router'
import {BootstrapVue, IconsPlugin} from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import axios from 'axios';

import App from '@/App.vue'
import Main from '@/components/Main.vue'
import DrugInformations from '@/components/DrugInformations.vue'
import Login from '@/components/forms/Login.vue'
import Register from '@/components/forms/Register.vue'
import Infos from '@/components/Infos.vue'
import Location from '@/components/Location.vue'
import Profile from '@/components/Profile.vue'

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)

Vue.use(VueRouter)

Vue.config.productionTip = false;

// WARN: Vue.prototype.
//console.log('Vue.prototype', Vue.prototype);

Vue.prototype.$eventbus = new Vue([]);
//Vue.prototype.$login = null;
//Vue.prototype.$token = null;

Vue.prototype.$getLogin = function () {
  return Vue.prototype.$login;
};

const getLsLogin = function () {

  let loginStorage = localStorage.getItem('login');
  loginStorage = loginStorage == null ? {} : JSON.parse(loginStorage);
  //console.log('getLsStorage', loginStorage);
  return loginStorage;
};

const setLsLogin = function (loginTokenJson) {
  loginTokenJson = typeof loginTokenJson == 'object' ? loginTokenJson : {};
  loginTokenJson = JSON.stringify(loginTokenJson, null, 2);
  //console.log('setLsStorage', loginTokenJson);
  localStorage.setItem('login', loginTokenJson);
  return loginTokenJson;
};

Vue.prototype.$getLogin = function () {
  //Vue.prototype.$login = login == null || login === '' ? null : ''+login;
  return getLsLogin().login;
};

Vue.prototype.$setLogin = function (login) {
  //Vue.prototype.$login = login == null || login === '' ? null : ''+login;
  throw new Error('not yet implemented. login=' + login);
};

Vue.prototype.$getToken = function () {
  //return Vue.prototype.$token;
  return getLsLogin().token;
};

Vue.prototype.$setToken = function (token) {
  throw new Error('not yet implemented. token=' + token);
  //console.log('$setToken', token);
  //Vue.prototype.$token = token == null || token === '' ? null : ''+token;
};

Vue.prototype.$login = function (login, token) {
  const lsLogin = getLsLogin();
  lsLogin.login = login;
  lsLogin.token = token;
  lsLogin.date = new Date();
  setLsLogin(lsLogin);
  Vue.prototype.$eventbus.$emit('login', login, token);
}

Vue.prototype.$logout = function () {
  const lsLogin = getLsLogin();
  lsLogin.login = null;
  lsLogin.token = null;
  lsLogin.date = new Date();
  setLsLogin(lsLogin);
  Vue.prototype.$eventbus.$emit('logout');
}

Vue.prototype.$isLoggedIn = function () {
  //return Vue.prototype.$token != null;
  return getLsLogin().login != null;
};

Vue.prototype.$test = 'test-value';

// do use name (named routes)!!!
const routes = [
  {name: 'home', path: '', component: Main},
  {name: 'drugInformations', path: '/legal-infos', component: DrugInformations},
  {name: 'register', path: '/register', component: Register},
  {name: 'login', path: '/login', component: Login},
  {name: 'infos', path: '/infos', component: Infos},
  {name: 'location', path: '/location/:locationId', component: Location},
  {name: 'profile', path: '/profile', component: Profile}
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


setInterval(() => {
  axios.get('/api/ping')
      .then(response => {
        //console.log('ping > ', typeof response.data, response.data);
        if (response.data === false) {
          Vue.prototype.$logout();
        }

      });
}, 10000);

axios.interceptors.request.use(function (config) {

  // config.headers.Authorization = 'AUTH_TOKEN';
  const token = Vue.prototype.$getToken();

  if (token != null) {
    config.headers.TOKEN = token;
  }

  return config;
});




