<template>
  <div>
    <OpenStreetMap/>
  </div>
</template>

<script>

import OpenStreetMap from "@/components/OpenStreetMap";
export default {
  components: {OpenStreetMap},
  data: ()=>({
    user: 'USER'
  }),

  mount() {},

  methods: {}
}

</script>

<style scoped lang="scss">

</style>