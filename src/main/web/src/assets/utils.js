import axios from "axios";

const doNothing = function() {};

export default {

    /**
     * Intended to do nothing.
     * Invented for validation not to skip compiling due to empty empty methods.
     */
    void() {},

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

        return value.length == 0 ? null : value;
    },

    matches(regex, text) {

        if (regex == null || typeof regex != 'string') {
            throw new Error('Bad text or regex.');
        } else if (text == null) {
            return false;
        }

        return new RegExp(regex, 'g').test(''+text);
    },

    validateRemote(fieldValueObject, handler) {

        console.assert( typeof fieldValueObject == 'object' );

        //asserts.assertNotNull(fieldsValuesObject);
        handler = typeof handler == 'function' ? handler : ()=>console.log('no handler defined');

        //asserts.assertFunction(handler);
        axios.post('/api/validate', fieldValueObject)
            .then( response=>{
                    doNothing(response);
                },
                error=>{
                    if ( typeof handler == 'function' ) {
                        handler(error);
                    } else {
                        console.error('no handler declared.', fieldValueObject)
                    }
            } )
            .catch( exception=>{
                console.error( exception );
            }
        );
    }
}