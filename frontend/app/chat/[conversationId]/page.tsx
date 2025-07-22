'use client';
import React, { useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import ConversationList from '../../../src/components/Chat/ConversationList';
import ChatWindow from '../../../src/components/Chat/ChatWindow';
import Header from '../../../components/Header';
import axiosInstance from '../../../src/utils/axiosInstance';

export default function ConversationPage() {
  const params = useParams();
  const router = useRouter();
  const conversationId = params?.conversationId && !isNaN(Number(params.conversationId)) ? Number(params.conversationId) : null;
  const [selectedConversation, setSelectedConversation] = useState<number | null>(conversationId);
  const [refreshConversations, setRefreshConversations] = useState(0);

  // If conversationId is invalid, fetch conversations and redirect to first one
  React.useEffect(() => {
    if (conversationId === null) {
      axiosInstance.get('/chat/conversations?page=0&size=20')
        .then(res => {
          const data = res.data;
          if (data && data.length > 0 && data[0].conversationId) {
            router.replace(`/chat/${data[0].conversationId}`);
          }
        }).catch(err => {
            console.error("Failed to fetch conversations for redirect", err);
        });
    }
  }, [conversationId, router]);

  // Keep selectedConversation in sync with URL
  React.useEffect(() => {
    setSelectedConversation(conversationId);
  }, [conversationId]);

  const handleMessagesRead = () => {
    setRefreshConversations((prev) => prev + 1);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Header />
      <div className="flex flex-1 max-w-5xl mx-auto w-full border rounded-xl shadow bg-white overflow-hidden mt-8">
        {/* Sidebar: Conversation List */}
        <aside className="w-1/3 border-r bg-gray-50 p-4 overflow-y-auto">
          <ConversationList 
            selectedConversation={selectedConversation}
            onSelect={(id) => router.push(`/chat/${id}`)}
            key={refreshConversations}
            refreshKey={refreshConversations}
          />
        </aside>
        {/* Main Chat Area */}
        <main className="flex-1 flex flex-col">
          <ChatWindow conversationId={selectedConversation} onMessagesRead={handleMessagesRead} />
        </main>
      </div>
    </div>
  );
} 