export interface Project {
  id: number
  title: string
  description: string
  requiredSkills: string[]
  budget: number
  deadline: string
  status: 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
  type: 'FREE' | 'PAID'
  client: {
    id: number
    username: string
    email: string
    isVerified: boolean
  }
  freelancer?: {
    id: number
    username: string
    email: string
  }
  attachmentUrl?: string
  isFeatured: boolean
  viewCount: number
  applicationCount: number
  createdAt: string
  updatedAt: string
} 