<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'app'
}
</script>

<style>
#app {
  font-family: 'Inter', sans-serif;
  background-color: var(--bg-primary);
  min-height: 100vh;
  color: var(--text-primary);
}
</style>
