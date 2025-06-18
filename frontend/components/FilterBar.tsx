'use client'

import { useState } from 'react'
import { Filter, X } from 'lucide-react'

interface FilterBarProps {
  selectedSkills: string[]
  onSkillsChange: (skills: string[]) => void
}

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

export default function FilterBar({ selectedSkills, onSkillsChange }: FilterBarProps) {
  const [isOpen, setIsOpen] = useState(false)

  const toggleSkill = (skill: string) => {
    if (selectedSkills.includes(skill)) {
      onSkillsChange(selectedSkills.filter(s => s !== skill))
    } else {
      onSkillsChange([...selectedSkills, skill])
    }
  }

  const clearFilters = () => {
    onSkillsChange([])
  }

  return (
    <div className="card">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900 flex items-center">
          <Filter className="w-5 h-5 mr-2" />
          Filters
        </h3>
        {selectedSkills.length > 0 && (
          <button
            onClick={clearFilters}
            className="text-sm text-gray-500 hover:text-gray-700"
          >
            Clear all
          </button>
        )}
      </div>

      {/* Selected Skills */}
      {selectedSkills.length > 0 && (
        <div className="mb-4">
          <h4 className="text-sm font-medium text-gray-700 mb-2">Selected Skills:</h4>
          <div className="flex flex-wrap gap-2">
            {selectedSkills.map((skill) => (
              <span
                key={skill}
                className="badge badge-primary text-xs flex items-center"
              >
                {skill}
                <button
                  onClick={() => toggleSkill(skill)}
                  className="ml-1 hover:text-red-600"
                >
                  <X className="w-3 h-3" />
                </button>
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Skills Filter */}
      <div>
        <h4 className="text-sm font-medium text-gray-700 mb-3">Skills:</h4>
        <div className="space-y-2 max-h-64 overflow-y-auto">
          {commonSkills.map((skill) => (
            <label
              key={skill}
              className="flex items-center space-x-2 cursor-pointer hover:bg-gray-50 p-2 rounded"
            >
              <input
                type="checkbox"
                checked={selectedSkills.includes(skill)}
                onChange={() => toggleSkill(skill)}
                className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
              />
              <span className="text-sm text-gray-700">{skill}</span>
            </label>
          ))}
        </div>
      </div>

      {/* Project Type Filter */}
      <div className="mt-6 pt-4 border-t border-gray-200">
        <h4 className="text-sm font-medium text-gray-700 mb-3">Project Type:</h4>
        <div className="space-y-2">
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">Free Projects</span>
          </label>
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">Paid Projects</span>
          </label>
        </div>
      </div>

      {/* Budget Range */}
      <div className="mt-6 pt-4 border-t border-gray-200">
        <h4 className="text-sm font-medium text-gray-700 mb-3">Budget Range:</h4>
        <div className="space-y-2">
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">$0 - $100</span>
          </label>
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">$100 - $500</span>
          </label>
          <label className="flex items-center space-x-2 cursor-pointer">
            <input
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">$500+</span>
          </label>
        </div>
      </div>
    </div>
  )
} 