'use client';

import React, { useState, useEffect, useRef, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Bell, 
  X, 
  Check, 
  Trash2, 
  Settings, 
  MessageSquare, 
  FileText, 
  Star,
  Award,
  AlertCircle,
  ExternalLink
} from 'lucide-react';

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
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'all' | 'unread' | 'settings'>('all');
  const wsRef = useRef<WebSocket | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchNotifications();
    fetchUnreadCount();
    setupWebSocket();
    
    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
    };
  }, []);

  const setupWebSocket = () => {
    try {
      const ws = new WebSocket('ws://localhost:8080/ws');
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

  const handleNewNotification = (notification: Notification) => {
    setNotifications(prev => [notification, ...prev]);
    setUnreadCount(prev => prev + 1);
  };

  const fetchNotifications = async () => {
    try {
      const response = await fetch('/api/notifications', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setNotifications(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching notifications: Network error');
    }
  };

  const fetchUnreadCount = async () => {
    try {
      const response = await fetch('/api/notifications/unread-count', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setUnreadCount(data.unreadCount || 0);
      }
    } catch (error) {
      console.error('Error fetching unread count: Network error');
    }
  };

  const markAsRead = async (notificationId: number) => {
    try {
      const response = await fetch(`/api/notifications/${notificationId}/read`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        setNotifications(prev => prev.map(notif => 
          notif.id === notificationId ? { ...notif, isRead: true } : notif
        ));
        setUnreadCount(prev => Math.max(0, prev - 1));
      }
    } catch (error) {
      console.error('Error marking notification as read: Network error');
    }
  };

  const markAllAsRead = async () => {
    try {
      const response = await fetch('/api/notifications/mark-all-read', {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        setNotifications(prev => prev.map(notif => ({ ...notif, isRead: true })));
        setUnreadCount(0);
      }
    } catch (error) {
      console.error('Error marking all notifications as read: Network error');
    }
  };

  const deleteNotification = async (notificationId: number) => {
    try {
      const response = await fetch(`/api/notifications/${notificationId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        setNotifications(prev => prev.filter(notif => notif.id !== notificationId));
        const notification = notifications.find(n => n.id === notificationId);
        if (notification && !notification.isRead) {
          setUnreadCount(prev => Math.max(0, prev - 1));
        }
      }
    } catch (error) {
      console.error('Error deleting notification: Network error');
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

  const handleClickOutside = useCallback((event: MouseEvent) => {
    if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
      setIsOpen(false);
    }
  }, []);

  const filteredNotifications = notifications.filter(notification => {
    if (activeTab === 'unread') return !notification.isRead;
    return true;
  });

  return (
    <div className="relative">
      {/* Notification Bell */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="relative p-2 text-gray-600 hover:text-gray-900 transition-colors"
      >
        <Bell className="w-6 h-6" />
        {unreadCount > 0 && (
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center"
          >
            {unreadCount > 99 ? '99+' : unreadCount}
          </motion.div>
        )}
      </button>

      {/* Notification Panel */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: -10, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -10, scale: 0.95 }}
            className="absolute right-0 mt-2 w-96 bg-white rounded-xl shadow-xl border border-gray-200 z-50"
          >
            {/* Header */}
            <div className="flex items-center justify-between p-4 border-b border-gray-100">
              <h3 className="text-lg font-semibold text-gray-900">Notifications</h3>
              <div className="flex items-center space-x-2">
                {unreadCount > 0 && (
                  <button
                    onClick={markAllAsRead}
                    className="text-sm text-blue-600 hover:text-blue-800"
                  >
                    Mark all read
                  </button>
                )}
                <button
                  onClick={() => setIsOpen(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Tabs */}
            <div className="flex border-b border-gray-100">
              <button
                onClick={() => setActiveTab('all')}
                className={`flex-1 py-3 text-sm font-medium ${
                  activeTab === 'all' 
                    ? 'text-blue-600 border-b-2 border-blue-600' 
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                All
              </button>
              <button
                onClick={() => setActiveTab('unread')}
                className={`flex-1 py-3 text-sm font-medium ${
                  activeTab === 'unread' 
                    ? 'text-blue-600 border-b-2 border-blue-600' 
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                Unread ({unreadCount})
              </button>
              <button
                onClick={() => setActiveTab('settings')}
                className={`flex-1 py-3 text-sm font-medium ${
                  activeTab === 'settings' 
                    ? 'text-blue-600 border-b-2 border-blue-600' 
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                <Settings className="w-4 h-4 inline mr-1" />
                Settings
              </button>
            </div>

            {/* Content */}
            <div className="max-h-96 overflow-y-auto">
              {activeTab === 'settings' ? (
                <div className="p-4">
                  <h4 className="text-sm font-medium text-gray-900 mb-4">Notification Preferences</h4>
                  <p className="text-sm text-gray-600">Settings will be available soon.</p>
                </div>
              ) : (
                <div>
                  {loading ? (
                    <div className="flex items-center justify-center py-8">
                      <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
                    </div>
                  ) : filteredNotifications.length > 0 ? (
                    filteredNotifications.map((notification) => (
                      <motion.div
                        key={notification.id}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        className={`p-4 border-b border-gray-100 hover:bg-gray-50 transition-colors ${
                          !notification.isRead ? 'bg-blue-50' : ''
                        }`}
                      >
                        <div className="flex items-start space-x-3">
                          <div className="flex-shrink-0 mt-1">
                            {getNotificationIcon(notification.type)}
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center justify-between">
                              <p className="text-sm font-medium text-gray-900">
                                {notification.title}
                              </p>
                              <div className="flex items-center space-x-1">
                                <span className="text-xs text-gray-500">
                                  {formatTime(notification.createdAt)}
                                </span>
                                {!notification.isRead && (
                                  <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                                )}
                              </div>
                            </div>
                            <p className="text-sm text-gray-600 mt-1">
                              {notification.message}
                            </p>
                            <div className="flex items-center space-x-2 mt-2">
                              {!notification.isRead && (
                                <button
                                  onClick={() => markAsRead(notification.id)}
                                  className="text-xs text-blue-600 hover:text-blue-800 flex items-center"
                                >
                                  <Check className="w-3 h-3 mr-1" />
                                  Mark as read
                                </button>
                              )}
                              {notification.link && (
                                <button
                                  onClick={() => {
                                    if (notification.link) {
                                      window.location.href = notification.link;
                                    }
                                  }}
                                  className="text-xs text-blue-600 hover:text-blue-800 flex items-center"
                                >
                                  <ExternalLink className="w-3 h-3 mr-1" />
                                  View Details
                                </button>
                              )}
                              <button
                                onClick={() => deleteNotification(notification.id)}
                                className="text-xs text-red-600 hover:text-red-800 flex items-center"
                              >
                                <Trash2 className="w-3 h-3 mr-1" />
                                Delete
                              </button>
                            </div>
                          </div>
                        </div>
                      </motion.div>
                    ))
                  ) : (
                    <div className="text-center py-8">
                      <Bell className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-sm text-gray-500">
                        {activeTab === 'unread' ? 'No unread notifications' : 'No notifications yet'}
                      </p>
                    </div>
                  )}
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