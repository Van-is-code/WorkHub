import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ChartBarIcon } from '@heroicons/react/24/outline';
import AdminCharts from '../../components/admin/AdminCharts';

export default function AdminStats() {
  const navigate = useNavigate();
  React.useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }
  }, [navigate]);
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center gap-2 mb-8">
          <ChartBarIcon className="w-8 h-8 text-primary" />
          <h2 className="text-3xl font-extrabold text-blue-700">Thống kê & Biểu đồ hệ thống</h2>
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <AdminCharts />
        </div>
      </div>
    </div>
  );
}
