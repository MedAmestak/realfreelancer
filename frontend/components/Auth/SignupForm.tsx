"use client";
import React, { useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../../hooks/useAuth";

const SignupForm: React.FC = () => {
  const { register } = useAuth();
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    const result = await register({ username, email, password });
    setSubmitting(false);
    if (result.success) {
      router.push("/dashboard");
    } else {
      setError(result.error || "Registration failed. Please try again.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-md p-8 w-full max-w-md mx-auto mt-12">
      <h2 className="text-2xl font-bold mb-6 text-center">Create Your Account</h2>
      {error && <div className="bg-red-100 text-red-700 px-4 py-2 rounded mb-4 text-center">{error}</div>}
      <div className="mb-4">
        <label className="block text-gray-700 mb-2">Username</label>
        <input
          type="text"
          value={username}
          onChange={e => setUsername(e.target.value)}
          className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div className="mb-4">
        <label className="block text-gray-700 mb-2">Email</label>
        <input
          type="email"
          value={email}
          onChange={e => setEmail(e.target.value)}
          className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div className="mb-6">
        <label className="block text-gray-700 mb-2">Password</label>
        <input
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
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