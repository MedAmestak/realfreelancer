'use client';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import axiosInstance from '../../src/utils/axiosInstance';

export default function ChatRootRedirect() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [noConversations, setNoConversations] = useState(false);

  useEffect(() => {
    // Try to fetch the first conversation and redirect
    axiosInstance.get('/chat/conversations?page=0&size=1')
      .then(res => {
        const data = res.data;
        if (data && data.length > 0 && data[0].conversationId) {
          router.replace(`/chat/${data[0].conversationId}`);
        } else {
          setNoConversations(true);
        }
      })
      .catch(err => {
          console.error("Failed to fetch conversations for redirect", err);
          setNoConversations(true);
      })
      .finally(() => setLoading(false));
  }, [router]);

  if (loading) return null;
  if (noConversations) return <div className="min-h-screen flex items-center justify-center text-gray-400">No conversations yet. Start a new chat!</div>;
  return null;
}
