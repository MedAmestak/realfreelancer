'use client';

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
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
}

interface QuickStats {
  unreadMessages: number;
  pendingApplications: number;
  activeProjects: number;
  completedProjects: number;
}

interface RecentActivity {
  recentProjects: any[];
  recentApplications: any[];
}

const UserDashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [quickStats, setQuickStats] = useState<QuickStats | null>(null);
  const [recentActivity, setRecentActivity] = useState<RecentActivity | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      // Fetch user dashboard data
      const response = await fetch('/api/dashboard/user', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (response.ok) {
        const data = await response.json();
        setStats(data.analytics);
        setQuickStats(data.quickStats);
        setRecentActivity({
          recentProjects: data.recentProjects || [],
          recentApplications: data.recentApplications || []
        });
      }
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const StatCard = ({ title, value, icon: Icon, color, trend }: any) => (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className={`bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-all duration-300`}
    >
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-gray-600">{title}</p>
          <p className="text-2xl font-bold text-gray-900 mt-1">{value}</p>
          {trend && (
            <div className="flex items-center mt-2">
              <TrendingUp className={`w-4 h-4 ${trend > 0 ? 'text-green-500' : 'text-red-500'}`} />
              <span className={`text-sm ml-1 ${trend > 0 ? 'text-green-600' : 'text-red-600'}`}>
                {trend > 0 ? '+' : ''}{trend}%
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

  const QuickActionCard = ({ title, description, icon: Icon, action, color }: any) => (
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

  const ActivityItem = ({ type, title, time, status }: any) => (
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

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-2">Welcome back! Here's what's happening with your projects.</p>
        </motion.div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatCard
            title="Total Projects"
            value={stats?.totalProjects || 0}
            icon={Briefcase}
            color="bg-blue-500"
            trend={12}
          />
          <StatCard
            title="Applications"
            value={stats?.totalApplications || 0}
            icon={FileText}
            color="bg-green-500"
            trend={8}
          />
          <StatCard
            title="Average Rating"
            value={stats?.averageRating?.toFixed(1) || '0.0'}
            icon={Star}
            color="bg-yellow-500"
            trend={5}
          />
          <StatCard
            title="Completion Rate"
            value={`${stats?.completionRate || 0}%`}
            icon={CheckCircle}
            color="bg-purple-500"
            trend={-2}
          />
        </div>

        {/* Quick Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center">
              <MessageSquare className="w-5 h-5 text-blue-600 mr-2" />
              <span className="text-sm font-medium text-gray-600">Unread Messages</span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-1">{quickStats?.unreadMessages || 0}</p>
          </div>
          <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center">
              <Clock className="w-5 h-5 text-yellow-600 mr-2" />
              <span className="text-sm font-medium text-gray-600">Pending Applications</span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-1">{quickStats?.pendingApplications || 0}</p>
          </div>
          <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center">
              <TrendingUp className="w-5 h-5 text-green-600 mr-2" />
              <span className="text-sm font-medium text-gray-600">Active Projects</span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-1">{quickStats?.activeProjects || 0}</p>
          </div>
          <div className="bg-white rounded-xl p-4 shadow-sm border border-gray-100">
            <div className="flex items-center">
              <Award className="w-5 h-5 text-purple-600 mr-2" />
              <span className="text-sm font-medium text-gray-600">Completed</span>
            </div>
            <p className="text-2xl font-bold text-gray-900 mt-1">{quickStats?.completedProjects || 0}</p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Quick Actions */}
          <div className="lg:col-span-1">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h2>
            <div className="space-y-4">
              <QuickActionCard
                title="Post New Project"
                description="Create a new project and find the perfect freelancer"
                icon={Briefcase}
                color="bg-blue-500"
                action={() => window.location.href = '/projects/new'}
              />
              <QuickActionCard
                title="Browse Projects"
                description="Find projects that match your skills"
                icon={Search}
                color="bg-green-500"
                action={() => window.location.href = '/projects'}
              />
              <QuickActionCard
                title="View Messages"
                description="Check your conversations and messages"
                icon={MessageSquare}
                color="bg-purple-500"
                action={() => window.location.href = '/chat'}
              />
            </div>
          </div>

          {/* Recent Activity */}
          <div className="lg:col-span-2">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">Recent Activity</h2>
            <div className="bg-white rounded-xl shadow-sm border border-gray-100">
              <div className="p-6">
                <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Projects</h3>
                <div className="space-y-2">
                  {recentActivity?.recentProjects && recentActivity.recentProjects.length > 0 ? (
                    recentActivity.recentProjects.map((project: any, index: number) => (
                      <ActivityItem
                        key={index}
                        type="project"
                        title={project.title}
                        time={new Date(project.createdAt).toLocaleDateString()}
                        status={project.status?.toLowerCase()}
                      />
                    ))
                  ) : (
                    <p className="text-gray-500 text-sm">No recent projects</p>
                  )}
                </div>
              </div>
              
              <div className="border-t border-gray-100 p-6">
                <h3 className="text-lg font-medium text-gray-900 mb-4">Recent Applications</h3>
                <div className="space-y-2">
                  {recentActivity?.recentApplications && recentActivity.recentApplications.length > 0 ? (
                    recentActivity.recentApplications.map((application: any, index: number) => (
                      <ActivityItem
                        key={index}
                        type="application"
                        title={`Application for ${application.project?.title || 'Project'}`}
                        time={new Date(application.createdAt).toLocaleDateString()}
                        status={application.status?.toLowerCase()}
                      />
                    ))
                  ) : (
                    <p className="text-gray-500 text-sm">No recent applications</p>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserDashboard; 