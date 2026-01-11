import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const adminFunctions = [
  { title: 'Thống kê', path: '/admin/stats', icon: '📊' },
  { title: 'Quản lý người dùng', path: '/admin/users', icon: '👤' },
  { title: 'Quản lý ứng viên', path: '/admin/candidates', icon: '🧑‍💼' },
  { title: 'Quản lý công việc', path: '/admin/jobs', icon: '💼' },
  { title: 'Quản lý loại công việc', path: '/admin/job-types', icon: '🗂️' },
  { title: 'Quản lý vị trí công việc', path: '/admin/job-positions', icon: '📌' },
  { title: 'Quản lý danh mục công việc', path: '/admin/job-categories', icon: '📚' },
  { title: 'Quản lý công ty', path: '/admin/company-manager', icon: '🏭' },
  { title: 'Quản lý ứng tuyển', path: '/admin/applications', icon: '📝' },
  { title: 'Quản lý phiên phỏng vấn', path: '/admin/interview-sessions', icon: '🎤' },
  { title: 'Quản lý slot phỏng vấn', path: '/admin/interview-slots', icon: '⏰' },
  { title: 'Quản lý gói dịch vụ', path: '/admin/service-packages', icon: '💎' },
  { title: 'Quản lý recruiter đã mua gói', path: '/admin/recruiter-packages', icon: '🛒' },

];

export default function AdminSidebar() {
  const location = useLocation();
  return (
    <aside className="w-64 bg-white shadow-lg flex flex-col min-h-screen">
      <div className="h-16 flex items-center justify-center border-b">
        <Link to="/" className="text-2xl font-bold text-blue-700 tracking-wide flex items-center">
          <img src="/workhub-logo.png" alt="WorkHub Logo" className="h-10 w-auto object-contain" />
        </Link>
      </div>
      <nav className="flex-1 py-6">
        {adminFunctions.map((func) => (
          <Link
            key={func.title}
            to={func.path}
            className={`flex items-center px-6 py-3 text-gray-700 hover:bg-blue-50 hover:text-blue-700 transition rounded-lg mx-2 my-1 ${location.pathname === func.path ? 'bg-blue-50 text-blue-700 font-bold' : ''}`}
          >
            <span className="text-xl mr-3">{func.icon}</span>
            <span className="font-medium">{func.title}</span>
          </Link>
        ))}
      </nav>
    </aside>
  );
}
