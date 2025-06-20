'use client'

import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { Search, Filter, Star, Users, Briefcase, Award } from 'lucide-react'
import Header from '../components/Header'

interface Project {
  id: number;
  title: string;
  description: string;
  requiredSkills: string[];
  budget: number;
  deadline: string;
  status: 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  type: 'FREE' | 'PAID';
  client: {
    id: number;
    username: string;
    email: string;
  };
  freelancer?: {
    id: number;
    username: string;
    email: string;
  };
  attachmentUrl?: string;
  isFeatured: boolean;
  viewCount: number;
  applicationCount: number;
  createdAt: string;
  updatedAt: string;
}

interface Stat {
  icon: React.ElementType;
  label: string;
  value: number;
}

export default function HomePage() {
  const [projects, setProjects] = useState<Project[]>([])
  const [filteredProjects, setFilteredProjects] = useState<Project[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedSkills, setSelectedSkills] = useState<string[]>([])

  useEffect(() => {
    fetchProjects()
  }, [])

  useEffect(() => {
    filterProjects()
  }, [projects, searchTerm, selectedSkills])

  const fetchProjects = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/projects')
      if (response.ok) {
        const data = await response.json()
        setProjects(data.content || data)
      } else {
        console.error('Failed to fetch projects: Server error')
      }
    } catch (error) {
      console.error('Error fetching projects: Network error')
    } finally {
      setLoading(false)
    }
  }

  const filterProjects = () => {
    let filtered = projects

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(project =>
        project.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        project.description.toLowerCase().includes(searchTerm.toLowerCase())
      )
    }

    // Filter by skills
    if (selectedSkills.length > 0) {
      filtered = filtered.filter(project =>
        selectedSkills.some(skill =>
          project.requiredSkills.includes(skill)
        )
      )
    }

    setFilteredProjects(filtered)
  }

  const stats = [
    { icon: Briefcase, label: 'Active Projects', value: projects.filter(p => p.status === 'OPEN').length },
    { icon: Users, label: 'Freelancers', value: 150 },
    { icon: Star, label: 'Completed Projects', value: 89 },
    { icon: Award, label: 'Total Badges', value: 234 },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-blue-600 to-purple-600 text-white py-20">
        <div className="container mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6 }}
            className="text-center"
          >
            <h1 className="text-4xl md:text-6xl font-bold mb-6">
              RealFreelancer
            </h1>
            <p className="text-xl md:text-2xl mb-8 text-blue-100">
              Free freelance platform for portfolio-building projects
            </p>
            <p className="text-lg text-blue-200 mb-12 max-w-2xl mx-auto">
              Connect with developers, showcase your skills, and build amazing projects together. 
              Perfect for building your portfolio and gaining real-world experience.
            </p>
            
            {/* Search Bar */}
            <div className="max-w-2xl mx-auto">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Search projects by title or description..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 rounded-lg text-gray-900 focus:outline-none focus:ring-2 focus:ring-white"
                />
              </div>
            </div>
          </motion.div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-12 bg-white">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {stats.map((stat, index) => (
              <motion.div
                key={stat.label}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, delay: index * 0.1 }}
                className="text-center"
              >
                <stat.icon className="w-8 h-8 mx-auto mb-2 text-blue-600" />
                <div className="text-2xl font-bold text-gray-900">{stat.value}</div>
                <div className="text-sm text-gray-600">{stat.label}</div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Main Content */}
      <section className="py-12">
        <div className="container mx-auto px-4">
          <div className="flex flex-col lg:flex-row gap-8">
            {/* Filters Sidebar */}
            <div className="lg:w-1/4">
              <div className="bg-white rounded-lg shadow-sm p-6">
                <h3 className="text-lg font-semibold text-gray-900 mb-4">Filter by Skills</h3>
                <div className="space-y-2">
                  {['React', 'Node.js', 'Python', 'Java', 'JavaScript', 'TypeScript'].map(skill => (
                    <label key={skill} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={selectedSkills.includes(skill)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setSelectedSkills([...selectedSkills, skill])
                          } else {
                            setSelectedSkills(selectedSkills.filter(s => s !== skill))
                          }
                        }}
                        className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                      />
                      <span className="ml-2 text-sm text-gray-700">{skill}</span>
                    </label>
                  ))}
                </div>
              </div>
            </div>

            {/* Projects Grid */}
            <div className="lg:w-3/4">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">
                  Available Projects ({filteredProjects.length})
                </h2>
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <Filter className="w-4 h-4" />
                  <span>Filtered</span>
                </div>
              </div>

              {loading && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                  {[...Array(6)].map((_, i) => (
                    <div key={`skeleton-project-${i}`} className="bg-white rounded-lg shadow-md p-6 animate-pulse">
                      <div className="h-4 bg-gray-200 rounded w-3/4 mb-4"></div>
                      <div className="h-3 bg-gray-200 rounded w-full mb-2"></div>
                      <div className="h-3 bg-gray-200 rounded w-3/4 mb-4"></div>
                      <div className="flex gap-2">
                        <div className="h-6 bg-gray-200 rounded w-16"></div>
                        <div className="h-6 bg-gray-200 rounded w-20"></div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              {!loading && filteredProjects.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                  {filteredProjects.map((project, index) => (
                    <motion.div
                      key={project.id}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.4, delay: index * 0.1 }}
                      className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow"
                    >
                      <h3 className="text-lg font-semibold text-gray-900 mb-2">{project.title}</h3>
                      <p className="text-gray-600 text-sm mb-4 line-clamp-3">{project.description}</p>
                      <div className="flex items-center justify-between text-sm text-gray-500 mb-3">
                        <span>${project.budget}</span>
                        <span>{project.status}</span>
                      </div>
                      <div className="flex flex-wrap gap-1">
                        {project.requiredSkills?.slice(0, 3).map((skill: string) => (
                          <span key={skill} className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded">
                            {skill}
                          </span>
                        ))}
                      </div>
                    </motion.div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12">
                  <Briefcase className="w-16 h-16 mx-auto text-gray-400 mb-4" />
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">
                    No projects found
                  </h3>
                  <p className="text-gray-600">
                    Try adjusting your search criteria or check back later for new projects.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </section>
    </div>
  )
} 