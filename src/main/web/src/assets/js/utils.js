import axios from "axios";
import L from "leaflet";

const doNothing = function () {
};

const keyValues = {};

export default {

    /**
     * Intended to do nothing.
     * Invented for validation not to skip compiling due to empty empty methods.
     */
    void() {
        console.log('void');
    },

    set(key, value = undefined) {

        this.assertString(key);

        if (typeof value != 'undefined') {

            const valueBefore = keyValues[key];

            keyValues[key] = value;

            return valueBefore;

        } else {
            this.delete(key);
        }
    },

    delete(key) {
        this.assertString(key);
        //https://stackoverflow.com/a/3455416/845117
        delete keyValues[key];
    },

    get(key) {
        this.assertString(key);
        return keyValues[key];
    },

    isSet(key) {
        this.assertString(key);
        return typeof keyValues[key] !== 'undefined';
    },

    test() {
        console.log('utils test');
    },

    isBlank(value) {
        return this.trimToNull(value) == null;
    },

    isNotBlank(value) {
        return !this.isBlank(value);
    },

    trimToNull(value) {

        if ( value == null|| typeof value != 'string')  {
            return value;
        }

        value = value.trim();

        return value.length === 0 ? null : value;
    },

    matches(regex, text) {

        if (regex == null || typeof regex != 'string') {
            throw new Error('Bad text or regex.');
        }

        return new RegExp(regex, 'g').test(text);
    },

    isEmail(email) {
        return typeof email == 'string' && /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(email);
    },

    validateRemote(fieldValueObject, handler) {

        console.assert(typeof fieldValueObject == 'object');

        //asserts.assertNotNull(fieldsValuesObject);
        handler = typeof handler == 'function' ? handler : () => console.log('no handler defined');

        //asserts.assertFunction(handler);
        axios.post('/api/validate', fieldValueObject)
            .then( response=>{
                    doNothing(response);
                },
                error=>{
                    if (typeof handler == 'function') {
                        handler(error);
                    } else {
                        console.error('no handler declared.', fieldValueObject)
                    }
                })
            .catch(exception => {
                    console.error(exception);
                }
            );
    },

    assertNotNull(value, message = null) {
        if (typeof value == 'undefined' || value == null) {
            throw new Error(message == null ? 'value is null.' : message);
        }
    },

    assertObject(value, message = null) {
        if (typeof value !== 'object') {
            throw new Error(message == null ? 'value is not an object: ' + value : message);
        }
    },

    assertString(value, message = null) {
        if (typeof value != 'string') {
            throw new Error(message == null ? 'value is not a string: ' + value : message);
        }
    },

    assertNumber(value, message = null) {
        if (typeof value != 'number') {
            throw new Error(message == null ? 'value is not a number: ' + value : message);
        }
    },

    assertFunction(value, message = null) {
        if (typeof value !== 'function') {
            throw new Error(message == null ? 'value is not a function: ' + value : message);
        }
    },

    assertArray(value, message = null) {
        if (!Array.isArray(value)) {
            throw new Error(message == null ? 'value is not an array: ' + value : message);
        }
    },

    trimStringArray(strings) {

        this.assertArray(strings, "given parameter is no array");

        let runLoop = true;

        while (runLoop) {

            runLoop = false;

            for (let i = 0; i < strings.length; i++) {
                if (typeof strings[i] == 'undefined' || strings[i] == null || strings[i].trim() === '') {
                    runLoop = true;
                    strings.splice(i, 1);
                    break;
                }
            }
        }
    },

    AbstractWeedIcon: L.Icon.extend({
        options: {
            //shadowUrl: require('../assets/leaf-shadow.png'),
            iconSize: [38, 45],
            shadowSize: [50, 64],
            iconAnchor: [20, 20],
            shadowAnchor: [0, 0],
            //popupAnchor:  [-3, -76]
            popupAnchor: [0, 0]
        }
    }),

    executeSafe(f) {

        if (typeof f != 'function') {
            console.warn('given parameter was not a function. doing nothing')
            return;
        }

        try {
            console.log('execute...', f);
            return f();
        } catch (exception) {
            console.warn('execution gave exception', exception, f);
        }
    }
}