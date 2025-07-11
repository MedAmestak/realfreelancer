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

  // The filter content (used in both modal and sidebar)
  const filterContent = (
    <div className="w-full max-w-xs mx-auto md:max-w-none md:w-full">
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
        <div className="space-y-2 max-h-48 overflow-y-auto pr-1">
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
          <label htmlFor="free-projects" className="flex items-center space-x-2 cursor-pointer">
            <input
              id="free-projects"
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">Free Projects</span>
          </label>
          <label htmlFor="paid-projects" className="flex items-center space-x-2 cursor-pointer">
            <input
              id="paid-projects"
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
          <label htmlFor="budget-0-100" className="flex items-center space-x-2 cursor-pointer">
            <input
              id="budget-0-100"
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">$0 - $100</span>
          </label>
          <label htmlFor="budget-100-500" className="flex items-center space-x-2 cursor-pointer">
            <input
              id="budget-100-500"
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">$100 - $500</span>
          </label>
          <label htmlFor="budget-500-plus" className="flex items-center space-x-2 cursor-pointer">
            <input
              id="budget-500-plus"
              type="checkbox"
              className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
            />
            <span className="text-sm text-gray-700">$500+</span>
          </label>
        </div>
      </div>
    </div>
  )

  return (
    <>
      {/* Mobile: Sticky Filters Button */}
      <div className="fixed bottom-4 left-0 right-0 z-40 flex justify-center md:hidden">
        <button
          onClick={() => setIsOpen(true)}
          className="bg-blue-600 text-white px-6 py-3 rounded-full shadow-lg flex items-center gap-2 text-base font-semibold hover:bg-blue-700 transition-all"
        >
          <Filter className="w-5 h-5" /> Filters
        </button>
      </div>

      {/* Mobile: Modal Drawer */}
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-end md:hidden">
          {/* Overlay */}
          <div
            className="fixed inset-0 bg-black bg-opacity-40 transition-opacity"
            onClick={() => setIsOpen(false)}
          />
          {/* Drawer */}
          <div className="w-full bg-white rounded-t-2xl shadow-2xl p-6 max-h-[80vh] overflow-y-auto animate-slide-up">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900 flex items-center">
                <Filter className="w-5 h-5 mr-2" /> Filters
              </h3>
              <button
                onClick={() => setIsOpen(false)}
                className="text-gray-400 hover:text-gray-600"
                aria-label="Close filters"
              >
                <X className="w-6 h-6" />
              </button>
            </div>
            {filterContent}
            <div className="mt-6 flex gap-2">
              <button
                onClick={() => setIsOpen(false)}
                className="flex-1 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors"
              >
                Apply Filters
              </button>
              <button
                onClick={clearFilters}
                className="flex-1 bg-gray-100 text-gray-700 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors"
              >
                Clear
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Desktop: Sidebar */}
      <div className="hidden md:block">
        <div className="bg-white rounded-lg shadow-sm p-6 sticky top-8 max-h-[80vh] overflow-y-auto">
          {filterContent}
        </div>
      </div>
    </>
  )
} 