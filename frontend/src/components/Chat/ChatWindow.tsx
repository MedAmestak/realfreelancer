import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { connectSocket } from '../../utils/socket';
import { Client } from '@stomp/stompjs';
import MessageInput from './MessageInput';
import axiosInstance from '../../utils/axiosInstance';

interface ChatWindowProps {
conversationId: number | null;
onMessagesRead?: () => void;
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

const ChatWindow: React.FC<ChatWindowProps> = ({ conversationId, onMessagesRead }) => {
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
const [windowFocused, setWindowFocused] = useState(true);
const audioRef = useRef<HTMLAudioElement | null>(null);
const markAsReadCalledRef = useRef<{ [key: string]: boolean }>({});
const [otherUserId, setOtherUserId] = useState<number | null>(null);

const fetchMessages = useCallback(async () => {
    if (!conversationId) return;
    setLoading(true);
    setError(null);
    try {
        const response = await axiosInstance.get(`/chat/conversation/${conversationId}?page=0&size=50`);
        const data = response.data;
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
    } catch (e: any) {
        setError(e.response?.data?.message || 'An error occurred while fetching messages.');
    } finally {
        setLoading(false);
    }
}, [conversationId, user]);

useEffect(() => {
    fetchMessages();
}, [fetchMessages]);

useEffect(() => {
    const onFocus = () => setWindowFocused(true);
    const onBlur = () => setWindowFocused(false);
    window.addEventListener('focus', onFocus);
    window.addEventListener('blur', onBlur);
    return () => {
        window.removeEventListener('focus', onFocus);
        window.removeEventListener('blur', onBlur);
    };
}, []);

  // WebSocket real-time updates
useEffect(() => {
    if (!user || !conversationId) return;
    socketRef.current = connectSocket({
      onMessage: (msg) => {
        fetchMessages();
      },
      user: user.username,
      token: getAuthToken() || '',
      onTyping: (msg) => {
        // handle typing event
      },
    });
    return () => {
      if (socketRef.current) (socketRef.current as { disconnect: () => void }).disconnect();
    };
  }, [user, conversationId, getAuthToken]);

useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
}, [messages]);

useEffect(() => {
    // Compute the other user's ID from the messages list
    if (messages.length > 0 && user) {
      const firstMsg = messages[0];
      const otherId = firstMsg.senderId === user.id ? firstMsg.receiverId : firstMsg.senderId;
      setOtherUserId(otherId);
    }
}, [messages, user]);

useEffect(() => {
    if (
      windowFocused &&
      user &&
      conversationId
    ) {
      // Only call mark-as-read once per focus/conversation
      const key = `${user.id}-${conversationId}`;
      if (!markAsReadCalledRef.current[key]) {
        markAsReadCalledRef.current[key] = true;
        console.debug('[Chat] Marking messages as read for conversationId:', conversationId, 'user.id:', user.id);
        axiosInstance.put(`/chat/read/${conversationId}`).then(() => {
          if (typeof onMessagesRead === 'function') onMessagesRead();
        });
      }
    } else if (windowFocused && user && conversationId === user.id) {
      // Reset the flag if self-chat or invalid
      markAsReadCalledRef.current = {};
    }
}, [windowFocused, user, conversationId, getAuthToken, onMessagesRead]);

if (!conversationId) {
    return <div className="flex-1 flex items-center justify-center text-gray-400">Select a conversation to start chatting.</div>;
}
if (user && conversationId === user.id) {
    return <div className="flex-1 flex items-center justify-center text-gray-400">You cannot chat with yourself.</div>;
}
if (loading) return <div className="flex-1 flex items-center justify-center text-gray-400">Loading messages...</div>;
if (error) return <div className="flex-1 flex items-center justify-center text-red-500">{error}</div>;

return (
    <div className="flex-1 flex flex-col bg-gray-50">
      <audio ref={audioRef} src="./notification.mp3" preload="auto" />
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
            <div className="text-xs text-gray-500 italic mb-2">The user is typing...</div>
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