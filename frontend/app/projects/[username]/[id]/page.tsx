'use client';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { useAuth } from '../../../../src/contexts/AuthContext';
import Link from 'next/link';

export default function ProjectDetailPage({ params }: { params: { username: string; id: string } }) {
  const { username, id } = params;
  const [project, setProject] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    fetch(`http://localhost:8080/api/projects/${id}`)
      .then(res => res.json())
      .then(data => {
        setProject(data);
        setLoading(false);
      });
  }, [id]);

  if (loading) return <div>Loading...</div>;
  if (!project) return <div>Project not found.</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-4">{project.title}</h1>
      <div className="mb-2 text-gray-600">By <Link href={`/profile/${project.clientUsername}`}>{project.clientUsername}</Link></div>
      <div className="mb-4 text-gray-800">{project.description}</div>
      <div className="mb-2">Skills: {project.requiredSkills?.join(', ')}</div>
      <div className="mb-2">Budget: {project.budget}</div>
      <div className="mb-2">Deadline: {project.deadline}</div>
      <div className="mb-2">Type: {project.type}</div>
      <div className="mb-2">Status: {project.status}</div>
      <div className="mt-6">
        {user && user.username !== project.clientUsername && (
          <Link href={`/chat/${project.clientUsername}`}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors">
            Send Message
          </Link>
        )}
      </div>
    </div>
  );
} 