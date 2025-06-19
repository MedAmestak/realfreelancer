'use client'

import { useState, useEffect } from 'react'
import { motion } from 'framer-motion'
import { Search, Filter, Briefcase } from 'lucide-react'
import Header from '../../components/Header'
import ProjectCard from '../../components/ProjectCard'
import FilterBar from '../../components/FilterBar'

export default function ProjectsPage() {
  const [projects, setProjects] = useState<any[]>([])
  const [filteredProjects, setFilteredProjects] = useState<any[]>([])
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
        console.error('Failed to fetch projects:', response.status)
      }
    } catch (error) {
      console.error('Error fetching projects:', error)
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

  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      
      <div className="container mx-auto px-4 py-8">
        {/* Page Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mb-8"
        >
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Browse Projects</h1>
          <p className="text-gray-600">Find the perfect project to showcase your skills and build your portfolio.</p>
        </motion.div>

        {/* Search Bar */}
        <div className="mb-6">
          <div className="relative max-w-2xl">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              placeholder="Search projects by title or description..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-3 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent"
            />
          </div>
        </div>

        <div className="flex flex-col lg:flex-row gap-8">
          {/* Filters Sidebar */}
          <div className="lg:w-1/4">
            <FilterBar 
              selectedSkills={selectedSkills}
              onSkillsChange={setSelectedSkills}
            />
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

            {loading ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {[...Array(6)].map((_, i) => (
                  <div key={i} className="bg-white rounded-lg shadow-sm p-6 animate-pulse">
                    <div className="h-4 bg-gray-200 rounded mb-4"></div>
                    <div className="h-3 bg-gray-200 rounded mb-2"></div>
                    <div className="h-3 bg-gray-200 rounded mb-4"></div>
                    <div className="flex gap-2">
                      <div className="h-6 bg-gray-200 rounded w-16"></div>
                      <div className="h-6 bg-gray-200 rounded w-20"></div>
                    </div>
                  </div>
                ))}
              </div>
            ) : filteredProjects.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {filteredProjects.map((project, index) => (
                  <motion.div
                    key={project.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.4, delay: index * 0.1 }}
                  >
                    <ProjectCard project={project} />
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
    </div>
  )
} 