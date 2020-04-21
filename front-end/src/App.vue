<template>
  <v-app id="app">
    <div class="row">
      <Menu></Menu>
      <router-view></router-view>
    </div>
  </v-app>
</template>

<script>
import Menu from './components/Menu'

export default {
  name: 'App',

  components: {
    Menu,
  },

  data: () => ({
    //
  }),
};
</script>

<style>
  @import './assets/style.css';
</style>
