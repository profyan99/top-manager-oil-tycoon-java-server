import axios from 'axios'
import { backendUrl } from './store/modules/misc.js'
import store from "./store"

const api = axios.create({
    baseURL: backendUrl,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json;charset=UTF-8',
        'Access-Control-Allow-Origin': '*',
    }
});

api.interceptors.response.use(undefined, function(err) {
  console.log("EROR INTERCEPTOR....", err.status);
  return new Promise(function(resolve, reject) {
    //&& err.data.errors[0].errorCode == 'AUTHENTICATION_ERROR'
    if (err.status === 403) {
      console.log("inner");
      store.dispatch("logOut");
    }
    throw err;
  });
});

export default api;
