<template>

  <div v-bind:id="id" ref="map" class="mapContainer"/>

</template>

<script>

import "leaflet/dist/leaflet.css";
import L from "leaflet";
import utils from "@/assets/utils";

export default {

  name: 'openstreetmap',

  props: {
    id: String
  },

  data: () => ({
    defaultLatLon: [50.975715, 11.033536],
    // Niederlande
    //defaultLatLon: [50.853392, 5.694722],
    mapDiv: null,
    layerGroup: null,

    flags: {
      colored: require('../assets/images/osm/flagicon-1.png'),
      black: require('../assets/images/osm/flagicon-t.png'),
      green: require('../assets/images/osm/flagicon-gt.png')
    }
  }),

  mounted() {

    this.setupLeafletMap();

    /*
    const keyPrefix = 'openstreetmap.'+ this.id;
    const keyRedrawMap = keyPrefix +'.redraw';
    this.$eventbus.$on(keyRedrawMap, ()=>this.redraw());
    console.log( keyRedrawMap +', mounted().');
    */
  },

  methods: {

    getMap() {
      return this.mapDiv;
    },

    /**
     *
     */
    getBounds() {

      const latLngBounds = this.mapDiv.getBounds();

      const ne = latLngBounds.getNorthEast();
      const sw = latLngBounds.getSouthWest();

      return {
        nw: {
          lat: ne.lat,
          lon: sw.lng
        },
        ne: {
          lat: () => {
            throw new Error('unsupported - to be implemented')
          },
          lon: () => {
            throw new Error('unsupported - to be implemented')
          }
        },
        sw: {
          lat: () => {
            throw new Error('unsupported - to be implemented')
          },
          lon: () => {
            throw new Error('unsupported - to be implemented')
          }
        },
        se: {
          lat: sw.lat,
          lon: ne.lng
        },
        zoom: this.mapDiv.getZoom()
      };
    },

    deleteLocations() {
      this.mapDiv.removeLayer(this.layerGroup);
      this.layerGroup = L.layerGroup([]).addTo(this.mapDiv);
    },

    addLocation(lat, lon, iconReference = null, contentHtml = null, clickHandler = null) {

      //console.log('addLocation()', lat, lon, iconReference, contentHtml, clickHandler);

      iconReference = !this.flags[iconReference] ? this.flags.colored : this.flags[iconReference];

      const markerOptions = {
        icon: new utils.AbstractWeedIcon({iconUrl: iconReference})
      };
      const marker = L.marker([lat, lon], markerOptions);
      let clickTarget = null;

      if (contentHtml != null) {

        const popupContent = L.DomUtil.create('div', 'leaflet-popup-text');
        popupContent.style.padding = '10px';
        popupContent.innerHTML = contentHtml;

        const popup = L.popup().setContent(popupContent);

        if (typeof clickHandler == 'function') {

          //console.log('clickHandler', clickHandler);
          popupContent.style.cursor = 'pointer';
          clickTarget = popupContent;
        }

        marker.bindPopup(popup);

      } else if (typeof clickHandler == 'function') {

        clickTarget = marker;
      }

      if (clickTarget != null) {
        L.DomEvent.addListener(clickTarget, 'click', event => clickHandler(event, this.mapDiv));
      }

      this.layerGroup.addLayer(marker);
      //console.log('addLayer', this.layerGroup, marker);

      //console.log('addLocation().');
      return marker;
    },

    getCenter() {

      const centerLatLng = this.mapDiv.getBounds().getCenter();

      console.log(centerLatLng);

      return {
        lat: centerLatLng.lat,
        lon: centerLatLng.lng,
        zoom: this.mapDiv.getZoom()
      }
    },

    setCenter(lat, lon, zoom = null) {

      if (isNaN(lat) || isNaN(lon)) {
        throw new Error('parameters lat and lon must be numbers');
      }
      // todo check numeric bounds for lat/lon

      this.mapDiv.fitBounds([
        [lat, lon]
      ]);

      this.mapDiv.setZoom(typeof zoom == 'number' ? zoom : this.mapDiv.getZoom());
    },

    moveTo(lat, lon, zoom = null) {

      if (isNaN(lat) || isNaN(lon)) {
        throw new Error('parameters lat and lon must be numbers');
      }

      this.mapDiv.fitBounds([
        [lat, lon]
      ]);

      this.mapDiv.setZoom(typeof zoom == 'number' ? zoom : this.mapDiv.getZoom());
    },

    getZoom() {
      return this.mapDiv.getZoom();
    },

    setupLeafletMap() {

      // TODO check api.mapbox.com if it has better nicer layers as proposed on
      //  https://blog.logrocket.com/building-an-interactive-map-with-vue-and-leaflet/...

      //console.log('setupLeafletMap()');
      //console.log('this.id', this.id);

      this.mapDiv = L.map(this.id);
      this.layerGroup = L.layerGroup([]).addTo(this.mapDiv);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(this.mapDiv);

      //console.log('this.mapDiv', this.mapDiv);
      this.mapDiv.setView(this.defaultLatLon, 20);

      //this.mapDiv.on('click', event => this.$emit('event', event, this.mapDiv, this.id));
      //this.mapDiv.on('dblclick', event => this.$emit('event', event, this.mapDiv, this.id));
      //this.mapDiv.on('moveend', event => this.$emit('event', event, this.mapDiv, this.id));
      //this.mapDiv.on('resize' , event=> this.$emit('event', event, this.mapDiv, this.id));

      //console.log('this.locations', this.locations);
      //console.log('this.id', this.id);
      //console.log('setupLeafletMap().');
    },

    redraw(redrawFct) {

      //console.log("redraw()");

      // Needs to have it this way, otherwise the map is not fully drawn
      // taken from https://stackoverflow.com/a/65771815/845117
      setTimeout(() => this.mapDiv.invalidateSize(), 0);

      if (typeof redrawFct == 'function') {
        redrawFct();
      }
      // console.log("redraw().");
    },

    /**
     * @param eventname one of click, dblclick, moveend, resize, ... (https://leafletjs.com/reference.html#event-objects)
     * @param handler
     */
    addEventHandler(eventname, handler) {

      utils.assertString(eventname, "No proper key for eventname: ", eventname);
      utils.assertFunction(handler, "No proper function for handler: ", handler);

      this.mapDiv.on(eventname, event => handler(event));
    },
  }
}
</script>

<style lang="scss" scoped>

div.mapContainer {

  align: center;

  width: 100%;
  height: 75vh;

  border-radius: 5px;
}

</style>