import React, { useEffect, useState } from 'react';
import { getRecruiterJobs } from '../../apiService';
import { Link } from 'react-router-dom';
import { FaPlus, FaEye, FaCalendarAlt, FaUserTie, FaClipboardList } from 'react-icons/fa';

export default function RecruiterJobList() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    fetchJobs();
    // eslint-disable-next-line
  }, []);

  const fetchJobs = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getRecruiterJobs(config); // Đúng endpoint cho recruiter
      console.log('DEBUG jobs API response:', res);
      if (res && res.data) {
        setJobs(res.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
      } else {
        setJobs([]);
        setError('Không nhận được dữ liệu từ server.');
      }
    } catch (err) {
      setJobs([]);
      setError('Lỗi khi tải dữ liệu: ' + (err?.response?.data?.message || err.message || 'Không xác định'));
      console.error('Lỗi khi fetch jobs:', err);
    }
    setLoading(false);
  };

  // Skeleton loading
  const renderSkeleton = () => (
    <div className="animate-pulse">
      {[1,2,3].map(i => (
        <div key={i} className="h-12 bg-gray-200 rounded mb-2" />
      ))}
    </div>
  );

  // Trạng thái không có job
  const renderEmpty = () => (
    <div className="flex flex-col items-center justify-center py-16 text-gray-500">
      <FaClipboardList size={48} className="mb-4" />
      <div className="text-lg font-semibold mb-2">Chưa có tin tuyển dụng nào</div>
      <div className="mb-4">Hãy tạo tin tuyển dụng đầu tiên để thu hút ứng viên!</div>
      <Link to="/recruiter/jobs/create" className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded shadow hover:bg-blue-700 transition">
        <FaPlus className="mr-2" /> Tạo tin tuyển dụng
      </Link>
    </div>
  );

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold text-blue-700 flex items-center">
          <FaClipboardList className="mr-2" /> Danh sách tin tuyển dụng
        </h2>
        <Link to="/recruiter/jobs/create" className="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded shadow hover:bg-blue-700 transition">
          <FaPlus className="mr-2" /> Tạo tin tuyển dụng
        </Link>
      </div>
      <div className="bg-white rounded-xl shadow overflow-x-auto">
        {loading ? (
          <div className="p-6">{renderSkeleton()}</div>
        ) : error ? (
          <div className="text-red-600 font-semibold mb-4">{error}</div>
        ) : jobs.length === 0 ? (
          renderEmpty()
        ) : (
          <table className="w-full min-w-[700px]">
            <thead>
              <tr className="bg-gray-100 text-gray-700">
                <th className="px-4 py-3 text-left">Tiêu đề</th>
                <th className="px-4 py-3 text-left">Vị trí</th>
                <th className="px-4 py-3 text-left">Loại</th>
                <th className="px-4 py-3 text-left">Ngày đăng</th>
                <th className="px-4 py-3 text-left">Trạng thái</th>
                <th className="px-4 py-3 text-left">Hành động</th>
              </tr>
            </thead>
            <tbody>
              {jobs.map(job => (
                <tr key={job.id} className="border-b hover:bg-blue-50 transition">
                  <td className="px-4 py-3 font-semibold text-blue-800 max-w-xs truncate">{job.title}</td>
                  <td className="px-4 py-3 flex items-center gap-2"> <FaUserTie className="text-gray-400" /> {job.position?.name || '-'}</td>
                  <td className="px-4 py-3">{job.type?.name || '-'}</td>
                  <td className="px-4 py-3 flex items-center gap-2"><FaCalendarAlt className="text-gray-400" />{job.createdAt ? new Date(job.createdAt).toLocaleDateString() : '-'}</td>
                  <td className="px-4 py-3">{job.status || 'Đang đăng'}</td>
                  <td className="px-4 py-3 flex gap-2">
                    <Link to={`/recruiter/jobs/${job.id}`} className="inline-flex items-center px-2 py-1 bg-blue-100 text-blue-700 rounded hover:bg-blue-200 transition text-sm">
                      <FaEye className="mr-1" /> Xem
                    </Link>
                    <Link to={`/recruiter/jobs/${job.id}/create-session`} className="inline-flex items-center px-2 py-1 bg-green-100 text-green-700 rounded hover:bg-green-200 transition text-sm">
                      <FaCalendarAlt className="mr-1" /> Tạo phiên
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
