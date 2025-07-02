import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { use } from 'react';
import SockJS from 'sockjs-client';

const SOCKET_URL = 'http://localhost:8080/ws';

export interface SocketOptions {
  onMessage: (msg: IMessage) => void;
  user: string;
  token: string;
  onTyping?: (msg: IMessage) => void;
}

export function connectSocket({ onMessage, user, token, onTyping }: SocketOptions) {
  const client = new Client({
    webSocketFactory: () => new SockJS(SOCKET_URL),
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
    send: (destination: string, body: string) => client.publish({ destination, body }),
    sendTyping: (typingPayload: object) => client.publish({ destination: '/app/typing', body: JSON.stringify(typingPayload) }),
    client,
  };
} 