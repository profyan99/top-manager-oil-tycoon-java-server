import Home from './pages/Home.vue'
import Login from './pages/Login.vue'
import Signup from './pages/Signup.vue'

export default [
    {
        name: 'Home',
        path: '/',
        component: Home
    },
    {
        Name: 'signin',
        path: '/signin',
        component: Login
    },
    {
        Name: 'signup',
        path: '/signup',
        component: Signup
    }
]