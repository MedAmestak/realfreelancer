'use client'

import React, { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { User, Edit, Save, X, Star, Award, Briefcase, MessageSquare } from 'lucide-react'
import Header from '../../components/Header'
import { useAuth } from '../../src/contexts/AuthContext'
import { User as AuthUser } from '../../src/contexts/AuthContext'

interface Profile {
  id: number;
  username: string;
  email: string;
  bio: string;
  githubLink: string;
  skills: string[];
  reputationPoints: number;
  isVerified: boolean;
  avatarUrl?: string;
  createdAt: string;
  stats: {
    totalProjects: number;
    completedProjects: number;
    averageRating: number;
    totalReviews: number;
  };
}

interface FormData {
  username: string;
  email: string;
  bio: string;
  githubLink: string;
  skills: string[];
}

export default function ProfilePage() {
  const { user, getAuthToken, setUser } = useAuth() // ⬅️ Added setUser here
  const [profile, setProfile] = useState<Profile | null>(null)
  const [loading, setLoading] = useState(true)
  const [editing, setEditing] = useState(false)
  const [formData, setFormData] = useState<FormData>({
    username: '',
    email: '',
    bio: '',
    githubLink: '',
    skills: []
  })
  const [error, setError] = useState<string | null>(null)

  const commonSkills = [
    'JavaScript', 'TypeScript', 'React', 'Vue', 'Angular', 'Node.js',
    'Python', 'Java', 'C#', 'PHP', 'Ruby', 'Go', 'Rust',
    'HTML', 'CSS', 'Sass', 'Tailwind CSS', 'Bootstrap',
    'MongoDB', 'PostgreSQL', 'MySQL', 'Redis',
    'Docker', 'Kubernetes', 'AWS', 'Azure', 'Google Cloud',
    'Git', 'GitHub', 'CI/CD', 'Testing', 'DevOps',
    'UI/UX', 'Design', 'Mobile', 'iOS', 'Android',
    'Machine Learning', 'AI', 'Data Science', 'Blockchain'
  ]

  const fetchProfile = async () => {
    setLoading(true);
    try {
      const token = getAuthToken();
      if (!token) {
        setError('You are not logged in.');
        setLoading(false);
        return;
      }
      const response = await fetch('http://localhost:8080/api/auth/profile', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (response.ok) {
        const data: Profile = await response.json();
        setProfile(data)
        setFormData({
          username: data.username || '',
          email: data.email || '',
          bio: data.bio || '',
          githubLink: data.githubLink || '',
          skills: data.skills || []
        })
      }
    } catch (error) {
      console.error('Error fetching profile: Network error')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (user) {
      fetchProfile()
    }
  }, [user])

  const handleSkillToggle = (skill: string) => {
    setFormData(prev => ({
      ...prev,
      skills: prev.skills.includes(skill)
        ? prev.skills.filter(s => s !== skill)
        : [...prev.skills, skill]
    }))
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = async () => {
    try {
      const token = getAuthToken();
      if (!token) {
        setError('Authentication error.');
        setLoading(false);
        return;
      }

      const response = await fetch('http://localhost:8080/api/auth/profile', {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const updatedProfile = await response.json(); 

        await fetchProfile();
        setEditing(false);

        // ⬇️ Update user in AuthContext so UserMenu reflects new username
        setUser((prev: AuthUser | null) => (
          prev ? { ...prev, username: updatedProfile.username } : prev
        ));
      }
    } catch (error) {
      console.error('Error updating profile: Network error')
    }
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="container mx-auto px-4 py-8">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Authentication Required</h1>
            <p className="text-gray-600">Please log in to view your profile.</p>
          </div>
        </div>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="container mx-auto px-4 py-8">
          <div className="flex items-center justify-center">
            <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600"></div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      
      <div className="container mx-auto px-4 py-8">
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="max-w-4xl mx-auto"
        >
          {/* Profile Header */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8 mb-8">
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center space-x-4">
                <div className="w-20 h-20 bg-gradient-to-r from-primary-600 to-purple-600 rounded-full flex items-center justify-center">
                  <User className="w-10 h-10 text-white" />
                </div>
                <div>
                  <h1 className="text-3xl font-bold text-gray-900">{profile?.username}</h1>
                  <p className="text-gray-600">{profile?.email}</p>
                </div>
              </div>
              <button
                onClick={() => setEditing(!editing)}
                className="btn-primary flex items-center space-x-2"
              >
                {editing ? (
                  <>
                    <X className="w-4 h-4" />
                    <span>Cancel</span>
                  </>
                ) : (
                  <>
                    <Edit className="w-4 h-4" />
                    <span>Edit Profile</span>
                  </>
                )}
              </button>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <Briefcase className="w-6 h-6 mx-auto text-blue-600 mb-2" />
                <div className="text-2xl font-bold text-gray-900">
                  {profile?.stats?.totalProjects ?? 0}
                </div>
                <div className="text-sm text-gray-600">Projects</div>
              </div>
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <Star className="w-6 h-6 mx-auto text-yellow-600 mb-2" />
                <div className="text-2xl font-bold text-gray-900">{profile?.stats.averageRating?.toFixed(1) || '0.0'}</div>
                <div className="text-sm text-gray-600">Rating</div>
              </div>
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <Award className="w-6 h-6 mx-auto text-purple-600 mb-2" />
                <div className="text-2xl font-bold text-gray-900">{profile?.reputationPoints || 0}</div>
                <div className="text-sm text-gray-600">Points</div>
              </div>
              <div className="text-center p-4 bg-gray-50 rounded-lg">
                <MessageSquare className="w-6 h-6 mx-auto text-green-600 mb-2" />
                <div className="text-2xl font-bold text-gray-900">{profile?.stats.totalReviews || 0}</div>
                <div className="text-sm text-gray-600">Reviews</div>
              </div>
            </div>
          </div>

          {/* Profile Details */}
          <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">Profile Information</h2>

            {editing ? (
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Username</label>
                  <input
                    type="text"
                    name="username"
                    value={formData.username}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Email</label>
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Bio</label>
                  <textarea
                    name="bio"
                    value={formData.bio}
                    onChange={handleInputChange}
                    rows={4}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    placeholder="Tell us about yourself..."
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">GitHub Link</label>
                  <input
                    type="url"
                    name="githubLink"
                    value={formData.githubLink}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    placeholder="https://github.com/username"
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-4">Skills</label>
                  <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                    {commonSkills.map((skill) => (
                      <label
                        key={skill}
                        className="flex items-center space-x-2 cursor-pointer hover:bg-gray-50 p-2 rounded"
                      >
                        <input
                          type="checkbox"
                          checked={formData.skills.includes(skill)}
                          onChange={() => handleSkillToggle(skill)}
                          className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                        />
                        <span className="text-sm text-gray-700">{skill}</span>
                      </label>
                    ))}
                  </div>
                </div>

                <div className="flex justify-end space-x-4">
                  <button
                    onClick={() => setEditing(false)}
                    className="btn-secondary"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleSave}
                    className="btn-primary flex items-center space-x-2"
                  >
                    <Save className="w-4 h-4" />
                    <span>Save Changes</span>
                  </button>
                </div>
              </div>
            ) : (
              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">Bio</h3>
                  <p className="text-gray-600">{profile?.bio || 'No bio added yet.'}</p>
                </div>

                {profile?.githubLink && (
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">GitHub</h3>
                    <a
                      href={profile.githubLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-primary-600 hover:text-primary-700 underline"
                    >
                      {profile.githubLink}
                    </a>
                  </div>
                )}

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Skills</h3>
                  {profile?.skills && profile.skills.length > 0 ? (
                    <div className="flex flex-wrap gap-2">
                      {profile.skills.map((skill: string) => (
                        <span
                          key={skill}
                          className="badge badge-primary"
                        >
                          {skill}
                        </span>
                      ))}
                    </div>
                  ) : (
                    <p className="text-gray-600">No skills added yet.</p>
                  )}
                </div>
              </div>
            )}
          </div>
        </motion.div>
      </div>
    </div>
  )
} 