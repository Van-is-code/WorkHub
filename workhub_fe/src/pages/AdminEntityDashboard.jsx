import React from 'react';
import { Link } from 'react-router-dom';

const entities = [
  { name: 'User', path: '/admin/users' },
  { name: 'Admin', path: '/admin/admins' },
  { name: 'Admin Log', path: '/admin/admin-logs' },
  { name: 'Application', path: '/admin/applications' },
  { name: 'Company Profile', path: '/admin/companies' },
  { name: 'Inspection', path: '/admin/inspections' },
  { name: 'Interview Session', path: '/admin/interview-sessions' },
  { name: 'Interview Slot', path: '/admin/interview-slots' },
  { name: 'Job', path: '/admin/jobs' },
  { name: 'Job Category', path: '/admin/job-categories' },
  { name: 'Job Position', path: '/admin/job-positions' },
  { name: 'Job Type', path: '/admin/job-types' },
  { name: 'Message', path: '/admin/messages' },
  { name: 'Notification', path: '/admin/notifications' },
  { name: 'Resume', path: '/admin/resumes' },
  { name: 'Resume Review', path: '/admin/resume-reviews' },
  { name: 'Resume View', path: '/admin/resume-views' },
  { name: 'Review', path: '/admin/reviews' },
  { name: 'Saved Job', path: '/admin/saved-jobs' },
  { name: 'Service Feature', path: '/admin/service-features' },
  { name: 'Service Package', path: '/admin/service-packages' },
  { name: 'Skill', path: '/admin/skills' },
  { name: 'Transaction', path: '/admin/transactions' },
  { name: 'User Benefits', path: '/admin/user-benefits' },
  { name: 'User Package', path: '/admin/user-packages' },
  { name: 'User Package History', path: '/admin/user-package-histories' },
];

export default function AdminEntityDashboard() {
  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard - Quản lý toàn bộ entity</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {entities.map((entity) => (
          <Link
            key={entity.name}
            to={entity.path}
            className="block p-6 bg-white rounded-lg shadow hover:bg-blue-50 border border-gray-200 transition"
          >
            <span className="text-xl font-semibold text-blue-700">{entity.name}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
