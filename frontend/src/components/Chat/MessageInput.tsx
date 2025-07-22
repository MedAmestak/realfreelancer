import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import axiosInstance from '../../utils/axiosInstance';

interface MessageInputProps {
  conversationId: number | null;
  projectId: number | null;
  onMessageSent?: () => void;
  inputClassName?: string;
  buttonClassName?: string;
  sendTyping?: (payload: object) => void;
  user?: { id: number; username: string } | null;
  otherUsername?: string | null;
}

const MessageInput: React.FC<MessageInputProps> = ({ conversationId, projectId, onMessageSent, inputClassName = '', buttonClassName = '', sendTyping, user, otherUsername }) => {
  const { getAuthToken } = useAuth();
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const typingTimeout = React.useRef<NodeJS.Timeout | null>(null);
  const [isTyping, setIsTyping] = useState(false);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!conversationId || !message.trim()) return;
    setLoading(true);
    setError(null);
    try {
      await axiosInstance.post('/chat/send', { conversationId, content: message });
      setMessage('');
      if (onMessageSent) onMessageSent();
    } catch (e: any) {
      setError(e.response?.data?.message || 'An unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleTyping = (typing: boolean) => {
    if (!sendTyping || !user || !conversationId) return;
    console.log('Sending typing event:', { user, conversationId, otherUsername, typing });
    sendTyping({
      senderId: user.id,
      senderUsername: user.username,
      receiverId: conversationId,
      receiverUsername: otherUsername || '',
      typing,
    });
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setMessage(e.target.value);
    if (!isTyping) {
      setIsTyping(true);
      handleTyping(true);
    }
    if (typingTimeout.current) clearTimeout(typingTimeout.current);
    typingTimeout.current = setTimeout(() => {
      setIsTyping(false);
      handleTyping(false);
    }, 1500);
  };

  const handleFocus = () => {
    if (!isTyping) {
      setIsTyping(true);
      handleTyping(true);
    }
    if (typingTimeout.current) clearTimeout(typingTimeout.current);
  };

  const handleBlur = () => {
    setIsTyping(false);
    handleTyping(false);
    if (typingTimeout.current) clearTimeout(typingTimeout.current);
  };

  return (
    <form onSubmit={handleSend} className="flex gap-2">
      <input
        type="text"
        className={inputClassName}
        placeholder={conversationId ? 'Type your message...' : 'Select a conversation to start chatting'}
        value={message}
        onChange={handleInputChange}
        onFocus={handleFocus}
        onBlur={handleBlur}
        disabled={!conversationId || loading}
        autoComplete="off"
      />
      <button
        type="submit"
        className={buttonClassName}
        disabled={!conversationId || loading || !message.trim()}
      >
        Send
      </button>
      {error && <div className="text-red-500 text-xs ml-2 self-center">{error}</div>}
    </form>
  );
};

export default MessageInput; 