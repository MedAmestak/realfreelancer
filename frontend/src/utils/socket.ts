import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { use } from 'react';
import SockJS from 'sockjs-client';

const SOCKET_URL = 'http://localhost:8080/ws';

export interface SocketOptions {
  onMessage: (msg: IMessage) => void;
  user: string;
  token: string;
}

export function connectSocket({ onMessage, user, token }: SocketOptions) {
  const client = new Client({
    webSocketFactory: () => new SockJS(SOCKET_URL),
    connectHeaders: {
      Authorization: `Bearer ${token}`
    },
    debug: () => {},
    reconnectDelay: 5000,
  });

  let subscription: StompSubscription | null = null;

  client.onConnect = () => {
    subscription = client.subscribe(`/user/${user}/queue/messages`, onMessage);
  };

  client.onDisconnect = () => {
    if (subscription) subscription.unsubscribe();
  };

  client.activate();

  return {
    disconnect: () => client.deactivate(),
    send: (destination: string, body: string) => client.publish({ destination, body }),
    client,
  };
} 