import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { use } from 'react';
import SockJS from 'sockjs-client';

const SOCKET_URL = (token: string) => `http://localhost:8080/ws?token=${token}`;

export interface SocketOptions {
  onMessage: (msg: IMessage) => void;
  user: string;
  token: string;
  onTyping?: (msg: IMessage) => void;
}

export function connectSocket({ onMessage, user, token, onTyping }: SocketOptions) {
  const client = new Client({
    webSocketFactory: () => new SockJS(SOCKET_URL(token)),
    connectHeaders: {
      Authorization: `Bearer ${token}`
    },
    debug: () => {},
    reconnectDelay: 5000,
  });

  let messageSubscription: StompSubscription | null = null;
  let typingSubscription: StompSubscription | null = null;

  client.onConnect = () => {
    messageSubscription = client.subscribe(`/user/${user}/queue/messages`, onMessage);
    if (onTyping) {
      typingSubscription = client.subscribe(`/user/${user}/queue/typing`, onTyping);
    }
  };

  client.onDisconnect = () => {
    if (messageSubscription) messageSubscription.unsubscribe();
    if (typingSubscription) typingSubscription.unsubscribe();
  };

  client.activate();

  return {
    disconnect: () => client.deactivate(),
    send: (destination: string, body: string) => {
      if (client.connected) {
        client.publish({ destination, body });
      } else {
        console.warn('STOMP client not connected');
      }
    },
    sendTyping: (typingPayload: object) => {
      if (client.connected) {
        client.publish({ destination: '/app/typing', body: JSON.stringify(typingPayload) });
      } else {
        console.warn('STOMP client not connected');
      }
    },
    client,
  };
}