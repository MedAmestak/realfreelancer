import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';

interface MessageInputProps {
  conversationId: number | null;
  onMessageSent?: () => void;
}

const MessageInput: React.FC<MessageInputProps> = ({ conversationId, onMessageSent }) => {
  const { getAuthToken } = useAuth();
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!conversationId || !message.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const token = getAuthToken();
      const res = await fetch('http://localhost:8080/api/chat/send', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ recipientId: conversationId, content: message })
      });
      if (!res.ok) throw new Error('Failed to send message');
      setMessage('');
      if (onMessageSent) onMessageSent();
    } catch (e: unknown) {
      if (e instanceof Error) {
        setError(e.message);
      } else {
        setError('An unknown error occurred');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSend} className="flex gap-2">
      <input
        type="text"
        className="flex-1 px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-400"
        placeholder={conversationId ? 'Type your message...' : 'Select a conversation to start chatting'}
        value={message}
        onChange={e => setMessage(e.target.value)}
        disabled={!conversationId || loading}
        autoComplete="off"
      />
      <button
        type="submit"
        className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-lg font-semibold transition disabled:opacity-50"
        disabled={!conversationId || loading || !message.trim()}
      >
        Send
      </button>
      {error && <div className="text-red-500 text-xs ml-2 self-center">{error}</div>}
    </form>
  );
};

export default MessageInput; 