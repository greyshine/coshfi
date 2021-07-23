/**
 * Check https://leafletjs.com/
 * @type {{map: *}}
 */
const leaflet = function() {

    const leaflet = {
        mainjs: null,
        markers: []
    };

    const map = L.map('openstreetmap').setView([52.080256, 4.312392], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(map);

    const m = L.marker([52.080256, 4.312392])
        .bindPopup('Pionier van goede beslissingen.')
        .openPopup()
        .addTo(map);
    leaflet.markers.push(m);

    const redrawHandler = (event)=>{
        console.log('event', event);

        const bounds = map.getBounds();
        console.log('bounds', bounds );

        leaflet.mainjs.loadLocations( { sw: bounds.southWest, ne: bounds.northEast} )
    };

    // each zoom fires a moveend
    // leaflet.map.on( 'zoomend', redrawHandler) ;
    map.on( 'moveend', redrawHandler) ;
    map.on( 'viewreset', redrawHandler) ;
    map.on( 'load', redrawHandler) ;
    map.on( 'resize', redrawHandler) ;

    leaflet.reset = function() {
        console.log('clear all locations');
        leaflet.markers.forEach( m=>map.removeLayer(m) )
        leaflet.markers = [];
    };

    leaflet.addLocation = function(location) {

        console.log('adding location', location);
        const m = L.marker([location.lat, location.lon])
            .bindPopup(location.name)
            .openPopup()
            .addTo(map);

        leaflet.markers.push( m );
    };

    leaflet.map = map;
    return leaflet;
}();