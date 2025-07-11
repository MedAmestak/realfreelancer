'use client'

import { useState, useEffect } from 'react'
import Link from 'next/link'
import { motion, AnimatePresence } from 'framer-motion'
import { Menu, X, Bell, LogOut } from 'lucide-react'
import { useAuth } from '../src/contexts/AuthContext'
import UserMenu from './UserMenu'
import NotificationCenter from '../src/components/Notifications/NotificationCenter'
import { useRouter } from 'next/navigation';

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const { user, logout } = useAuth()
  const router = useRouter();

  const toggleMenu = () => setIsMenuOpen(!isMenuOpen)

  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-gradient-to-r from-primary-600 to-purple-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">RF</span>
            </div>
            <span className="text-xl font-bold text-gray-900">RealFreelancer</span>
          </Link>

          {/* Desktop Navigation */}


          {/* Auth Buttons */}
          <div className="hidden md:flex items-center space-x-4">
            {user ? (
              <div className="flex items-center space-x-4">
                <NotificationCenter />
                <UserMenu />
              </div>
            ) : (
              <div className="flex items-center space-x-4">
                <Link href="/login" className="flex items-center space-x-2 text-gray-600 hover:text-primary-600 transition-colors">
                  <span>Login</span>
                </Link>
                <Link href="/signup" className="btn-primary">
                  <span>Sign Up</span>
                </Link>
              </div>
            )}
          </div>

          {/* Mobile Menu Button */}
          <button
            onClick={toggleMenu}
            className="md:hidden p-2 rounded-md text-gray-600 hover:text-gray-900 hover:bg-gray-100"
          >
            {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>

        {/* Mobile Menu */}
        {isMenuOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            className="md:hidden py-4 border-t border-gray-200"
          >
            <nav className="flex flex-col space-y-4">
              <Link href="/" className="text-gray-600 hover:text-primary-600 transition-colors">
                Projects
              </Link>
              {user && (
                <>
                  <Link href="/post" className="text-gray-600 hover:text-primary-600 transition-colors">
                    Post Project
                  </Link>
                  <Link href="/profile" className="text-gray-600 hover:text-primary-600 transition-colors">
                    Profile
                  </Link>
                </>
              )}
              <div className="pt-4 border-t border-gray-200">
                {user ? (
                  <button
                    onClick={() => { logout(); router.push('/'); }}
                    className="flex items-center space-x-2 text-gray-600 hover:text-red-600 transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                    <span>Logout</span>
                  </button>
                ) : (
                  <div className="flex flex-col space-y-2">
                    <Link href="/login" className="flex items-center justify-center space-x-2 text-gray-600 hover:text-primary-600 transition-colors">
                      <span>Login</span>
                    </Link>
                    <Link href="/register" className="btn-primary inline-flex items-center justify-center">
                      <span>Sign Up</span>
                    </Link>
                  </div>
                )}
              </div>
            </nav>
          </motion.div>
        )}
      </div>
    </header>
  )
} 