export default {

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
    }
}