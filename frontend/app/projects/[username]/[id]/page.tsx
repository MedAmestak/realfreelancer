'use client';
import { useEffect, useState } from 'react';
import { useAuth } from '../../../../src/contexts/AuthContext';
import Link from 'next/link';
import { Calendar, DollarSign, User, Tag, ShieldCheck, Briefcase, MessageSquare, Send } from 'lucide-react';
import { Project } from '@/types/project'; // Assuming you have this type defined
import Header from '@/components/Header';

export default function ProjectDetailPage({ params }: { params: { id: string } }) {
  const { id } = params;
  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { user, getAuthToken } = useAuth();

  useEffect(() => {
    const fetchProject = async () => {
      const token = getAuthToken();
      const headers: Record<string, string> = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;
      try {
        const response = await fetch(`http://localhost:8080/api/projects/${id}`, {
          headers,
        });

        if (response.ok) {
          const data = await response.json();
          setProject(data);
        } else if (response.status === 404) {
          setError("Project not found.");
        } else {
          const errorText = await response.text();
          setError(`Failed to load project: ${errorText}`);
        }
      } catch (err) {
        setError("An error occurred while fetching the project.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProject();
  }, [id, getAuthToken]);

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const formatBudget = (budget?: number) => {
    if (budget === undefined) return 'N/A';
    if (budget === 0) return '$Free';
    return `$${budget.toLocaleString()}`;
  };

  if (loading) return <div className="text-center py-10">Loading...</div>;
  if (error) return <div className="text-center py-10 text-red-500">{error}</div>;
  if (!project) return <div className="text-center py-10">Project not found.</div>;

  return (
    <>
      <Header />
      <div className="bg-gray-50 min-h-screen">
        <div className="container mx-auto px-4 py-8">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            
            {/* Main Content */}
            <div className="lg:col-span-2 bg-white p-6 sm:p-8 rounded-lg shadow-md">
              <h1 className="text-3xl sm:text-4xl font-bold text-gray-800 mb-3">{project.title}</h1>
              <div className="flex items-center space-x-2 text-sm text-gray-500 mb-6">
                <Briefcase className="w-4 h-4" />
                <span>{project.type}</span>
                <span className="text-gray-300">|</span>
                <Calendar className="w-4 h-4" />
                <span>Posted on {formatDate(project.createdAt)}</span>
              </div>

              <h2 className="text-xl font-semibold text-gray-700 mt-8 mb-4 border-b pb-2">Project Description</h2>
              <p className="text-gray-600 leading-relaxed whitespace-pre-wrap">{project.description}</p>

              <h2 className="text-xl font-semibold text-gray-700 mt-8 mb-4 border-b pb-2">Required Skills</h2>
              <div className="flex flex-wrap gap-3">
                {project.requiredSkills?.map(skill => (
                  <span key={skill} className="bg-blue-100 text-blue-800 text-sm font-medium px-3 py-1 rounded-full">{skill}</span>
                ))}
              </div>
            </div>

            {/* Sidebar */}
            <div className="lg:col-span-1 space-y-6">
              <div className="bg-white p-6 rounded-lg shadow-md">
                  <div className="flex items-center justify-between mb-4">
                    <h2 className="text-2xl font-bold text-gray-800">{formatBudget(project.budget)}</h2>
                    <span className={`px-3 py-1 text-sm font-semibold rounded-full ${project.status === 'OPEN' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>
                      {project.status}
                    </span>
                  </div>
                  <div className="text-sm text-gray-500 flex items-center justify-between">
                      <span>Deadline:</span>
                      <span>{formatDate(project.deadline)}</span>
                  </div>

                  {(!user) && (
                      <Link href="/login"
                          className="mt-6 w-full flex items-center justify-center bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-semibold">
                          <Send className="w-5 h-5 mr-2" />
                          Login to Apply
                      </Link>
                  )}
                  {user && project?.client && user.username !== project.client.username && (
                    <button
                      onClick={async () => {
                        if (!project?.client?.id) return;
                        const token = getAuthToken();
                        if (!token) return;
                        await fetch('http://localhost:8080/api/chat/send', {
                          method: 'POST',
                          headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json',
                          },
                          body: JSON.stringify({
                            recipientId: project.client.id,
                            content: `Hi, I'm interested in your project: ${project.title}`,
                            projectId: project.id,
                          }),
                        });
                        window.location.href = `/chat/${project.client.id}`;
                      }}
                      className="mt-6 w-full flex items-center justify-center bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors font-semibold"
                    >
                      <Send className="w-5 h-5 mr-2" />
                      Apply Now
                    </button>
                  )}
              </div>

              <div className="bg-white p-6 rounded-lg shadow-md">
                  <h3 className="text-lg font-semibold text-gray-700 mb-4 border-b pb-2">About the Client</h3>
                  {project.client ? (
                      <div className="flex items-center space-x-4">
                          <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center">
                              <User className="w-6 h-6 text-gray-500"/>
                          </div>
                          <div>
                              <Link href={`/profile/${project.client.username}`} className="font-semibold text-gray-800 hover:text-blue-600">{project.client.username}</Link>
                              <div className="text-sm text-gray-500 flex items-center space-x-2 mt-1">
                                  {project.client.isVerified ? (
                                      <><ShieldCheck className="w-4 h-4 text-green-500"/><span>Verified Client</span></>
                                  ) : (
                                      <><ShieldCheck className="w-4 h-4 text-gray-400"/><span>Client</span></>
                                  )}
                              </div>
                          </div>
                      </div>
                  ) : (
                      <p className="text-gray-500">Client information not available.</p>
                  )}
              </div>
            </div>

          </div>
        </div>
      </div>
    </>
  );
} 