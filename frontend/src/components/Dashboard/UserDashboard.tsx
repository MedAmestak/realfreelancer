'use client';

import React, { useState, useEffect, useCallback } from 'react';
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
  totalProjects: number;
  totalApplications: number;
  totalReviews: number;
  averageRating: number;
  completionRate: number;
  acceptanceRate: number;
  status: string;
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

  return (
    <div className="min-h-screen bg-gray-50 py-10">
      <div className="max-w-4xl mx-auto space-y-10">
        {/* Header */}
        <div>
          <h1 className="text-2xl font-extrabold text-gray-900 mb-2">Welcome back, {user.username}</h1>
          <p className="text-xm text-gray-500">Your freelance overview at a glance.</p>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-6">
          <div className="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-md transition">
            <Briefcase className="w-8 h-8 text-blue-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{stats?.totalProjects ?? 0}</span>
            <span className="text-sm text-gray-500 mt-1">Projects</span>
          </div>
          <div className="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-md transition">
            <FileText className="w-8 h-8 text-green-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{stats?.totalApplications ?? 0}</span>
            <span className="text-sm text-gray-500 mt-1">Applications</span>
          </div>
          <div className="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-md transition">
            <Star className="w-8 h-8 text-yellow-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{stats?.averageRating?.toFixed(1) ?? '0.0'}</span>
            <span className="text-sm text-gray-500 mt-1">Avg. Rating</span>
          </div>
          <div className="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-md transition">
            <CheckCircle className="w-8 h-8 text-purple-500 mb-2" />
            <span className="text-3xl font-bold text-gray-900">{stats?.completionRate ?? 0}%</span>
            <span className="text-sm text-gray-500 mt-1">Completion</span>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="w-full my-10">
  <div className="flex justify-between w-full gap-4">
    <button 
      onClick={() => (window.location.href = '/post')}
      className="flex items-center gap-2 bg-green-600 hover:bg-purple text-purple-500 px-6 py-2 rounded-xl font-semibold shadow transition"
    >
      <Briefcase className="w-5 h-5" /> Post Project
    </button>

    <button
      onClick={() => (window.location.href = '/projects')}
      className="flex items-center gap-2 bg-green-600 hover:bg-purple-700 text-purple-500 px-6 py-3 rounded-xl font-semibold shadow transition"
    >
      <Search className="w-5 h-5" /> Browse Projects
    </button>

    <button
      onClick={() => (window.location.href = '/chat')}
      className="flex items-center gap-2 bg-purple-600 hover:bg-purple-700 text-blue px-6 py-3 rounded-xl font-semibold shadow transition"
    >
      <MessageSquare className="w-5 h-5" /> View Messages
    </button>
  </div>
</div>


        {/* Recent Activity */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div>
            <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Projects</h2>
            <div className="bg-white rounded-2xl shadow p-4 space-y-3">
              {recentActivity?.recentProjects && recentActivity.recentProjects.length > 0 ? (
                recentActivity.recentProjects.map((project: RecentProject) => (
                  <div key={project.id} className="flex justify-between items-center py-2 border-b last:border-b-0">
                    <div>
                      <div className="font-medium text-gray-800">{project.title}</div>
                      <div className="text-xs text-gray-400">{new Date(project.createdAt).toLocaleDateString()}</div>
                    </div>
                    <span className="text-xs px-2 py-1 rounded bg-gray-100 text-gray-600 capitalize">{project.status}</span>
                  </div>
                ))
              ) : (
                <div className="text-gray-400 text-sm">No recent projects</div>
              )}
            </div>
          </div>
          <div>
            <h2 className="text-xl font-bold text-gray-900 mb-4">Recent Applications</h2>
            <div className="bg-white rounded-2xl shadow p-4 space-y-3">
              {recentActivity?.recentApplications && recentActivity.recentApplications.length > 0 ? (
                recentActivity.recentApplications.map((application: RecentApplication) => (
                  <div key={application.id} className="flex justify-between items-center py-2 border-b last:border-b-0">
                    <div>
                      <div className="font-medium text-gray-800">{application.projectTitle}</div>
                      <div className="text-xs text-gray-400">{new Date(application.createdAt).toLocaleDateString()}</div>
                    </div>
                    <span className="text-xs px-2 py-1 rounded bg-gray-100 text-gray-600 capitalize">{application.status}</span>
                  </div>
                ))
              ) : (
                <div className="text-gray-400 text-sm">No recent applications</div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserDashboard; 