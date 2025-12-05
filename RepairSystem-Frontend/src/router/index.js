import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: '',
    component: () => import(/* webpackChunkName: "landing" */ '../pages/Landing/Landing.vue')
  },
  {
    path: '/login',
    component: () => import(/* webpackChunkName: "login" */ '../pages/LogInAndSignUp/LogIn.vue')
  },
  {
    path: '/signup',
    component: () => import(/* webpackChunkName: "signup" */ '../pages/LogInAndSignUp/SignUp.vue')
  },
  {
    path: '/customerDashboard',
    component: () => import(/* webpackChunkName: "customer-dashboard" */ '../pages/Welcome/CustomerDashboard.vue'),
    children: [
      {
        path: '/customerDashboard/Overview/:userId',
        component: () => import(/* webpackChunkName: "customer-overview" */ '../pages/Welcome/CustomerOverview.vue')
      },
      {
        path: '/customerDashboard/myAccount/:userId',
        component: () => import(/* webpackChunkName: "customer-profile" */ '../pages/Welcome/CustomerEditProfile.vue')
      },
      {
        path: '/customerDashboard/cars/:userId',
        component: () => import(/* webpackChunkName: "customer-cars" */ '../pages/Welcome/CustomerCars.vue')
      },
      {
        path: '/customerDashboard/services/:userId',
        component: () => import(/* webpackChunkName: "service-browser" */ '../pages/Welcome/ServiceBrowser.vue')
      },
      {
        path: '/customerDashboard/bookAppointment/:userId',
        component: () => import(/* webpackChunkName: "book-appointment" */ '../pages/Booking/CustomerBookAppointment.vue')
      },
      {
        path: '/customerDashboard/myCars/:userId',
        component: () => import(/* webpackChunkName: "customer-cars" */ '../pages/Welcome/CustomerCars.vue')
      }
    ]
  },
  {
    path: '/mechanicDashboard',
    component: () => import(/* webpackChunkName: "mechanic-dashboard" */ '../pages/Welcome/MechanicDashboard.vue'),
    children: [
      {
        path: '/mechanicDashboard/Overview/:userId',
        component: () => import(/* webpackChunkName: "mechanic-overview" */ '../pages/Welcome/MechanicOverview.vue')
      },
      {
        path: '/mechanicDashboard/myAccount/:userId',
        component: () => import(/* webpackChunkName: "mechanic-profile" */ '../pages/Welcome/MechanicEditProfile.vue')
      },
      {
        path: '/mechanicDashboard/Schedule/:userId',
        component: () => import(/* webpackChunkName: "mechanic-schedule" */ '../pages/Welcome/MechanicSchedule.vue')
      },
      {
        path: '/mechanicDashboard/Services/:userId',
        component: () => import(/* webpackChunkName: "mechanic-services" */ '../pages/Welcome/MechanicServices.vue')
      }
    ]
  },
  {
    path: '/adminDashboard',
    component: () => import(/* webpackChunkName: "admin-dashboard" */ '../pages/Welcome/AdminDashboard.vue'),
    children: [
      {
        path: '/adminDashboard/Overview/:userId',
        component: () => import(/* webpackChunkName: "admin-overview" */ '../pages/Welcome/AdminOverview.vue')
      },
      {
        path: '/adminDashboard/myAccount/:userId',
        component: () => import(/* webpackChunkName: "admin-profile" */ '../pages/Welcome/AdminEditProfile.vue')
      },
      {
        path: '/adminDashboard/TeamMembers/:userId',
        component: () => import(/* webpackChunkName: "team-members" */ '../pages/Welcome/TeamMembers.vue')
      },
      {
        path: '/adminDashboard/Customers/:userId',
        component: () => import(/* webpackChunkName: "admin-customers" */ '../pages/Welcome/AdminCustomer.vue')
      },
      {
        path: '/adminDashboard/Services/:userId',
        component: () => import(/* webpackChunkName: "admin-services" */ '../pages/Welcome/AdminServices.vue'),
        name: 'AdminServices'
      },
      {
        path: '/adminDashboard/Services/:userId/:serviceType',
        component: () => import(/* webpackChunkName: "admin-service-details" */ '../pages/Welcome/AdminServiceDetails.vue'),
        name: 'AdminServiceDetails'
      },
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
