<template>

  <div>

    <b-modal id="modalEmailChange"
             :title="lang.page.account.modal.emailchange.title"
             button-size="sm"
             hide-header-close
             size="lg"
             @ok="modalEmailOk">
      <div>Do you really want to change your email to</div>
      <div>{{ this.email }}</div>
      <div>Once changed and you logout. A new confirmation is send to the address for login again.</div>
    </b-modal>

    <div v-show="showId==='menu'" id="menu">
      <h1 v-html="lang.get('page.account.h1', this.login)"></h1>
      <hr/>
      <ul>
        <li>
          <b-link @click="show('profile')">Profil</b-link>
        </li>
        <li>
          <b-link @click="show('bookkeeping')">Abrechnungen</b-link>
        </li>
      </ul>

    </div>

    <div v-show="showId==='profile'" id="profile">

      <h1 v-html="lang.get('page.account.h1', this.login)"/>

      <hr/>

      <b-form-group :description="lang.page.account.name_description"
                    :label="lang.page.account.name"
                    label-for="name">
        <div>
          <b-input id="name" v-model="name" :placeholder="lang.page.account.name_placeholder" trim
                   @blur="blurField($event, 'name')"/>
        </div>
      </b-form-group>

      <b-form-group :description="lang.page.account.contactPerson_description"
                    :label="lang.page.account.contactPerson"
                    label-for="contactPerson">
        <div>
          <b-input id="contactPerson" v-model="contactPerson" :placeholder="lang.page.account.contactPerson_placeholder"
                   trim @blur="blurField($event, 'contactPerson')"/>
        </div>
      </b-form-group>

      <b-form-group :description="lang.page.account.email_description"
                    :label="lang.page.account.email"
                    label-for="email">
        <div>
          <b-input id="email" v-model="email" :placeholder="lang.page.account.email_placeholder" trim
                   @blur="blurField($event, 'email')"></b-input>
        </div>
      </b-form-group>

      <b-form-group :description="lang.page.account.phone_description"
                    :label="lang.page.account.phone"
                    label-for="phone">
        <div>
          <b-form-input id="phone" v-model="phone" :placeholder="lang.page.account.phone_placeholder" trim type="tel"
                        @blur="blurField($event, 'phone')"></b-form-input>
        </div>
      </b-form-group>

      <b-form-group :description="lang.page.account.address_description"
                    :label="lang.page.account.address"
                    label-for="address">
        <div>
          <b-input v-for="(al, index) in addressLines" id="address" :key="index" v-model="addressLines[index]"
                   :placeholder="'address line '+(index+1)" trim @blur="blurField($event, 'address')"></b-input>
          <b-button size="sm" @click="addressLines.push('')">Add line</b-button>
        </div>
      </b-form-group>

      <b-form-group :description="lang.page.account.language_description"
                    :label="lang.page.account.language"
                    label-for="language">
        <div>
          <b-form-select id="language" v-model="language" @change="changeLanguage">
            <b-form-select-option v-for="lng in lang.getLanguages()" :key="lng" :selected="lng==language" :value="lng">
              <span v-html="lang.get('global.lang.'+lng)"/>
            </b-form-select-option>
          </b-form-select>
        </div>
      </b-form-group>

      <div class="alignRight">
        <b-button size="sm">Quit Account</b-button>
      </div>

      <hr/>
      <b-link @click="show('menu')" v-html="lang.global.back"></b-link>

      <div class="small">version: {{ version }}</div>
      <div class="small">tkn: {{ token }}</div>
    </div>

    <div v-show="showId==='bookkeeping'" id="bookkeeping">
      Bookkeeping
      <hr/>
      <b-link @click="show('menu')"></b-link>
    </div>

  </div>
</template>

<script>
import utils from '@/assets/js/utils.js';
import lang from '@/assets/js/lang/lang.js';
import axios from "axios";
import user from "@/assets/js/user.js";

export default {

  data: () => ({

    lang: lang,

    login: null,
    password: null,
    showId: 'menu',

    language: null,

    data: null,

    name: null,
    contactPerson: null,
    addressLines: [''],
    email: null,
    phone: null,

    token: null,
    version: '?'
  }),

  created() {

    this.$eventbus.$on('info', info => this.version = info.version);

    this.language = lang.getLanguage();

  },

  mounted() {

    this.version = this.$info.version;

    console.log('this.$user', this.$user);

    this.login = this.$user.login;

    if (this.$user.token == null) {
      this.$router.push('/login');
      return;
    }

    this.token = this.$user.token;

    this.loadProfile(() => {
    });

    const showSectionId = localStorage.getItem('account.show.section');
    if (showSectionId != null) {
      this.show(showSectionId);
      localStorage.removeItem('account.show.section');
    }

  },

  methods: {

    show(sectionId = 'menu') {

      switch (sectionId) {

        case 'profile':
          this.loadProfile(() => this.showId = sectionId);
          break;

        case 'menu':
        default:
          this.showId = sectionId;
          return;
      }
    },

    loadProfile(callback) {

      console.log('loadProfile(); ', 'token=' + this.token);

      axios.get('/api/user/' + this.$user.login, {headers: {TOKEN: this.token}})
          .then(response => {

            console.log(response.data);

            this.data = response.data;

            this.name = response.data.name;
            this.contactPerson = response.data.contactPerson;
            this.email = response.data.email;
            this.phone = response.data.phone;

            this.addressLines = [];
            response.data.address.forEach(addressLine => this.addressLines.push(addressLine));
            this.addressLines = this.addressLines.length > 0 ? this.addressLines : this.addressLines = [''];
            this.language = response.data.language;

            if (typeof callback != 'undefined') {
              callback();
            }

          }, error => {
            console.error(error);
          })
          .catch(exception => {

            console.error(exception);
          });


    },

    changeLanguage(event) {
      console.log('change lang: ', event, this.language);
      this.lang.set(this.language);

      localStorage.setItem('account.show.section', 'profile');
      window.location.reload(false);
    },

    modalEmailOk(event) {
      console.log('WORKS', event);

      const body = {
        field: 'email',
        email: this.email
      };

      this.postDataChange(body, () => this.email = this.data.email);
    },

    blurField(event, field) {

      console.log('blur', event.target.id, event.target);

      let x = 0;

      const body = {
        field: field
      };

      let functionOnError = null;

      switch (body.field) {

        case 'name':

          body.name = this.name;
          functionOnError = () => this.name = this.data.name;

          break;

        case 'contactPerson':
          body.contactPerson = this.contactPerson;
          functionOnError = () => this.name = this.data.contactPerson;
          break;

        case 'address':

          utils.trimStringArray(this.addressLines);
          if (this.addressLines.length == 0 || (this.addressLines.length == 1 && this.addressLines[0].trim() == '')) {
            this.addressLines = [''];
            return;
          }

          body.address = this.addressLines;
          functionOnError = () => this.addressLines = this.data.address;
          break;

        case 'email':

          x += (4 + 1) * 2;

          if (x == 10 || this.data.email != this.email) {
            this.$bvModal.show('modalEmailChange')
          }

          return;

        case 'phone':
          body.phone = this.phone;
          functionOnError = () => this.phone = this.data.phone;
          break;

        default:
          throw new Error('Unknown field: ' + field)
      }

      this.postDataChange(body, functionOnError);
    },

    postDataChange(body, functionOnError) {

      axios.post('/api/user', body)
          .then(response => {
                console.log(response)
              },
              error => {

                console.error(error);

                if (typeof functionOnError == 'function') {
                  functionOnError();
                }

                if (error.response.status == 403) {

                  console.log('logout on 403');
                  user.logout();
                  this.$eventbus.$emit('logout');
                  // will throw an exception if we are already on page /
                  this.$router.push('/');
                  return;
                }
              })
          .catch(exception => {

                if (typeof functionOnError == 'function') {
                  functionOnError();
                }
                console.error(exception);
              }
          );
    }


  }
}

</script>

<style lang="scss" scoped>

div#menu {

  h1 {
  }


}

</style>