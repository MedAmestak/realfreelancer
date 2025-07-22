'use client';

import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Bell, X, MessageSquare, FileText, Star, Award, AlertCircle } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import SockJS from 'sockjs-client';
import axiosInstance from '../../utils/axiosInstance';

interface Notification {
  id: number;
  type: 'PROJECT_APPLICATION' | 'NEW_MESSAGE' | 'REVIEW_RECEIVED' | 'BADGE_EARNED' | 'SYSTEM_ANNOUNCEMENT';
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  link?: string;
  metadata?: {
    projectId?: number;
    messageId?: number;
    reviewId?: number;
    badgeId?: number;
  };
}

interface NotificationCenterProps {}

const NotificationCenter: React.FC<NotificationCenterProps> = () => {
  const { getAuthToken } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'unread' | 'settings'>('all');
  const wsRef = useRef<WebSocket | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const listRef = useRef<HTMLDivElement>(null);

  const handleNewNotification = (notification: Notification) => {
    setNotifications(prev => {
      if (prev.some(n => n.id === notification.id)) return prev;
      return [notification, ...prev];
    });
    if (!notification.isRead) setUnreadCount(prev => prev + 1);
  };

  const setupWebSocket = () => {
    try {
      const token = getAuthToken();
      if (!token) {
        console.error('No authentication token found');
        return;
      }
      const ws = new SockJS(`http://localhost:8080/ws?token=${token}`);
      wsRef.current = ws;

      ws.onopen = () => {
        console.log('WebSocket connected');
      };

      ws.onmessage = (event) => {
        const notification = JSON.parse(event.data);
        handleNewNotification(notification);
      };

      ws.onerror = (error) => {
        console.error('WebSocket error: Connection failed');
      };

      ws.onclose = () => {
        console.log('WebSocket disconnected');
      };
    } catch (error) {
      console.error('WebSocket error: Connection failed');
    }
  };

  // Helper to normalize notification API response
  function normalizeNotificationsResponse(data: unknown): Notification[] {
    if (Array.isArray(data)) return data as Notification[];
    if (
      typeof data === 'object' &&
      data !== null &&
      'content' in data &&
      Array.isArray((data as { content: unknown }).content)
    ) {
      return (data as { content: Notification[] }).content;
    }
    return [];
  }

  // Remove infinite scroll and custom scroll handler
  // Only fetch once per dropdown open, fetch up to 50 notifications
  const fetchNotifications = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get("/notifications?page=0&size=50");
      if (response.data) {
        const fetched = normalizeNotificationsResponse(response.data);
        setNotifications(fetched);
        setUnreadCount(fetched.filter(n => !n.isRead).length);
      }
    } catch (error) {
      console.error('Error fetching notifications: Network error', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchUnreadCount = async () => {
    try {
      const response = await axiosInstance.get('/notifications/unread-count');
      if (response.data) {
        setUnreadCount(response.data.unreadCount || 0);
      }
    } catch (error) {
      console.error('Error fetching unread count: Network error', error);
    }
  };

  // On mount, only set up WebSocket and unread count
  useEffect(() => {
    fetchUnreadCount();
    setupWebSocket();
    return () => {
      if (wsRef.current) wsRef.current.close();
    };
  }, []);

  // When dropdown is opened, fetch notifications
  useEffect(() => {
    if (isOpen) {
      fetchNotifications();
    }
  }, [isOpen]);

  const markAsRead = async (notificationId: number) => {
    try {
      await axiosInstance.put(`/notifications/${notificationId}/read`);
        setNotifications(prev => prev.map(notif => 
          notif.id === notificationId ? { ...notif, isRead: true } : notif
        ));
        setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read: Network error', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      await axiosInstance.put('/notifications/mark-all-read');
        setNotifications(prev => prev.map(notif => ({ ...notif, isRead: true })));
        setUnreadCount(0);
    } catch (error) {
      console.error('Error marking all notifications as read: Network error', error);
    }
  };

  const deleteNotification = async (notificationId: number) => {
    try {
      await axiosInstance.delete(`/notifications/${notificationId}`);
        setNotifications(prev => prev.filter(notif => notif.id !== notificationId));
        const notification = notifications.find(n => n.id === notificationId);
        if (notification && !notification.isRead) {
          setUnreadCount(prev => Math.max(0, prev - 1));
      }
    } catch (error) {
      console.error('Error deleting notification: Network error', error);
    }
  };

  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'PROJECT_APPLICATION':
        return <FileText className="w-5 h-5 text-blue-600" />;
      case 'NEW_MESSAGE':
        return <MessageSquare className="w-5 h-5 text-green-600" />;
      case 'REVIEW_RECEIVED':
        return <Star className="w-5 h-5 text-yellow-600" />;
      case 'BADGE_EARNED':
        return <Award className="w-5 h-5 text-purple-600" />;
      case 'SYSTEM_ANNOUNCEMENT':
        return <AlertCircle className="w-5 h-5 text-red-600" />;
      default:
        return <Bell className="w-5 h-5 text-gray-600" />;
    }
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return date.toLocaleDateString();
  };

  const handleTabChange = (tab: 'all' | 'unread') => {
    setActiveTab(tab);
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    if (!isOpen) return;
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const filteredNotifications = notifications.filter(notification => {
    if (activeTab === 'unread') return !notification.isRead;
    return true;
  });

  // When notification panel is opened, mark all as read and reset unread count
  useEffect(() => {
    if (isOpen) {
      if (unreadCount > 0) {
        markAllAsRead();
        setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
        setUnreadCount(0);
      }
    }
  }, [isOpen]);

  return (
    <div className="relative">
      {/* Notification Bell */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 text-gray-600 hover:text-gray-900 transition-colors"
        aria-label="Open notifications"
      >
        <Bell className="w-6 h-6" />
        {unreadCount > 0 && (
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            className="absolute top-0 right-0 -translate-y-1/2 translate-x-1/2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center shadow-lg border-2 border-white z-10"
            style={{ pointerEvents: 'none' }}
          >
            {unreadCount > 99 ? '99+' : unreadCount}
          </motion.div>
        )}
      </button>

      {/* Notification Panel */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            ref={dropdownRef}
            initial={{ opacity: 0, y: -10, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -10, scale: 0.95 }}
            className="absolute right-0 mt-2 w-[420px] bg-white rounded-xl shadow-2xl border border-gray-200 z-50"
            style={{ boxShadow: '0 8px 32px 0 rgba(31, 38, 135, 0.15)' }}
          >
            {/* Header */}
            <div className="flex items-center justify-between p-5 border-b border-gray-100">
              <h3 className="text-xl font-bold text-gray-900">Notifications</h3>
              <button
                onClick={() => setIsOpen(false)}
                className="text-gray-400 hover:text-gray-600"
                aria-label="Close notifications"
              >
                <X className="w-6 h-6" />
              </button>
            </div>
            {/* Content: Only notifications list */}
            <div
              ref={listRef}
              className="h-[400px] overflow-y-auto px-2 py-2"
              style={{ minWidth: 380 }}
            >
              {loading && notifications.length === 0 ? (
                <div className="flex items-center justify-center py-8">
                  <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                </div>
              ) : notifications.length > 0 ? (
                <AnimatePresence>
                  {notifications.map((notification, idx) => (
                    <motion.div
                      key={notification.id}
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: 20 }}
                      transition={{ duration: 0.2 }}
                      className={`group flex items-center gap-4 bg-white rounded-xl shadow-sm border border-gray-100 px-4 py-3 mb-2 cursor-pointer relative transition-all duration-150
                        ${!notification.isRead ? 'before:content-[""] before:absolute before:left-0 before:top-0 before:bottom-0 before:w-1.5 before:rounded-l-xl before:bg-blue-500/80 bg-blue-50' : ''}
                        hover:shadow-md hover:bg-gray-50`}
                      style={{ minHeight: 64 }}
                      onClick={() => {
                        if (notification.link) {
                          window.open(notification.link, '_blank');
                        }
                      }}
                    >
                      <div className="flex-shrink-0 flex items-center justify-center h-10 w-10">
                        {getNotificationIcon(notification.type)}
                      </div>
                      <div className="flex-1 min-w-0 flex flex-col justify-center">
                        <div className="flex items-center justify-between">
                          <span className="font-semibold text-gray-900 text-base truncate max-w-[220px]">{notification.title}</span>
                          <span className="text-xs text-gray-400 ml-2 whitespace-nowrap">{formatTime(notification.createdAt)}</span>
                        </div>
                        <span className="text-sm text-gray-600 mt-0.5 truncate max-w-[260px]">{notification.message}</span>
                      </div>
                    </motion.div>
                  ))}
                  {loading && (
                    <div className="flex items-center justify-center py-4">
                      <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
                    </div>
                  )}
                </AnimatePresence>
              ) : (
                <div className="flex flex-col items-center justify-center py-12">
                  <Bell className="w-14 h-14 text-gray-300 mb-4 animate-float" />
                  <p className="text-base text-gray-500 font-medium">You&apos;re all caught up!</p>
                  <p className="text-sm text-gray-400 mt-1">No notifications yet. We&apos;ll keep you posted.</p>
                </div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default NotificationCenter; 