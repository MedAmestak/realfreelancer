import React, { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';

interface ConversationListProps {
  selectedConversation: number | null;
  onSelect: (conversationId: number) => void;
}

interface Conversation {
  id: number;
  username: string;
  avatarUrl?: string;
  lastMessageTime: string;
  unreadCount: number;
}

interface ConversationApi {
  userId: number;
  username: string;
  avatarUrl?: string;
  lastMessageTime: string;
  unreadCount: number;
}

const ConversationList: React.FC<ConversationListProps> = ({ selectedConversation, onSelect }) => {
  const { getAuthToken } = useAuth();
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
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
          id: item.userId,
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
    fetchConversations();
  }, [getAuthToken]);

  if (loading) return <div className="text-center text-gray-400 py-8">Loading...</div>;
  if (error) return <div className="text-center text-red-500 py-8">{error}</div>;
  if (conversations.length === 0) return <div className="text-center text-gray-400 py-8">No conversations yet.</div>;

  return (
    <ul className="space-y-2">
      {conversations.map(conv => (
        <li
          key={conv.id}
          className={`flex items-center gap-3 p-3 rounded-lg cursor-pointer transition border hover:bg-gray-100 ${selectedConversation === conv.id ? 'bg-blue-50 border-blue-400' : 'bg-white border-transparent'}`}
          onClick={() => onSelect(conv.id)}
        >
          <div className="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center text-lg font-bold text-gray-500">
            {conv.avatarUrl ? (
              <img src={conv.avatarUrl} alt={conv.username} className="w-10 h-10 rounded-full object-cover" />
            ) : (
              conv.username[0]?.toUpperCase()
            )}
          </div>
          <div className="flex-1 min-w-0">
            <div className="font-medium text-gray-900 truncate">{conv.username}</div>
            <div className="text-xs text-gray-400 truncate">{conv.lastMessageTime ? new Date(conv.lastMessageTime).toLocaleString() : ''}</div>
          </div>
          {conv.unreadCount > 0 && (
            <span className="ml-2 bg-blue-500 text-white text-xs font-semibold px-2 py-1 rounded-full">{conv.unreadCount}</span>
          )}
        </li>
      ))}
    </ul>
  );
};

export default ConversationList; 