'use client'

import { motion } from 'framer-motion'
import Link from 'next/link'
import { Calendar, DollarSign, Users, Eye, MessageSquare, Star } from 'lucide-react'
import { Project } from '@/types/project'

interface ProjectCardProps {
  project: Project
}

export default function ProjectCard({ project }: ProjectCardProps) {
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })
  }

  const formatBudget = (budget: number) => {
    if (budget === 0) return 'Free'
    return `${budget.toLocaleString()}`
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'OPEN':
        return 'badge-success'
      case 'IN_PROGRESS':
        return 'badge-warning'
      case 'COMPLETED':
        return 'badge-primary'
      case 'CANCELLED':
        return 'badge-danger'
      default:
        return 'badge-secondary'
    }
  }

  return (
    <motion.div
      whileHover={{ y: -4 }}
      className="card h-full hover:shadow-lg transition-all duration-200 cursor-pointer flex flex-col">
      <div className="flex flex-col flex-grow p-4 space-y-4">
        {/* Growable Content Area */}
        <div className="flex-grow space-y-3">
            {/* Header */}
            <div className="flex items-start justify-between">
              <h3 className="text-lg font-semibold text-gray-900 line-clamp-2">
                {project.title}
              </h3>
              {project.isFeatured && (
                <span className="badge badge-primary text-xs">Featured</span>
              )}
            </div>

            {/* Description */}
            <p className="text-gray-500 text-sm overflow-hidden text-ellipsis" style={{
              display: '-webkit-box',
              WebkitBoxOrient: 'vertical',
              WebkitLineClamp: 2
            }}>
              {project.description}
            </p>

            {/* Skills */}
            <div className="flex flex-wrap gap-2 ">
              {project.requiredSkills.slice(0, 3).map((skill) => (
                <span
                  key={skill}
                  className="badge badge-secondary text-xs badge-primary"
                >
                  {skill}
                </span>
              ))}
              {project.requiredSkills.length > 3 && (
                <span className="badge badge-secondary text-xs">
                  +{project.requiredSkills.length - 3} more
                </span>
              )}
            </div>
        </div>

        {/* Footer Area */}
        <div className="space-y-3">
            {/* Project Info */}
            <div className="flex items-center justify-between text-sm text-gray-500">
              <div className="flex items-center space-x-4">
                <div className="flex items-center space-x-1">
                  <DollarSign className="w-3 h-3" />
                  <span>{formatBudget(project.budget)}</span>
                </div>
                <div className="flex items-center space-x-1">
                  <Calendar className="w-3 h-3" />
                  <span>{formatDate(project.deadline)}</span>
                </div>
              </div>
              <span className={`badge ${getStatusColor(project.status)}`}>
                {project.status}
              </span>
            </div>

            {/* Stats */}
            <div className="flex items-center justify-between pt-2 border-t border-gray-100">
              <div className="flex items-center space-x-4 text-xs text-gray-500">
                <div className="flex items-center space-x-1">
                  <Eye className="w-3 h-3" />
                  <span>{project.viewCount}</span>
                </div>
                <div className="flex items-center space-x-1">
                  <MessageSquare className="w-3 h-3" />
                  <span>{project.applicationCount}</span>
                </div>
              </div>
              <div className="flex items-center space-x-1 text-xs text-gray-500">
                <Users className="w-3 h-3" />
                <span>{project.client.username}</span>
              </div>
            </div>
        </div>
      </div>
    </motion.div>
  )
} 