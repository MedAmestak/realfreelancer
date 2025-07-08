'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { motion } from 'framer-motion'
import { Plus, Save, ArrowLeft } from 'lucide-react'
import Header from '../../components/Header'
import { useAuth } from '../../src/contexts/AuthContext'

export default function PostProjectPage() {
  const router = useRouter()
  const { user, getAuthToken } = useAuth()
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    requiredSkills: [] as string[],
    budget: 0,
    deadline: '',
    type: 'FREE' as 'FREE' | 'PAID'
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})

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

  const handleSkillToggle = (skill: string) => {
    setFormData(prev => ({
      ...prev,
      requiredSkills: prev.requiredSkills.includes(skill)
        ? prev.requiredSkills.filter(s => s !== skill)
        : [...prev.requiredSkills, skill]
    }))
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleBudgetChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = parseInt(e.target.value) || 0;
    setFormData(prev => ({ ...prev, budget: value }));
  };

  const isFormValid =
    formData.title.trim().length > 0 &&
    formData.description.trim().length >= 20 &&
    formData.deadline &&
    !loading;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setFieldErrors({})

    // Validate description length
    if (formData.description.length < 20) {
      setError('Description must be at least 20 characters long')
      setLoading(false)
      return
    }
    // Validate deadline
    if (!formData.deadline || isNaN(new Date(formData.deadline).getTime())) {
      setError('Please select a valid deadline date')
      setLoading(false)
      return
    }

    // Format deadline to ISO string
    const deadlineDate = new Date(formData.deadline)
    deadlineDate.setHours(23, 59, 59)

    try {
      const headers: Record<string, string> = {
        'Content-Type': 'application/json'
      }
      
      const token = getAuthToken?.();
      if (token) {
        headers.Authorization = `Bearer ${token}`;
      }

      const projectData = {
        ...formData,
        deadline: deadlineDate.toISOString()
      }

      const response = await fetch('http://localhost:8080/api/projects', {
        method: 'POST',
        headers,
        body: JSON.stringify(projectData)
      })

      if (response.ok) {
        router.push('/dashboard')
      } else {
        let errMsg = 'Bad Request';
        let fieldErrs: Record<string, string> = {};
        try {
          const data = await response.json();
          if (typeof data === 'object' && data !== null) {
            if (Array.isArray(data)) {
              errMsg = data.join(', ');
            } else {
              fieldErrs = data;
              errMsg = Object.values(data).join(' ');
            }
          } else if (typeof data === 'string') {
            errMsg = data;
          }
        } catch {}
        setError(errMsg);
        setFieldErrors(fieldErrs);
      }
    } catch (err) {
      setError('Network error. Please try again.');
    } finally {
      setLoading(false);
    }
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Header />
        <div className="container mx-auto px-4 py-8">
          <div className="text-center">
            <h1 className="text-2xl font-bold text-gray-900 mb-4">Authentication Required</h1>
            <p className="text-gray-600">Please log in to post a project.</p>
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
          {/* Page Header */}
          <div className="flex items-center gap-4 mb-8">
            <button
              onClick={() => router.back()}
              className="p-2 text-gray-600 hover:text-gray-900 transition-colors"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Post New Project</h1>
              <p className="text-gray-600">Create a new project and find the perfect freelancer</p>
            </div>
          </div>

          {/* Project Form */}
          <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-sm border border-gray-200 p-8">
            {/* Show general error */}
            {error && <div className="bg-red-100 text-red-700 p-3 rounded mb-4">{error}</div>}
            {/* Show field errors */}
            {Object.keys(fieldErrors).length > 0 && (
              <ul className="mb-4">
                {Object.entries(fieldErrors).map(([field, msg]) => (
                  <li key={field} className="text-red-600 text-sm">{field}: {msg}</li>
                ))}
              </ul>
            )}

            {/* Basic Information */}
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Basic Information</h2>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
                    Project Title *
                  </label>
                  <input
                    id="title"
                    type="text"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    required
                  />
                </div>

                <div>
                  <label htmlFor="type" className="block text-sm font-medium text-gray-700 mb-2">
                    Project Type *
                  </label>
                  <select
                    id="type"
                    name="type"
                    value={formData.type}
                    onChange={handleInputChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  >
                    <option value="FREE">Free Project</option>
                    <option value="PAID">Paid Project</option>
                  </select>
                </div>
              </div>

              <div className="mt-6">
                <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                  Project Description * (minimum 20 characters)
                </label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  rows={6}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  placeholder="Describe your project in detail (minimum 20 characters)..."
                  required
                  minLength={20}
                />
                <span className="text-sm text-gray-500 mt-1">
                  {formData.description.length}/2000 characters
                </span>
              </div>
            </div>

            {/* Project Details */}
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Project Details</h2>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label htmlFor="budget" className="block text-sm font-medium text-gray-700 mb-2">
                    Budget (USD)
                  </label>
                  <input
                    id="budget"
                    type="number"
                    name="budget"
                    value={formData.budget}
                    onChange={handleBudgetChange}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    min="0"
                  />
                </div>

                <div>
                  <label htmlFor="deadline" className="block text-sm font-medium text-gray-700 mb-2">
                    Deadline *
                  </label>
                  <input
                    id="deadline"
                    type="date"
                    value={formData.deadline}
                    onChange={(e) => setFormData(prev => ({ ...prev, deadline: e.target.value }))}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                    required
                  />
                </div>
              </div>
            </div>

            {/* Required Skills */}
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Required Skills</h2>
              <p className="text-sm text-gray-600 mb-4">Select the skills required for this project</p>
              
              <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                {commonSkills.map((skill) => (
                  <label
                    key={skill}
                    className="flex items-center space-x-2 cursor-pointer hover:bg-gray-50 p-2 rounded"
                  >
                    <input
                      type="checkbox"
                      checked={formData.requiredSkills.includes(skill)}
                      onChange={() => handleSkillToggle(skill)}
                      className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                    />
                    <span className="text-sm text-gray-700">{skill}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Submit Button */}
            <div className="flex justify-end">
              <button
                type="submit"
                disabled={!isFormValid}
                className="btn-primary flex items-center space-x-2 disabled:opacity-50"
              >
                {loading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Creating...</span>
                  </>
                ) : (
                  <>
                    <Plus className="w-4 h-4" />
                    <span>Create Project</span>
                  </>
                )}
              </button>
            </div>
          </form>
        </motion.div>
      </div>
    </div>
  )
} 