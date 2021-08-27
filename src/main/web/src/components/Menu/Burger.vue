<template>

  <div id="burger" @click.prevent="toggle">
    <slot>
      <button type="button" class="burger-button" title="Menu">
        <span class="burger-bar burger-bar--1"></span>
        <span class="burger-bar burger-bar--2"></span>
        <span class="burger-bar burger-bar--3"></span>
      </button>
    </slot>
  </div>

</template>
<script>
export default {

  methods: {

    toggle() {
      this.$root.bus.$emit('main-menu');
    }
  }
}
</script>
<style scoped>

button {
  cursor: pointer;
}

.burger-button {
  position: relative;
  height: 30px;
  width: 32px;
  display: block;
  z-index: 999;
  border: 0;
  border-radius: 0;
  background-color: transparent;
  pointer-events: all;
}

.burger-bar {
  background-color: #A9D24E;
  position: absolute;
  top: 50%;
  right: 6px;
  left: 6px;
  height: 2px;
  width: auto;
  margin-top: -1px;
}

.burger-bar--1 {
  -webkit-transform: translateY(-6px);
  transform: translateY(-6px);
}

.burger-bar--2 {
  transform-origin: 100% 50%;
  transform: scaleX(1);
}

.burger-bar--3 {
  transform: translateY(6px);
}

#burger {
  align-self: flex-end;
}

</style>