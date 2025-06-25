"use client";
import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../../src/contexts/AuthContext";

const LoginForm: React.FC = () => {
  const { login } = useAuth();
  const router = useRouter();
  const [formData, setFormData] = useState({
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
      const result = await login(formData.email, formData.password);
      setSubmitting(false);
      if (result.success) {
        router.push('/dashboard');
      } else {
        setError(result.error || 'Invalid email or password');
      }
    } catch (err) {
      setSubmitting(false);
      setError('Network error. Please try again.');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-md p-8 w-full max-w-md mx-auto mt-12">
      <h2 className="text-2xl font-bold mb-6 text-center">Sign In to RealFreelancer</h2>
      {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4 text-center">{error}</div>}
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
        {submitting ? "Signing in..." : "Sign In"}
      </button>
      <div className="text-center mt-4 text-sm">
        Don&apos;t have an account? <a href="/signup" className="text-blue-600 hover:underline">Sign up</a>
      </div>
    </form>
  );
};

export default LoginForm;