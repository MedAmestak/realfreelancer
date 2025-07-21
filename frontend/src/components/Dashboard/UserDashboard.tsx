'use client';

import React, { useState, useEffect, useCallback, useMemo } from 'react';
import { motion } from 'framer-motion';
import { useAuth } from '../../contexts/AuthContext';
import { 
  BarChart3, 
  TrendingUp, 
  MessageSquare, 
  FileText, 
  DollarSign, 
  Star,
  Clock,
  CheckCircle,
  AlertCircle,
  Users,
  Briefcase,
  Award,
  Search
} from 'lucide-react';

interface DashboardStats {
  projectsPosted: number;
  projectsCompleted: number;
  applicationsSubmitted: number;
  applicationsAccepted: number;
  averageRating: number;
  totalEarnings: number;
  responseTime: number;
  completionRate: number;
  // Remove unused fields or add if you use more
}

interface QuickStats {
  unreadMessages: number;
  pendingApplications: number;
  activeProjects: number;
  completedProjects: number;
}

interface RecentProject {
  id: number;
  title: string;
  status: string;
  createdAt: string;
}

interface RecentApplication {
  id: number;
  projectTitle: string;
  status: string;
  createdAt: string;
}

interface RecentActivity {
  recentProjects: RecentProject[];
  recentApplications: RecentApplication[];
}

interface StatCardProps {
  title: string;
  value: number | string;
  icon: React.ElementType;
  color: string;
  trend?: {
    value: number;
    isPositive: boolean;
  };
}

interface QuickActionCardProps {
  title: string;
  description: string;
  icon: React.ElementType;
  action: () => void;
  color: string;
}

interface ActivityItemProps {
  type: 'project' | 'application';
  title: string;
  time: string;
  status: string;
}

const defaultAnalytics: DashboardStats = {
  projectsPosted: 0,
  projectsCompleted: 0,
  applicationsSubmitted: 0,
  applicationsAccepted: 0,
  averageRating: 0,
  totalEarnings: 0,
  responseTime: 0,
  completionRate: 0,
};

const UserDashboard: React.FC = () => {
  const { user, loading: authLoading, getAuthToken } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [quickStats, setQuickStats] = useState<QuickStats | null>(null);
  const [recentActivity, setRecentActivity] = useState<RecentActivity | null>(null);
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchDashboardData = useCallback(async () => {
    if (!user) {
      setError('You must be logged in to view your dashboard.');
      setDashboardLoading(false);
      return;
    }
    
    setDashboardLoading(true);
    try {
      const token = getAuthToken();
      const response = await fetch('http://localhost:8080/api/dashboard/user', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setStats(data.analytics || null);
        setQuickStats(data.quickStats || null);
        setRecentActivity({
          recentProjects: data.recentProjects || [],
          recentApplications: data.recentApplications || []
        });
        setError('');
      } else if (response.status === 401) {
        setError('Session expired or unauthorized. Please log in again.');
      } else {
        setError('Failed to fetch dashboard data.');
      }
    } catch (err) {
      setError('A network error occurred while fetching dashboard data.');
    } finally {
      setDashboardLoading(false);
    }
  }, [user, getAuthToken]);

  useEffect(() => {
    if (!authLoading) {
      fetchDashboardData();
    }
  }, [authLoading, fetchDashboardData]);

  const handleNavigateToProjects = useCallback(() => {
    window.location.href = '/projects';
  }, []);

  const handleNavigateToNewProject = useCallback(() => {
    window.location.href = '/projects/new';
  }, []);

  const handleNavigateToChat = useCallback(() => {
    window.location.href = '/chat';
  }, []);

  // Memoized navigation handlers
  const navHandlers = useMemo(() => ({
    post: () => window.location.href = '/post',
    projects: handleNavigateToProjects,
    chat: handleNavigateToChat,
    browse: () => window.location.href = '/projects',
    search: () => window.location.href = '/search',
  }), [handleNavigateToProjects, handleNavigateToChat]);

  const StatCard = ({ title, value, icon: Icon, color, trend }: StatCardProps) => (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className={"bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-all duration-300"}
    >
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
          {trend && (
            <div className="flex items-center mt-2">
              <TrendingUp className={`w-4 h-4 ${trend.isPositive ? 'text-green-500' : 'text-red-500'}`} />
              <span className={`text-sm ml-1 ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
                {trend.isPositive ? '+' : ''}{trend.value}%
              </span>
            </div>
          )}
        </div>
        <div className={`p-3 rounded-lg ${color}`}>
          <Icon className="w-6 h-6 text-white" />
        </div>
      </div>
    </motion.div>
  );

  const QuickActionCard = ({ title, description, icon: Icon, action, color }: QuickActionCardProps) => (
    <motion.div
      whileHover={{ scale: 1.02 }}
      whileTap={{ scale: 0.98 }}
      className="bg-white rounded-xl p-6 shadow-sm border border-gray-100 cursor-pointer hover:shadow-md transition-all duration-300"
      onClick={action}
    >
      <div className={`w-12 h-12 ${color} rounded-lg flex items-center justify-center mb-4`}>
        <Icon className="w-6 h-6 text-white" />
      </div>
      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-gray-600 text-sm">{description}</p>
    </motion.div>
  );

  const ActivityItem = ({ type, title, time, status }: ActivityItemProps) => (
    <div className="flex items-center space-x-3 p-3 hover:bg-gray-50 rounded-lg transition-colors">
      <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
        type === 'project' ? 'bg-blue-100' : 'bg-green-100'
      }`}>
        {type === 'project' ? (
          <Briefcase className="w-4 h-4 text-blue-600" />
        ) : (
          <FileText className="w-4 h-4 text-green-600" />
        )}
      </div>
      <div className="flex-1">
        <p className="text-sm font-medium text-gray-900">{title}</p>
        <p className="text-xs text-gray-500">{time}</p>
      </div>
      <div className={`px-2 py-1 rounded-full text-xs font-medium ${
        status === 'active' ? 'bg-green-100 text-green-800' :
        status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
        'bg-gray-100 text-gray-800'
      }`}>
        {status}
      </div>
    </div>
  );

  // --- UI ---
  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!user || error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="bg-red-50 text-red-700 px-6 py-4 rounded-lg shadow">
          {error || 'You must be logged in to view the dashboard.'}
        </div>
      </div>
    );
  }

  if (dashboardLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  // Use analytics for stat cards
  const analytics: DashboardStats = stats ?? defaultAnalytics;

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-50 pb-16">
      {/* Hero Header */}
      <section className="bg-gradient-to-r from-blue-600 to-purple-600 text-white py-12 md:py-16 shadow-lg mb-10">
        <div className="max-w-5xl mx-auto px-4 flex flex-col md:flex-row items-center justify-between gap-6">
        <div>
            <h1 className="text-3xl md:text-4xl font-extrabold mb-2 drop-shadow-lg">Welcome back, {user.username}!</h1>
            <p className="text-lg md:text-xl text-blue-100 mb-4">Your freelance business at a glance.</p>
            <div className="flex gap-3 mt-4">
              <button
                onClick={navHandlers.post}
                className="bg-white/90 hover:bg-white text-blue-700 font-semibold px-5 py-2 rounded-lg shadow transition flex items-center gap-2"
              >
                <Briefcase className="w-5 h-5" /> Post Project
              </button>
              <button
                onClick={navHandlers.browse}
                className="bg-white/90 hover:bg-white text-purple-700 font-semibold px-5 py-2 rounded-lg shadow transition flex items-center gap-2"
              >
                <Search className="w-5 h-5" /> Browse Projects
              </button>
              <button
                onClick={navHandlers.chat}
                className="bg-white/90 hover:bg-white text-green-700 font-semibold px-5 py-2 rounded-lg shadow transition flex items-center gap-2"
              >
                <MessageSquare className="w-5 h-5" /> Messages
              </button>
            </div>
          </div>
          <div className="hidden md:block">
            <img src="/dashboard-hero.svg" alt="Dashboard" className="w-64 drop-shadow-xl" />
          </div>
        </div>
      </section>

      {/* Stats Section (analytics only) */}
      <section className="max-w-5xl mx-auto px-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6 mb-10">
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-blue-500">
            <Briefcase className="w-8 h-8 text-blue-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.projectsPosted ?? 0}</span>
            <span className="text-sm text-gray-500 mt-1">Projects Posted</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-green-500">
            <CheckCircle className="w-8 h-8 text-green-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.projectsCompleted ?? 0}</span>
            <span className="text-sm text-gray-500 mt-1">Projects Completed</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-yellow-400">
            <FileText className="w-8 h-8 text-yellow-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.applicationsSubmitted ?? 0}</span>
            <span className="text-sm text-gray-500 mt-1">Applications</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-purple-500">
            <Star className="w-8 h-8 text-purple-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.averageRating?.toFixed(1) ?? '0.0'}</span>
            <span className="text-sm text-gray-500 mt-1">Avg. Rating</span>
          </motion.div>
          </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6 mb-10">
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-indigo-500">
            <DollarSign className="w-8 h-8 text-indigo-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">${analytics.totalEarnings?.toLocaleString() ?? '0'}</span>
            <span className="text-sm text-gray-500 mt-1">Total Earnings</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-pink-500">
            <TrendingUp className="w-8 h-8 text-pink-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.completionRate ?? 0}%</span>
            <span className="text-sm text-gray-500 mt-1">Completion Rate</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-orange-500">
            <Clock className="w-8 h-8 text-orange-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.responseTime ?? 0}h</span>
            <span className="text-sm text-gray-500 mt-1">Response Time</span>
          </motion.div>
          <motion.div whileHover={{ scale: 1.03 }} className="bg-white rounded-2xl shadow p-6 flex flex-col items-center border-t-4 border-teal-500">
            <CheckCircle className="w-8 h-8 text-teal-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{analytics.applicationsAccepted ?? 0}</span>
            <span className="text-sm text-gray-500 mt-1">Applications Accepted</span>
          </motion.div>
        </div>
      </section>

      {/* Recent Activity Section */}
      <section className="max-w-5xl mx-auto px-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div>
            <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2"><Briefcase className="w-5 h-5 text-blue-500" /> Recent Projects</h2>
            <div className="bg-white rounded-2xl shadow p-4 space-y-3 min-h-[180px]">
              {recentActivity?.recentProjects && recentActivity.recentProjects.length > 0 ? (
                recentActivity.recentProjects.map((project: RecentProject) => (
                  <div key={project.id} className="flex justify-between items-center py-2 border-b last:border-b-0">
                    <div>
                      <div className="font-medium text-gray-800">{project.title}</div>
                      <div className="text-xs text-gray-400">{new Date(project.createdAt).toLocaleDateString()}</div>
                    </div>
                    <span className={`text-xs px-2 py-1 rounded font-semibold capitalize ${
                      project.status === 'OPEN' ? 'bg-green-100 text-green-800' :
                      project.status === 'IN_PROGRESS' ? 'bg-yellow-100 text-yellow-800' :
                      project.status === 'COMPLETED' ? 'bg-blue-100 text-blue-800' :
                      'bg-gray-100 text-gray-600'
                    }`}>
                      {project.status.replace('_', ' ').toLowerCase()}
                    </span>
                  </div>
                ))
              ) : (
                <div className="text-gray-400 text-sm">No recent projects</div>
              )}
            </div>
          </div>
          <div>
            <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center gap-2"><FileText className="w-5 h-5 text-green-500" /> Recent Applications</h2>
            <div className="bg-white rounded-2xl shadow p-4 space-y-3 min-h-[180px]">
              {recentActivity?.recentApplications && recentActivity.recentApplications.length > 0 ? (
                recentActivity.recentApplications.map((application: RecentApplication) => (
                  <div key={application.id} className="flex justify-between items-center py-2 border-b last:border-b-0">
                    <div>
                      <div className="font-medium text-gray-800">{application.projectTitle}</div>
                      <div className="text-xs text-gray-400">{new Date(application.createdAt).toLocaleDateString()}</div>
                    </div>
                    <span className={`text-xs px-2 py-1 rounded font-semibold capitalize ${
                      application.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                      application.status === 'ACCEPTED' ? 'bg-green-100 text-green-800' :
                      application.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-600'
                    }`}>
                      {application.status.replace('_', ' ').toLowerCase()}
                    </span>
                  </div>
                ))
              ) : (
                <div className="text-gray-400 text-sm">No recent applications</div>
              )}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default UserDashboard; 