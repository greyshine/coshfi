const axios = require('axios');
axios.defaults.baseURL = 'http://localhost:8080';

const main = function() {

    const main = {
        leafletjs: null
    };

    console.log('loaded ;-)');

    // area.sw, area.ne
    main.loadLocations = function(area) {

        console.log('loadLocations()...', area);

        main.leafletjs.reset();

        const args = {
            sw: area.sw,
            ne: area.ne
        };

        axios.post( '/api/v1/locations', args )
        then( (response)=>{
            console.log('OK', response);
        }, error=>{
            console.error('ERROR', error);
            main.popup('Error on req');
        } )
        .catch( exception=> {
            main.popup('FEHLER\n'+exception);
        } );

        const location = {
            lat: 52.1,
            lon: 4.1,
            type: 'cs',
            label: 'Eine gute Adresse'
        };

        leaflet.addLocation( location );
    };

    main.popup = function(messageHtml) {

        if ( messageHtml == null || typeof messageHtml == 'undefined' ) {
            // close the popup
            return;
        }

        console.log('TODO: show popup');
    };

    return main;
}();