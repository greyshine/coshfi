<template>

  <b-container class="doubleCols">
    <b-row>
      <b-col>

        <OpenStreetMap id="osmHalf" ref="osmHalf"/>

      </b-col>
      <b-col>
        <div>{{ name }}</div>
        <hr/>
        <div v-html="address"/>
        <hr/>
        <div v-if="www != null">www: {{ www }}</div>
        <div v-if="email != null">email: {{ email }}</div>
        <div v-if="phone != null">phone: {{ phone }}</div>
        <div class="latlon small">{{ latlon }}, {{ id }}</div>
        <a @click="clickBack($event)">back to map</a>
      </b-col>
    </b-row>
  </b-container>

</template>

<script>
import OpenStreetMap from '@/components/OpenStreetMap';
import axios from 'axios';
import utils from '@/assets/js/utils';

export default {

  name: 'Location',

  components: {OpenStreetMap},

  data: () => ({
    id: null,
    name: null,
    address: null,
    www: null,
    email: null,
    phone: null,
    latlon: null
  }),

  mounted() {
    this.load(this.$route.params.locationId);
  },

  methods: {

    load(id) {

      console.log('load()', id);

      this.id = null;
      this.name = null;
      this.address = null;
      this.www = null;
      this.email = null;
      this.phone = null;
      this.latlon = null;

      // todo check history for UncaughtPromise Exception
      // remove catch-clause and/or onComplete parameter to see it
      this.$router.push({name: 'location', params: {locationId: id}}, () => {
      });
      //.catch((e)=>{console.log(e)});
      //.catch(()=>{});     0

      axios.get('/api/v1/location/' + id, {params: {}})
          .then(
              response => {

                //console.log('location-response.data', response.data, response);

                const location = response.data.location;

                this.id = id;
                this.name = location.name;
                this.address = location.address;
                this.latlon = location.lat + ',' + location.lon;

                if (this.address != null) {
                  this.address = this.address.replace('\n', '<br/>');
                }

                this.$refs.osmHalf.deleteLocations();
                this.$refs.osmHalf.addLocation(location.lat, location.lon);

                const that = this;
                response.data.neighbours.forEach(location => {

                  this.$refs.osmHalf.addLocation(location.lat,
                      location.lon,
                      'green',
                      null, // locationName
                      () => {
                        that.load(location.id);
                        console.log('loaded', location.id, location);
                      });
                });

                utils.set('$fullmapLatLonZoom', {'lat': location.lat, 'lon': location.lon});
                this.$refs.osmHalf.moveTo(location.lat, location.lon);
              },
              error => {
                console.error(error);
              }
          );
    },

    clickBack(event) {

      console.log("clickBack", event, this.latlon)
      this.$router.push({name: 'home', params: {latlon: this.latlon}})
    }

  }
}

</script>

<style lang="scss" scoped>

div.latlon {
  margin-top: 20px;
}

a:hover {
  color: black;
}

</style>