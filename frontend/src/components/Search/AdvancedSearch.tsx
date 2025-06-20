'use client';

import React, { useState, useEffect, useCallback, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Search, 
  Filter, 
  X, 
  MapPin, 
  DollarSign, 
  Clock, 
  Star,
  TrendingUp,
  Sparkles
} from 'lucide-react';

interface SearchFilters {
  query: string;
  skills: string[];
  location: string;
  minBudget: number;
  maxBudget: number;
  experienceLevel: string;
  projectType: string;
  sortBy: 'createdAt' | 'budget' | 'rating' | 'deadline';
  sortOrder: 'asc' | 'desc';
}

interface SearchSuggestion {
  text: string;
  type: 'skill' | 'keyword';
}

interface SearchResult {
  id: number;
  title: string;
  description: string;
  budget: number;
  deadline: string;
  requiredSkills: string[];
  type: 'FREE' | 'PAID';
  status: 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  client: {
    id: number;
    username: string;
    rating: number;
  };
  createdAt: string;
  matchScore?: number;
}

interface TrendingSkill {
  skill: string;
  count: number;
  trend: number;
}

const AdvancedSearch: React.FC = () => {
  const [filters, setFilters] = useState<SearchFilters>({
    query: '',
    skills: [],
    location: '',
    minBudget: 0,
    maxBudget: 0,
    experienceLevel: '',
    projectType: '',
    sortBy: 'createdAt',
    sortOrder: 'desc'
  });

  const [suggestions, setSuggestions] = useState<SearchSuggestion[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [trendingSkills, setTrendingSkills] = useState<TrendingSkill[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchResults, setSearchResults] = useState<SearchResult[]>([]);
  const searchRef = useRef<HTMLDivElement>(null);

  const popularSkills = [
    'React', 'Node.js', 'Python', 'Java', 'JavaScript', 'TypeScript',
    'Docker', 'AWS', 'MongoDB', 'PostgreSQL', 'Spring Boot', 'Vue.js'
  ];

  const experienceLevels = ['Beginner', 'Intermediate', 'Expert'];
  const projectTypes = ['Fixed Price', 'Hourly', 'Free'];

  useEffect(() => {
    fetchTrendingSkills();
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (filters.query.length > 2) {
      fetchSuggestions(filters.query);
    } else {
      setSuggestions([]);
    }
  }, [filters.query]);

  const handleClickOutside = (event: MouseEvent) => {
    if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
      setShowSuggestions(false);
    }
  };

  const fetchTrendingSkills = async () => {
    try {
      const response = await fetch('/api/search/trending-skills?limit=8');
      if (response.ok) {
        const data = await response.json();
        setTrendingSkills(data);
      }
    } catch (error) {
      console.error('Error fetching trending skills: Network error');
    }
  };

  const fetchSuggestions = async (query: string) => {
    try {
      const response = await fetch(`/api/search/suggestions?query=${encodeURIComponent(query)}`);
      if (response.ok) {
        const data = await response.json();
        setSuggestions(data.map((suggestion: string) => ({
          text: suggestion,
          type: 'skill' as const
        })));
      }
    } catch (error) {
      console.error('Error fetching suggestions: Network error');
    }
  };

  const performSearch = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== '' && value !== null && value !== undefined) {
          if (Array.isArray(value)) {
            value.forEach(v => params.append(key, v));
          } else {
            params.append(key, value.toString());
          }
        }
      });

      const response = await fetch(`/api/search/advanced?${params.toString()}`);
      if (response.ok) {
        const data = await response.json();
        setSearchResults(data.content || []);
      }
    } catch (error) {
      console.error('Error performing search: Network error');
    } finally {
      setLoading(false);
    }
  };

  const handleSkillToggle = (skill: string) => {
    setFilters(prev => ({
      ...prev,
      skills: prev.skills.includes(skill)
        ? prev.skills.filter(s => s !== skill)
        : [...prev.skills, skill]
    }));
  };

  const handleSuggestionClick = (suggestion: SearchSuggestion) => {
    if (suggestion.type === 'skill') {
      handleSkillToggle(suggestion.text);
    } else {
      setFilters(prev => ({ ...prev, query: suggestion.text }));
    }
    setShowSuggestions(false);
  };

  const clearFilters = () => {
    setFilters({
      query: '',
      skills: [],
      location: '',
      minBudget: 0,
      maxBudget: 0,
      experienceLevel: '',
      projectType: '',
      sortBy: 'createdAt',
      sortOrder: 'desc'
    });
  };

  const handleSkillClick = useCallback((skillName: string) => {
    handleSkillToggle(skillName);
  }, [handleSkillToggle]);

  const handleSearchClick = useCallback(() => {
    performSearch();
  }, [performSearch]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFilters(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleNumberInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({ ...prev, [e.target.name]: Number(e.target.value) }));
  };

  const FilterChip = ({ label, onRemove }: { label: string; onRemove: () => void }) => (
    <motion.div
      initial={{ opacity: 0, scale: 0.8 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.8 }}
      className="inline-flex items-center bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium mr-2 mb-2"
    >
      {label}
      <button
        onClick={onRemove}
        className="ml-2 hover:bg-blue-200 rounded-full p-1"
      >
        <X className="w-3 h-3" />
      </button>
    </motion.div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-8"
        >
          <h1 className="text-4xl font-bold text-gray-900 mb-4">Find Your Perfect Project</h1>
          <p className="text-xl text-gray-600">Advanced search with smart filters and recommendations</p>
        </motion.div>

        {/* Search Bar */}
        <div className="relative mb-8" ref={searchRef}>
          <div className="relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
            <input
              type="text"
              name="query"
              placeholder="Search for projects, skills, or keywords..."
              value={filters.query}
              onChange={handleInputChange}
              onFocus={() => setShowSuggestions(true)}
              className="w-full pl-12 pr-4 py-4 text-lg border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            <button
              onClick={handleSearchClick}
              className="absolute right-2 top-1/2 transform -translate-y-1/2 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Search
            </button>
          </div>

          {/* Search Suggestions */}
          <AnimatePresence>
            {showSuggestions && suggestions.length > 0 && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="absolute top-full left-0 right-0 bg-white border border-gray-200 rounded-xl shadow-lg z-50 mt-2"
              >
                {suggestions.map((suggestion, index) => (
                  <button
                    key={index}
                    onClick={() => handleSuggestionClick(suggestion)}
                    className="w-full text-left px-4 py-3 hover:bg-gray-50 flex items-center"
                  >
                    <Search className="w-4 h-4 text-gray-400 mr-3" />
                    {suggestion.text}
                  </button>
                ))}
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* Active Filters */}
        <AnimatePresence>
          {(filters.skills.length > 0 || filters.location || filters.experienceLevel || filters.projectType) && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="mb-6"
            >
              <div className="flex items-center flex-wrap">
                <span className="text-sm font-medium text-gray-700 mr-3">Active filters:</span>
                {filters.skills.map(skill => (
                  <FilterChip
                    key={skill}
                    label={skill}
                    onRemove={() => handleSkillToggle(skill)}
                  />
                ))}
                {filters.location && (
                  <FilterChip
                    label={filters.location}
                    onRemove={() => setFilters(prev => ({ ...prev, location: '' }))}
                  />
                )}
                {filters.experienceLevel && (
                  <FilterChip
                    label={filters.experienceLevel}
                    onRemove={() => setFilters(prev => ({ ...prev, experienceLevel: '' }))}
                  />
                )}
                {filters.projectType && (
                  <FilterChip
                    label={filters.projectType}
                    onRemove={() => setFilters(prev => ({ ...prev, projectType: '' }))}
                  />
                )}
                <button
                  onClick={clearFilters}
                  className="text-sm text-blue-600 hover:text-blue-800 font-medium"
                >
                  Clear all
                </button>
              </div>
            </motion.div>
          )}
        </AnimatePresence>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-8">
          {/* Filters Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 sticky top-8">
              <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <Filter className="w-5 h-5 mr-2" />
                Filters
              </h3>

              {/* Skills */}
              <div className="mb-6">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Skills</h4>
                <div className="space-y-2">
                  {popularSkills.map(skill => (
                    <label key={skill} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={filters.skills.includes(skill)}
                        onChange={() => handleSkillToggle(skill)}
                        className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                      />
                      <span className="ml-2 text-sm text-gray-700">{skill}</span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Budget Range */}
              <div className="mb-6">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Budget Range</h4>
                <div className="space-y-3">
                  <div>
                    <label className="text-xs text-gray-500">Min Budget</label>
                    <input
                      type="number"
                      name="minBudget"
                      value={filters.minBudget}
                      onChange={handleNumberInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="text-xs text-gray-500">Max Budget</label>
                    <input
                      type="number"
                      name="maxBudget"
                      value={filters.maxBudget}
                      onChange={handleNumberInputChange}
                      className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
              </div>

              {/* Experience Level */}
              <div className="mb-6">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Experience Level</h4>
                <select
                  name="experienceLevel"
                  value={filters.experienceLevel}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Any Level</option>
                  {experienceLevels.map(level => (
                    <option key={level} value={level}>{level}</option>
                  ))}
                </select>
              </div>

              {/* Project Type */}
              <div className="mb-6">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Project Type</h4>
                <select
                  name="projectType"
                  value={filters.projectType}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="">Any Type</option>
                  {projectTypes.map(type => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>

              {/* Sort Options */}
              <div className="mb-6">
                <h4 className="text-sm font-medium text-gray-700 mb-3">Sort By</h4>
                <select
                  name="sortBy"
                  value={filters.sortBy}
                  onChange={handleInputChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="createdAt">Date Posted</option>
                  <option value="budget">Budget</option>
                  <option value="deadline">Deadline</option>
                  <option value="viewCount">Views</option>
                </select>
              </div>

              <button
                onClick={handleSearchClick}
                disabled={loading}
                className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {loading ? 'Searching...' : 'Apply Filters'}
              </button>
            </div>
          </div>

          {/* Search Results */}
          <div className="lg:col-span-3">
            {/* Trending Skills */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
                <TrendingUp className="w-5 h-5 mr-2" />
                Trending Skills
              </h3>
              <div className="flex flex-wrap gap-2">
                {trendingSkills.map((skill) => (
                  <motion.div
                    key={skill.skill}
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    onClick={() => handleSkillClick(skill.skill)}
                    className="px-3 py-1 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-full text-sm font-medium hover:shadow-md transition-all"
                  >
                    {skill.skill}
                  </motion.div>
                ))}
              </div>
            </div>

            {/* Results */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Search Results ({searchResults.length})
              </h3>
              
              {loading ? (
                <div className="flex items-center justify-center py-12">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                </div>
              ) : searchResults.length > 0 ? (
                <div className="space-y-4">
                  {searchResults.map((result, index) => (
                    <motion.div
                      key={result.id}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: index * 0.1 }}
                      className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
                    >
                      <h4 className="text-lg font-semibold text-gray-900 mb-2">{result.title}</h4>
                      <p className="text-gray-600 mb-3">{result.description}</p>
                      <div className="flex items-center justify-between text-sm text-gray-500">
                        <div className="flex items-center space-x-4">
                          <span className="flex items-center">
                            <DollarSign className="w-4 h-4 mr-1" />
                            ${result.budget}
                          </span>
                          <span className="flex items-center">
                            <Clock className="w-4 h-4 mr-1" />
                            {result.deadline}
                          </span>
                          <span className="flex items-center">
                            <Star className="w-4 h-4 mr-1" />
                            {result.client?.rating || 'New'}
                          </span>
                        </div>
                        <button className="text-blue-600 hover:text-blue-800 font-medium">
                          View Details
                        </button>
                      </div>
                    </motion.div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12">
                  <Sparkles className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                  <p className="text-gray-500">No projects found. Try adjusting your filters.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdvancedSearch; 