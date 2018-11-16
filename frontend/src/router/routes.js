import Home from './../pages/Home.vue'
import Signin from './../pages/Signin.vue'
import Signup from './../pages/Signup.vue'
import Rooms from './../pages/Rooms.vue'
import ForgotPass from './../pages/ForgotPass.vue'
import ResetPass from './../pages/ResetPass.vue'
import NotFound from './../pages/NotFound.vue'
import VerificationEmail from './../pages/VerificationEmail.vue'
import Profile from './../pages/Profile.vue'
import Settings from './../pages/Settings.vue'

export default [{
    name: 'home',
    path: '/',
    component: Home
  },
  {
    name: 'signin',
    path: '/signin',
    component: Signin,
    meta: {
      guest: true
    }
  },
  {
    name: 'signup',
    path: '/signup',
    component: Signup,
    meta: {
      guest: true
    }
  },
  {
    name: 'rooms',
    path: '/rooms',
    component: Rooms,
    meta: {
      requiresAuth: true
    }
  },
  {
    name: 'forgotPass',
    path: '/forgot-password',
    component: ForgotPass,
    meta: {
      guest: true
    }
  },
  {
    name: 'resetPass',
    path: '/reset-password',
    component: ResetPass,
    meta: {
      guest: true
    }
  },
  {
    name: 'notFound',
    path: '/notfound',
    component: NotFound
  },
  {
    name:'VerificationEmail',
    path: '/verification',
    component: VerificationEmail
  },
  {
    name: 'settings',
    path: '/settings',
    component: Settings,
    meta: {
      requiresAuth: true
    }
  },
  {
    name: 'profile',
    path: '/profile/:userName',
    component: Profile,
    meta: {
      requiresAuth: true
    }
  }

]
