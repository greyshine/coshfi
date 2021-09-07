<template>
<div>
  <ul>
    <li v-for="(msg, index) in messages" :key="msg" :class="{ error: errorIndices.indexOf(index) > -1 }">
      {{msg}}
    </li>
  </ul>
</div>
</template>

<script>

export default {

  data: ()=>({
    messages:[],
    errorIndices:[]
  }),

  methods: {

    error(message) {
      this.add(message, true);
    },

    add(message, error = false) {

      if ( message == null ) { return; }
      message = ''+ message;

      if ( message.trim() === '' ) { return; }

      for( let m in this.messages ) {
        if ( m === message) {
          console.log('skipping adding existing message', message);
          return;
        }
      }

      console.log('add', message);
      this.messages.push(message);

      if ( error === true ) {
        this.errorIndices.push( this.messages.length-1 );
      }
    },

    clear(field) {

      if ( typeof field == 'number' ) {

        if (field <= -1 || field >= this.messages.length) {
          return;
        }

        this.messages.splice(field, 1);

        for(let i=0; i<this.errorIndices.length; i++) {
          if ( this.errorIndices[i] === field ) {
            this.errorIndices.splice(i,1);
            break;
          }
        }

      } else if ( field != null && typeof field != 'undefined' ) {

        this.clear( this.messages.indexOf( ''+field ) );

      } else {

        this.messages = [];
        this.errorIndices = [];
      }
    }

  }
}

</script>

<style scoped lang="scss">

div {

  background-color: #FFFFFF;
  border-radius: 3px;

  ul {
    padding: 0;
    margin-left: 25px;
    list-style-type: disc;

    li:first-child {
      padding-top: 10px;
    }

    li:last-child {
      padding-bottom: 10px;
    }

    li.error {
      color: $colorError
    }

  }
}
</style>