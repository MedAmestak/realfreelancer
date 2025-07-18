"use client";
import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../../src/contexts/AuthContext";

interface SignupFormData {
  username: string;
  email: string;
  password: string;
}

const SignupForm: React.FC = () => {
  const { register } = useAuth();
  const router = useRouter();
  const [formData, setFormData] = useState<SignupFormData>({
    username: '',
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const response = await register(formData);
      setSubmitting(false);
      if (response.success) {
        router.push('/login');
      } else {
        setError(response.error || 'Registration failed. Please try again.');
      }
    } catch (err) {
      setSubmitting(false);
      setError('Network error. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-md p-8 w-full max-w-md mx-auto mt-12">
      <h2 className="text-2xl font-bold mb-6 text-center">Create Your Account</h2>
      {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4 text-center">{error}</div>}
      <div className="mb-4">
        <label htmlFor="username" className="block text-gray-700 mb-2">Username</label>
        <input
          id="username"
          type="text"
          name="username"
          value={formData.username}
          onChange={handleInputChange}
          className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div className="mb-4">
        <label htmlFor="email" className="block text-gray-700 mb-2">Email</label>
        <input
          id="email"
          type="email"
          name="email"
          value={formData.email}
          onChange={handleInputChange}
          className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div className="mb-6">
        <label htmlFor="password" className="block text-gray-700 mb-2">Password</label>
        <input
          id="password"
          type="password"
          name="password"
          value={formData.password}
          onChange={handleInputChange}
          className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition-colors disabled:opacity-50"
        disabled={submitting}
      >
        {submitting ? "Signing up..." : "Sign Up"}
      </button>
      <div className="text-center mt-4 text-sm">
        Already have an account? <a href="/login" className="text-blue-600 hover:underline">Sign in</a>
      </div>
    </form>
  );
};

export default SignupForm; 