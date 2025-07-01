'use client';
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function ChatRootRedirect() {
  const router = useRouter();
  useEffect(() => {
    // Optionally, redirect to the first conversation or dashboard
    router.replace('/dashboard');
  }, [router]);
  return null;
}
