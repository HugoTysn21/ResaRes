import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store';

import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

import VeeValidate from 'vee-validate';
import vuetify from './plugins/vuetify';

Vue.config.productionTip = false;

Vue.use(VeeValidate);

// Config vue to ignore warn message caused by Vuetify calendar component
const ignoreWarnMessage = 'The .native modifier for v-on is only valid on components but it was used on <div>.';
Vue.config.warnHandler = function (msg) {
  if (msg === ignoreWarnMessage) {
    msg = null;
  }
};

// Init vue
new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app');
