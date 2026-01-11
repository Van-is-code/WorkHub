import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ChartBarIcon, UserGroupIcon, BriefcaseIcon, BuildingOfficeIcon, ClipboardDocumentListIcon, Cog6ToothIcon, BellAlertIcon, CurrencyDollarIcon, DocumentTextIcon, AcademicCapIcon, InboxIcon, WrenchScrewdriverIcon } from '@heroicons/react/24/outline';

const adminFunctions = [
  { title: 'Thống kê', path: '/admin/stats', icon: <ChartBarIcon className="w-6 h-6 text-blue-500" /> },
  { title: 'Quản lý người dùng', path: '/admin/users', icon: <UserGroupIcon className="w-6 h-6 text-green-500" /> },
  { title: 'Quản lý ứng viên', path: '/admin/candidates', icon: <AcademicCapIcon className="w-6 h-6 text-indigo-500" /> },
  { title: 'Quản lý công việc', path: '/admin/jobs', icon: <BriefcaseIcon className="w-6 h-6 text-yellow-500" /> },
  { title: 'Quản lý loại công việc', path: '/admin/job-types', icon: <Cog6ToothIcon className="w-6 h-6 text-pink-500" /> },
  { title: 'Quản lý vị trí công việc', path: '/admin/job-positions', icon: <ClipboardDocumentListIcon className="w-6 h-6 text-orange-500" /> },
  { title: 'Quản lý danh mục công việc', path: '/admin/job-categories', icon: <DocumentTextIcon className="w-6 h-6 text-purple-500" /> },
  { title: 'Quản lý công ty', path: '/admin/company-manager', icon: <BuildingOfficeIcon className="w-6 h-6 text-cyan-500" /> },
  { title: 'Quản lý ứng tuyển', path: '/admin/applications', icon: <InboxIcon className="w-6 h-6 text-teal-500" /> },
  { title: 'Quản lý phỏng vấn', path: '/admin/interviews', icon: <WrenchScrewdriverIcon className="w-6 h-6 text-amber-500" /> },
  { title: 'Quản lý kỹ năng & gói dịch vụ', path: '/admin/services', icon: <Cog6ToothIcon className="w-6 h-6 text-fuchsia-500" /> },
  { title: 'Quản lý thông báo & tin nhắn', path: '/admin/communications', icon: <BellAlertIcon className="w-6 h-6 text-rose-500" /> },
  { title: 'Quản lý đánh giá & hồ sơ', path: '/admin/profiles', icon: <UserGroupIcon className="w-6 h-6 text-lime-500" /> },
  { title: 'Quản lý giao dịch', path: '/admin/transactions', icon: <CurrencyDollarIcon className="w-6 h-6 text-emerald-500" /> },
  { title: 'Nhật ký hoạt động admin', path: '/admin/admin-logs', icon: <ClipboardDocumentListIcon className="w-6 h-6 text-gray-500" /> },
];

export default function AdminDashboard() {
  const location = useLocation();
  const navigate = useNavigate();
  React.useEffect(() => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    if (!token || role !== 'admin') {
      navigate('/login');
    }
  }, [navigate]);
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 flex">
      {/* Sidebar */}
      <aside className="w-72 bg-white shadow-2xl flex flex-col min-h-screen rounded-r-3xl">
        <div className="h-20 flex items-center justify-center border-b">
          <span className="text-3xl font-extrabold text-blue-700 tracking-wide">WorkHub Admin</span>
        </div>
        <nav className="flex-1 py-8">
          {adminFunctions.map((func) => (
            <Link
              key={func.title}
              to={func.path}
              className={`flex items-center px-7 py-4 text-lg hover:bg-blue-100 hover:text-blue-700 transition rounded-xl mx-3 my-2 gap-4 font-medium ${location.pathname === func.path ? 'bg-blue-100 text-blue-700 font-bold shadow' : 'text-gray-700'}`}
            >
              {func.icon}
              <span>{func.title}</span>
            </Link>
          ))}
        </nav>
      </aside>
      {/* Main content */}
      <div className="flex-1 flex flex-col">
        {/* Header */}
        <header className="h-20 bg-white shadow flex items-center px-12 justify-between rounded-bl-3xl">
          <h1 className="text-3xl font-extrabold text-blue-700">Dashboard quản trị</h1>
          <div className="flex items-center gap-4">
            <span className="text-gray-600 text-lg font-semibold">Xin chào, Admin!</span>
            <img src="https://themewagon.github.io/argon-dashboard-tailwind/assets/img/team-2.jpg" alt="avatar" className="w-12 h-12 rounded-full border-2 border-blue-400 shadow" />
          </div>
        </header>
        {/* Thống kê nhanh */}
        <section className="grid grid-cols-2 md:grid-cols-4 gap-8 p-12">
          <div className="bg-gradient-to-tr from-blue-200 to-blue-100 rounded-2xl shadow-lg p-8 flex flex-col items-center">
            <ChartBarIcon className="w-10 h-10 text-blue-600 mb-2" />
            <div className="text-2xl font-bold text-blue-700">120</div>
            <div className="text-gray-700 mt-1">Người dùng</div>
          </div>
          <div className="bg-gradient-to-tr from-green-200 to-green-100 rounded-2xl shadow-lg p-8 flex flex-col items-center">
            <UserGroupIcon className="w-10 h-10 text-green-600 mb-2" />
            <div className="text-2xl font-bold text-green-700">45</div>
            <div className="text-gray-700 mt-1">Ứng viên</div>
          </div>
          <div className="bg-gradient-to-tr from-yellow-200 to-yellow-100 rounded-2xl shadow-lg p-8 flex flex-col items-center">
            <BriefcaseIcon className="w-10 h-10 text-yellow-600 mb-2" />
            <div className="text-2xl font-bold text-yellow-700">32</div>
            <div className="text-gray-700 mt-1">Công việc</div>
          </div>
          <div className="bg-gradient-to-tr from-cyan-200 to-cyan-100 rounded-2xl shadow-lg p-8 flex flex-col items-center">
            <BuildingOfficeIcon className="w-10 h-10 text-cyan-600 mb-2" />
            <div className="text-2xl font-bold text-cyan-700">12</div>
            <div className="text-gray-700 mt-1">Công ty</div>
          </div>
        </section>
        {/* Nội dung động */}
      </div>
    </div>
  );
}
