import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import User from '../views/User.vue'
import Organization from "../views/Organization"
import Organizations from "../views/Organizations";
import Resources from "../views/Resources";
import Reservations from "../views/Reservations";

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    component: Home
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/users',
    component: User
  },
  {
    path: '/organization',
    component: Organization
  },
  {
    path: '/organizations',
    component: Organizations
  },
  {
    path: '/resources',
    component: Resources
  },
  {
    path: '/reservations',
    component: Reservations
  }
]

const router = new VueRouter({
  routes
})

export default router
