import React, { useEffect, useState, useRef } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { connectSocket } from '../../utils/socket';

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
}

const ChatWindow: React.FC<ChatWindowProps> = ({ conversationId }) => {
  const { getAuthToken, user } = useAuth();
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const socketRef = useRef<any>(null);

  // Fetch messages on conversation change
  useEffect(() => {
    if (!conversationId) return;
    setLoading(true);
    setError(null);
    const fetchMessages = async () => {
      try {
        const token = getAuthToken();
        const res = await fetch(`http://localhost:8080/api/chat/conversation/${conversationId}?page=0&size=50`, {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error('Failed to fetch messages');
        const data = await res.json();
        setMessages(Array.isArray(data) ? data : data.content || []);
      } catch (e: any) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };
    fetchMessages();
  }, [conversationId, getAuthToken]);

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
    <div className="flex-1 overflow-y-auto p-4 sm:p-6 bg-gray-50">
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
        <div ref={messagesEndRef} />
      </div>
    </div>
  );
};

export default ChatWindow; 