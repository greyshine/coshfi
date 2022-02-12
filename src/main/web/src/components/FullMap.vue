<template>

  <div id="osmFull">

    <OpenStreetMap id="osmFull" ref="osmFull"/>

  </div>

</template>

<script>

import OpenStreetMap from "@/components/OpenStreetMap";
import axios from "axios";
import utils from "@/assets/utils";

export default {

  components: {OpenStreetMap},

  data: () => ({}),

  mounted() {

    console.log('mounted()');

    if (utils.isSet('$fullmapLatLonZoom')) {

      const location = utils.get('$fullmapLatLonZoom');
      this.$refs.osmFull.setCenter(location.lat, location.lon);
      utils.delete('$fullmapLatLonZoom');
    }

    this.$refs.osmFull.addEventHandler('moveend', event => this.drawIcons(event));

    this.drawIcons();

    console.log('mounted().')
  },

  methods: {

    getMap() {
      return this.$refs.osmFull.getMap();
    },

    iconClicked(event, location) {

      console.log('iconClicked()', event, location);

      this.lastLatlon = this.$refs.osmFull.getBounds();

      console.log('router', this.$router);
      this.$router.push({name: 'location', params: {locationId: location.id}});

      console.log('iconClicked().');
    },

    drawIcons(event) {

      console.log('drawIcons()', event);

      const latLngBounds = this.$refs.osmFull.getBounds();

      const params = {
        nw: latLngBounds.nw.lat + ',' + latLngBounds.nw.lon,
        se: latLngBounds.se.lat + ',' + latLngBounds.se.lon,
        z: this.$refs.osmFull.getMap().getZoom()
      };

      axios.get('/api/v1/locations', {params: params})
          .then(
              response => {

                this.$refs.osmFull.deleteLocations();

                //console.log('fullmap-response.data.data', response.data.data.length, params, response.data.data, response);

                response.data.data.forEach(location => {

                  const html = '<div>' + location.name + '</div><hr/>'
                      + '<div>' + location.address.replace('\n', '<br/>') + '</div>'
                      + '<div style="margin-top: 10px;">(' + location.lat + ',' + location.lon + ')</div>';

                  const that = this;

                  this.$refs.osmFull.addLocation(location.lat, location.lon, 'colored', html,
                      (event) => that.iconClicked(event, location));
                });
              },
              error => {
                console.error(error);
              }
          );

      console.log('drawIcons().');
    },

    moveTo(lat, lon) {

      utils.assertNumber(lat, 'lat must be a number');
      utils.assertNumber(lon, 'lon must be a number');

      console.log('moveto', lat, lon);
      this.$refs.osmFull.moveTo(lat, lon);
    }
  }
}

</script>

<style lang="scss" scoped>

div.leaflet-popup-text {

  cursor: pointer;
  padding: 5px;

  .latlon {
    margin-top: 20px;
  }

}
</style>