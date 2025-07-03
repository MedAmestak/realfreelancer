'use client';
import React, { useState } from 'react';
import { useParams } from 'next/navigation';
import ConversationList from '../../../src/components/Chat/ConversationList';
import ChatWindow from '../../../src/components/Chat/ChatWindow';
import Header from '../../../components/Header';

export default function ConversationPage() {
  const params = useParams();
  const conversationId = params?.conversationId ? Number(params.conversationId) : null;
  const [selectedConversation, setSelectedConversation] = useState<number | null>(conversationId);

  // Keep selectedConversation in sync with URL
  React.useEffect(() => {
    setSelectedConversation(conversationId);
  }, [conversationId]);

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Header />
      <div className="flex flex-1 max-w-5xl mx-auto w-full border rounded-xl shadow bg-white overflow-hidden mt-8">
        {/* Sidebar: Conversation List */}
        <aside className="w-1/3 border-r bg-gray-50 p-4 overflow-y-auto">
          <ConversationList 
            selectedConversation={selectedConversation} 
            onSelect={setSelectedConversation} 
          />
        </aside>
        {/* Main Chat Area */}
        <main className="flex-1 flex flex-col">
          <ChatWindow conversationId={selectedConversation} />
        </main>
      </div>
    </div>
  );
} 