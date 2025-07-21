import React, { useEffect, useState, useRef, useCallback, useMemo } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { useRouter } from 'next/navigation';
import { connectSocket } from '../../utils/socket';

interface ConversationListProps {
  selectedConversation: number | null;
  onSelect: (conversationId: number) => void;
  refreshKey?: number;
}

interface Conversation {
  id: number;
  username: string;
  avatarUrl?: string;
  lastMessageTime: string;
  unreadCount: number;
}

interface ConversationApi {
  conversationId: number;
  username: string;
  avatarUrl?: string;
  lastMessageTime: string;
  unreadCount: number;
}

const ConversationList: React.FC<ConversationListProps> = ({ selectedConversation, onSelect, refreshKey }) => {
  const { getAuthToken, user } = useAuth();
  const router = useRouter();
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const socketRef = useRef<unknown>(null);

  const fetchConversations = async () => {
    setLoading(true);
    setError(null);
    try {
      const token = getAuthToken();
      const res = await fetch('http://localhost:8080/api/chat/conversations?page=0&size=20', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Failed to fetch conversations');
      const data: ConversationApi[] = await res.json();
      setConversations(data.map((item) => ({
        id: item.conversationId,
        username: item.username,
        avatarUrl: item.avatarUrl,
        lastMessageTime: item.lastMessageTime,
        unreadCount: item.unreadCount,
      })));
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

  useEffect(() => {
    fetchConversations();
  }, [getAuthToken, refreshKey]);

  // WebSocket for real-time unread badge updates
  useEffect(() => {
    if (!user) return;
    const token = getAuthToken();
    socketRef.current = connectSocket({
      onMessage: (msg) => {
        // Always refresh conversations on new message
        fetchConversations();
      },
      user: user.username,
      token: token || '',
    });
    return () => {
      if (socketRef.current) (socketRef.current as { disconnect: () => void }).disconnect();
    };
  }, [user, getAuthToken]);

  const handleSelect = (id: number) => {
    if (!id || isNaN(id)) {
      console.warn('Tried to select invalid conversation id:', id);
      return;
    }
    onSelect(id);
  };

  const handleListItemClick = useCallback((id: number) => {
    handleSelect(id);
  }, [handleSelect]);

  // Memoized handlers for each conversation id
  const listItemClickHandlers = useMemo(() => {
    const handlers: { [id: number]: () => void } = {};
    conversations.forEach(conv => {
      handlers[conv.id] = () => handleListItemClick(conv.id);
    });
    return handlers;
  }, [conversations, handleListItemClick]);

  if (loading) return <div className="text-center text-gray-400 py-8">Loading...</div>;
  if (error) return <div className="text-center text-red-500 py-8">{error}</div>;
  if (conversations.length === 0) return <div className="text-center text-gray-400 py-8">No conversations yet.</div>;

  return (
    <ul className="space-y-2">
      {conversations
        .filter(conv => conv.id && !isNaN(conv.id))
        .filter(conv => !(user && conv.id === user.id))
        .map(conv => {
          const isUnread = conv.unreadCount > 0;
          return (
            <li
              key={conv.id}
              className={`flex items-center gap-3 p-3 rounded-lg cursor-pointer transition border hover:bg-gray-100 ${selectedConversation === conv.id ? 'bg-blue-50 border-blue-400' : isUnread ? 'bg-blue-100 border-blue-300' : 'bg-white border-transparent'}`}
              onClick={listItemClickHandlers[conv.id]}
            >
              <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-lg font-bold text-gray-500">
                {conv.avatarUrl ? (
                  <img src={conv.avatarUrl} alt={conv.username} className="w-10 h-10 rounded-full object-cover" />
                ) : (
                  conv.username[0]?.toUpperCase()
                )}
              </div>
              <div className="flex-1 min-w-0">
                <div className={`truncate ${isUnread ? 'font-bold text-blue-900' : 'font-medium text-gray-900'}`}>{conv.username}</div>
                <div className="text-xs text-gray-400 truncate">{conv.lastMessageTime ? new Date(conv.lastMessageTime).toLocaleString() : ''}</div>
              </div>
              {isUnread && (
                <span className="ml-2 bg-blue-600 text-white text-xs font-bold px-2 py-1 rounded-full shadow">{conv.unreadCount}</span>
              )}
            </li>
          );
        })}
    </ul>
  );
};

export default ConversationList; 