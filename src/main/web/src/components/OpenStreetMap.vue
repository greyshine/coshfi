<template>

  <div id="mapContainer"></div>

</template>

<script>
import "leaflet/dist/leaflet.css";
import L from "leaflet";

export default {

  name: 'openstreetmap',

  data: ()=>({
    defaultLatLon: [50.853392, 5.694722],
    mapDiv: null
  }),

  mounted() {
    this.setupLeafletMap();
  },

  methods: {

    setupLeafletMap () {

      // TODO check api.mapbox.com if it has better nicer layers as proposed on
      //  https://blog.logrocket.com/building-an-interactive-map-with-vue-and-leaflet/...
      this.mapDiv = L.map('mapContainer').setView(this.defaultLatLon, 20);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(this.mapDiv);

    },
  }
}
</script>

<style scoped lang="scss">

#mapContainer {

  align: center;

  width: 97vw;
  /*height: 100vh;*/
  height: 70vh;
  align: center;
}

</style>