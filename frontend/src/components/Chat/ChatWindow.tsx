import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { connectSocket } from '../../utils/socket';
import { Client } from '@stomp/stompjs';
import MessageInput from './MessageInput';

interface ChatWindowProps {
conversationId: number | null;
}

interface Message {
id: number;
content: string;
senderId: number;
senderUsername: string;
receiverId: number;
receiverUsername: string;
isRead: boolean;
attachmentUrl?: string;
type: string;
createdAt: string;
projectId?: number;
senderEmail: string;
receiverEmail: string;
}

type SocketConnection = {
  disconnect: () => Promise<void>;
  send: (destination: string, body: string) => void;
  sendTyping: (typingPayload: object) => void;
  client: Client;
};

const ChatWindow: React.FC<ChatWindowProps> = ({ conversationId }) => {
const { getAuthToken, user } = useAuth();
const [messages, setMessages] = useState<Message[]>([]);
const [loading, setLoading] = useState(false);
const [error, setError] = useState<string | null>(null);
const messagesEndRef = useRef<HTMLDivElement>(null);
const socketRef = useRef<SocketConnection | null>(null);
const [projectId, setProjectId] = useState<number | null>(null);
const [isOtherTyping, setIsOtherTyping] = useState(false);
const typingTimeoutRef = useRef<NodeJS.Timeout | null>(null);
const [otherUsername, setOtherUsername] = useState<string | null>(null);

const fetchMessages = useCallback(async () => {
    if (!conversationId) return;
    setLoading(true);
    setError(null);
    try {
        const token = getAuthToken();
        const res = await fetch(`http://localhost:8080/api/chat/conversation/${conversationId}?page=0&size=50`, {
        headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error('Failed to fetch messages');
        let data: Message[] | { content: Message[] } = await res.json();
        let msgs = Array.isArray(data) ? data : data.content || [];
        msgs = msgs.slice().sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());
        setMessages(msgs);
        if (msgs.length > 0 && msgs[0].projectId) {
        setProjectId(msgs[0].projectId);
        } else {
        setProjectId(null);
        }
        if (msgs.length > 0 && user) {
          const firstMsg = msgs[0];
          // Use receiverUsername or senderUsername as the principal for typing events
          const otherPrincipal = firstMsg.senderId === user.id ? firstMsg.receiverUsername : firstMsg.senderUsername;
          setOtherUsername(otherPrincipal);
        }
    } catch (e: unknown) {
        if (e instanceof Error) {
        setError(e.message);
        } else {
        setError('An unknown error occurred');
        }
    } finally {
        setLoading(false);
    }
}, [conversationId, getAuthToken, user]);

useEffect(() => {
    fetchMessages();
}, [fetchMessages]);

  // WebSocket real-time updates
useEffect(() => {
    if (!user || !conversationId) return;
    const token = getAuthToken();
    socketRef.current = connectSocket({
    onMessage: (msg) => {
        try {
        const message: Message = JSON.parse(msg.body);
          // Only add if message is for this conversation
        if (
            (message.senderId === user.id && message.receiverId === conversationId) ||
            (message.senderId === conversationId && message.receiverId === user.id)
        ) {
            setMessages((prev) => [...prev, message]);
        }
        } catch {}
    },
    user: user.username,
    token: token || '',
    onTyping: (msg) => {
      try {
        const typing = JSON.parse(msg.body);
        console.log('Received typing event:', typing);
        if (
          typing.senderId === conversationId &&
          typing.receiverId === user.id &&
          typing.typing
        ) {
          setIsOtherTyping(true);
          if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
          typingTimeoutRef.current = setTimeout(() => setIsOtherTyping(false), 2000);
        } else if (
          typing.senderId === conversationId &&
          typing.receiverId === user.id &&
          !typing.typing
        ) {
          setIsOtherTyping(false);
        }
      } catch {}
    },
    });
    return () => {
    if (socketRef.current) socketRef.current.disconnect();
    };
}, [user, conversationId, getAuthToken]);

useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
}, [messages]);

if (!conversationId) {
    return <div className="flex-1 flex items-center justify-center text-gray-400">Select a conversation to start chatting.</div>;
}
if (loading) return <div className="flex-1 flex items-center justify-center text-gray-400">Loading messages...</div>;
if (error) return <div className="flex-1 flex items-center justify-center text-red-500">{error}</div>;

return (
    <div className="flex-1 flex flex-col bg-gray-50">
      {/* Messages area: scrollable */}
      <div className="flex-1 overflow-y-auto p-4 sm:p-6 bg-gray-50" style={{ maxHeight: '60vh' }}>
        <div className="flex flex-col gap-4">
          {messages.length === 0 && (
            <div className="text-center text-gray-400">No messages yet. Say hello!</div>
          )}
          {messages.map(msg => (
            <div
              key={msg.id}
              className={`max-w-[80%] px-4 py-2 rounded-lg shadow-sm text-sm break-words ${msg.senderId === user?.id ? 'bg-blue-100 ml-auto text-right' : 'bg-white mr-auto text-left'}`}
            >
              <div className="font-medium text-gray-800 mb-1">{msg.senderUsername}</div>
              <div>{msg.content}</div>
              <div className="text-xs text-gray-400 mt-1">{new Date(msg.createdAt).toLocaleTimeString()}</div>
            </div>
          ))}
          {isOtherTyping && (
            <div className="text-xs text-gray-500 italic mb-2">The other user is typing...</div>
          )}
          <div ref={messagesEndRef} />
        </div>
      </div>
      {/* Message input stays fixed at the bottom, styled */}
      <div className="border-t bg-white p-4 sticky bottom-0 z-10">
        <MessageInput 
          conversationId={conversationId} 
          projectId={projectId} 
          onMessageSent={fetchMessages} 
          inputClassName="flex-1 px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-gray-50" 
          buttonClassName="ml-2 px-6 py-2 rounded-lg font-bold bg-blue-600 hover:bg-blue-700 text-white transition-colors duration-150 shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-400 focus:ring-offset-2"
          sendTyping={socketRef.current?.sendTyping}
          user={user}
          otherUsername={otherUsername}
        />
      </div>
    </div>
);
};

export default ChatWindow;