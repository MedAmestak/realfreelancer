'use client';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

export default function ChatRootRedirect() {
  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [noConversations, setNoConversations] = useState(false);

  useEffect(() => {
    // Try to fetch the first conversation and redirect
    fetch('http://localhost:8080/api/chat/conversations?page=0&size=1', {
      headers: { 'Authorization': typeof window !== 'undefined' ? `Bearer ${localStorage.getItem('token')}` : '' }
    })
      .then(res => res.ok ? res.json() : [])
      .then((data) => {
        if (data && data.length > 0 && data[0].conversationId) {
          router.replace(`/chat/${data[0].conversationId}`);
        } else {
          setNoConversations(true);
        }
      })
      .finally(() => setLoading(false));
  }, [router]);

  if (loading) return null;
  if (noConversations) return <div className="min-h-screen flex items-center justify-center text-gray-400">No conversations yet. Start a new chat!</div>;
  return null;
}
