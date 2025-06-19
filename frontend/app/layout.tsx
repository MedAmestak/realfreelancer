import type { Metadata, Viewport } from 'next'
import { Inter } from 'next/font/google'
import './globals.css'
import { AuthProvider } from '../src/contexts/AuthContext'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'RealFreelancer - Free Freelance Platform',
  description: 'Connect with developers, showcase your skills, and build amazing projects together. Perfect for building your portfolio and gaining real-world experience.',
  keywords: 'freelance, projects, developers, portfolio, free',
  authors: [{ name: 'RealFreelancer Team' }],
  openGraph: {
    title: 'RealFreelancer - Free Freelance Platform',
    description: 'Connect with developers, showcase your skills, and build amazing projects together.',
    type: 'website',
  },
}

export const viewport: Viewport = {
  width: 'device-width',
  initialScale: 1,
  maximumScale: 1,
  userScalable: false,
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  )
} 