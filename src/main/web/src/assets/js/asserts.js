export default {

    hw() {
      console.log('helloworld');
    },

    isFunction(functionToCheck, message='value is not a function') {
        if ( typeof functionToCheck == 'function' ) {
            return;
        }
        throw new Error( message );
    },

    isNotNull(value, message='value must not be null') {
        if ( value != null ) {
            return;
        }
        throw new Error( message );
    },

    isEquals(obj1, obj2, message = 'objects are not equal') {
        if ( obj1 === obj2 ) { return; }
        throw new Error( message )
    }

}