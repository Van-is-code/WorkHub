import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BriefcaseIcon, UserGroupIcon, ClipboardDocumentListIcon, DocumentTextIcon } from '@heroicons/react/24/outline';
import { useQuery } from '@tanstack/react-query';
import { getCompanyByRecruiter, getUserBenefits, getRecruiterJobs, getApplicationsByUser, deleteJob } from '../apiService';

const Dashboard = () => {
  // Lấy user từ token
  const token = localStorage.getItem('token');
  let user = null;
  try {
    user = token ? JSON.parse(atob(token.split('.')[1])) : null;
  } catch { user = null; }

  // Lấy thông tin công ty
  const { data: company } = useQuery({
    queryKey: ['company', user?.id],
    queryFn: async () => {
      if (!user?.id) return null;
      const res = await getCompanyByRecruiter(user.id, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
      return res.data;
    },
    enabled: !!user?.id,
  });

  // Lấy user benefits (quota job/cv)
  const { data: benefits } = useQuery({
    queryKey: ['benefits', user?.id],
    queryFn: async () => {
      if (!user?.id) return null;
      const res = await getUserBenefits(user.id, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
      // Nếu trả về mảng, lấy benefit còn hạn/quota lớn nhất
      if (Array.isArray(res.data)) {
        return res.data.sort((a, b) => (b.jobPostLimit ?? 0) - (a.jobPostLimit ?? 0))[0] || null;
      }
      return res.data;
    },
    enabled: !!user?.id,
  });

  // Lấy số lượng job đã đăng
  const { data: jobs } = useQuery({
    queryKey: ['jobs', user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      // Sử dụng API lấy job theo recruiter, không cần lọc thủ công
      try {
        const res = await getRecruiterJobs({ headers: token ? { Authorization: `Bearer ${token}` } : {} });
        console.log('Recruiter jobs:', res.data); // Debug dữ liệu trả về
        return Array.isArray(res.data) ? res.data : [];
      } catch (err) {
        console.error('Lỗi lấy recruiter jobs:', err);
        return [];
      }
    },
    enabled: !!user?.id,
  });

  // Lấy số lượng CV đã ứng tuyển
  const { data: applications } = useQuery({
    queryKey: ['applications', user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      const res = await getApplicationsByUser(user.id, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
      return Array.isArray(res.data) ? res.data : [];
    },
    enabled: !!user?.id,
  });

  // Chuẩn bị dữ liệu động cho recruiterStats
  const recruiterStats = [
    { label: 'Tin tuyển dụng', value: jobs?.length ?? '-', icon: <BriefcaseIcon className="w-6 h-6 text-blue-500" /> },
    { label: 'CV đã ứng tuyển', value: applications?.length ?? '-', icon: <UserGroupIcon className="w-6 h-6 text-green-500" /> },
    { label: 'Quota job còn lại', value: benefits?.jobPostLimit ?? '-', icon: <DocumentTextIcon className="w-6 h-6 text-purple-500" /> },
    { label: 'Quota CV còn lại', value: benefits?.cvLimit ?? '-', icon: <ClipboardDocumentListIcon className="w-6 h-6 text-yellow-500" /> },
  ];

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.altKey && e.key === '1') {
        window.location.href = '/recruiter/job-manager';
      } else if (e.altKey && e.key === '2') {
        window.location.href = '/recruiter/cv-list';
      } else if (e.altKey && e.key === '3') {
        window.location.href = '/recruiter/jobs';
      } else if (e.altKey && e.key === '4') {
        window.location.href = '/recruiter/job-list';
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-2xl shadow-2xl p-8 relative overflow-hidden">
          <div className="flex items-center gap-6 mb-8">
            <img src="/workhub-logo.png" alt="Recruiter Avatar" className="w-20 h-20 rounded-full border-4 border-primary shadow" />
            <div>
              <h1 className="text-3xl font-extrabold text-primary mb-1">{company?.name || 'Dashboard Nhà Tuyển Dụng'}</h1>
              <p className="text-gray-700">Chào mừng bạn đến với trang quản lý dành cho nhà tuyển dụng.</p>
              <div className="mt-2 flex flex-wrap gap-4 text-base text-gray-700">
                
              </div>
            </div>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-10">
            {recruiterStats.map((stat, idx) => (
              <div key={idx} className="flex flex-col items-center bg-gradient-to-tr from-white to-blue-50 rounded-xl shadow p-4">
                {stat.icon}
                <div className="text-2xl font-bold text-primary mt-2">{stat.value}</div>
                <div className="text-gray-600 text-sm mt-1">{stat.label}</div>
              </div>
            ))}
          </div>
          {/* Danh sách tin tuyển dụng của bạn */}
          <div className="bg-white rounded-2xl shadow-card p-8 mt-8">
            <h3 className="text-xl font-bold mb-4 text-primary">Danh sách tin tuyển dụng của bạn</h3>
            {(!jobs || jobs.length === 0) ? (
              <div className="text-gray-500 text-center py-8">Bạn chưa có tin tuyển dụng nào.<br/>Hãy nhấn <span className='font-semibold text-primary'>+ Thêm tin mới</span> ở góc trên để bắt đầu đăng tuyển!</div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="bg-gray-100">
                      <th className="py-2 px-4">Tiêu đề</th>
                      <th className="py-2 px-4">Địa điểm</th>
                      <th className="py-2 px-4">Mức lương</th>
                      <th className="py-2 px-4">Hành động</th>
                    </tr>
                  </thead>
                  <tbody>
                    {jobs && jobs.map(job => (
                      <tr key={job.id} className="border-b hover:bg-gray-50 transition">
                        <td className="py-2 px-4 font-semibold text-dark">{job.title}</td>
                        <td className="py-2 px-4">{job.location}</td>
                        <td className="py-2 px-4">{job.salary || job.salaryRange || '-'}</td>
                        <td className="py-2 px-4 flex flex-wrap gap-2">
                          <Link to={`/recruiter/jobs/${job.id}/create-session`} className="px-3 py-1 bg-green-600 text-white rounded-full text-sm font-semibold shadow hover:bg-green-700 transition">Tạo phiên phỏng vấn</Link>
                          <Link to={`/recruiter/jobs/${job.id}/applications`} className="px-3 py-1 bg-blue-600 text-white rounded-full text-sm font-semibold shadow hover:bg-blue-700 transition">Xem ứng viên</Link>
                          <Link to={`/recruiter/jobs/${job.id}/detail`} className="px-3 py-1 bg-gray-600 text-white rounded-full text-sm font-semibold shadow hover:bg-gray-700 transition">Chi tiết</Link>
                          <button
                            className="px-3 py-1 bg-red-600 text-white rounded-full text-sm font-semibold shadow hover:bg-red-700 transition"
                            onClick={async () => {
                              if (window.confirm('Bạn có chắc chắn muốn xóa job này? Hành động này không thể hoàn tác!')) {
                                try {
                                  await deleteJob(job.id, { headers: { Authorization: `Bearer ${token}` } });
                                  window.location.reload();
                                } catch (err) {
                                  alert('Xóa job thất bại: ' + (err?.response?.data?.message || err.message));
                                }
                              }
                            }}
                          >
                            Xóa job
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
