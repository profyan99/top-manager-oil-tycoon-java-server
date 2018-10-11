import Home from './pages/Home.vue'
import Signin from './pages/Signin.vue'
import Signup from './pages/Signup.vue'
import Rooms from './pages/Rooms.vue'
import ForgotPass from './pages/ForgotPass.vue'

export default [{
    name: 'Home',
    path: '/',
    component: Home
  },
  {
    name: 'signin',
    path: '/signin',
    component: Signin
  },
  {
    name: 'signup',
    path: '/signup',
    component: Signup
  },
  {
    name: 'rooms',
    path: '/rooms',
    component: Rooms
  },
  {
    name: 'forgotPass',
    path: '/forgotpass',
    component: ForgotPass
  }
]
