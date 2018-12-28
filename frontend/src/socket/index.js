import SockJS from "sockjs-client";
//import Stomp from '@stomp/stompjs'
import Stomp from "webstomp-client";
import store from '../store'
import {
  SET_SOCKET_CONNECTED,
  SET_NEW_MESSAGE
} from '../store/util/mutationTypes.js'


var stompClient = null;

export function socketConnect() {
  let socket = new SockJS(store.getters.getUrls.socket + '?access_token=' + store.getters.accessToken);
  stompClient = Stomp.over(socket);
  return new Promise((resolve, reject) => {
    stompClient.connect({}, function(frame) {
      store.commit(SET_SOCKET_CONNECTED, true);
      console.log("Socket connected", frame);
      subscribeTopics();
      resolve();
    }, function(error) {
      console.log(error);
      reject(error);
    });
  });
}

function subscribeTopics() {
  stompClient.subscribe(store.getters.getSocketTopics.roomList, function(message) {
    store.commit(SET_NEW_MESSAGE, JSON.parse(message.body));
    console.log("Message", JSON.parse(message.body));
  });
  stompClient.subscribe(store.getters.getSocketEndpoints.error, function(message) {
    console.log("Error: ", message);
  })
}

export function sendMessage(message) {
  stompClient.send(store.getters.getSocketEndpoints.room, JSON.stringify(message), {});
}
