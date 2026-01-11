import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAdminJobs, getApplicationsByJobId, getResumeFileBase64, addUserToJob, deleteUserFromJob, getUsers, getResumesByUser } from '../../apiService';
import { BriefcaseIcon, UserGroupIcon, DocumentArrowDownIcon, EyeIcon, TrashIcon, PlusIcon, XMarkIcon, CheckIcon } from '@heroicons/react/24/outline';

export default function AdminApplications() {
  const [jobs, setJobs] = useState([]);
  const [selectedJob, setSelectedJob] = useState(null);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [userList, setUserList] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [userResumes, setUserResumes] = useState([]);
  const [selectedResume, setSelectedResume] = useState(null);
  const [search, setSearch] = useState("");
  const navigate = useNavigate();

  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    const fetchJobs = async () => {
      setLoading(true);
      try {
        const res = await getAdminJobs(config);
        setJobs(res.data);
      } catch (error) {
        if (error.response && error.response.status === 403) {
          navigate('/login');
        }
      }
      setLoading(false);
    };
    fetchJobs();
  }, [navigate]);

  const handleSelectJob = async (jobId) => {
    setSelectedJob(jobId);
    setLoading(true);
    try {
      const res = await getApplicationsByJobId(jobId, config);
      setApplications(res.data);
    } catch {
      setApplications([]);
    }
    setLoading(false);
  };

  // Xem CV dạng PDF
  const handleViewResume = async (resumeId, resumeTitle) => {
    try {
      const token = localStorage.getItem('token');
      const response = await getResumeFileBase64(resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const base64 = response.data;
      const pdfWindow = window.open('');
      pdfWindow.document.write(
        `<title>${resumeTitle || 'CV'}</title><iframe width='100%' height='100%' src='data:application/pdf;base64,${base64}'></iframe>`
      );
    } catch (error) {
      alert('Đã xảy ra lỗi khi xem CV.');
    }
  };

  // Tải về CV
  const handleDownloadResume = async (resumeId, resumeTitle) => {
    try {
      const token = localStorage.getItem('token');
      const response = await getResumeFileBase64(resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const base64 = response.data;
      const link = document.createElement('a');
      link.href = `data:application/pdf;base64,${base64}`;
      link.download = `${resumeTitle || 'CV'}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      alert('Đã xảy ra lỗi khi tải về CV.');
    }
  };

  // Lấy danh sách user khi mở modal thêm
  const handleOpenAddModal = async () => {
    setShowAddModal(true);
    try {
      const res = await getUsers(config);
      setUserList(res.data);
    } catch {}
  };
  const handleCloseAddModal = () => {
    setShowAddModal(false);
    setSelectedUser(null);
    setUserResumes([]);
    setSelectedResume(null);
  };
  // Khi chọn user, lấy danh sách CV
  const handleSelectUser = async (userId) => {
    setSelectedUser(userId);
    try {
      const res = await getResumesByUser(userId, config);
      setUserResumes(res.data);
    } catch {
      setUserResumes([]);
    }
  };
  // Thêm ứng viên vào job
  const handleAddUserToJob = async () => {
    if (!selectedUser || !selectedResume) return;
    try {
      await addUserToJob(selectedJob, selectedUser, selectedResume, config);
      handleCloseAddModal();
      handleSelectJob(selectedJob); // reload danh sách
    } catch (e) {
      alert('Thêm ứng viên thất bại!');
    }
  };
  // Xóa ứng viên khỏi job
  const handleDeleteUserFromJob = async (applicationId) => {
    if (!window.confirm('Bạn có chắc muốn xóa ứng viên này khỏi job?')) return;
    try {
      await deleteUserFromJob(selectedJob, applicationId, config);
      handleSelectJob(selectedJob);
    } catch (e) {
      alert('Xóa thất bại!');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-extrabold text-blue-700 flex items-center gap-2">
            <UserGroupIcon className="w-8 h-8 text-primary" /> Quản lý ứng tuyển
          </h2>
        </div>
        <div className="flex items-center mb-4">
          <input
            type="text"
            className="w-full max-w-xs px-4 py-2 border rounded shadow-sm focus:ring-2 focus:ring-primary focus:outline-none"
            placeholder="Tìm kiếm theo tên ứng viên, job, email..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
          {jobs.map(job => (
            <div
              key={job.id}
              className={`p-4 rounded shadow cursor-pointer border transition hover:bg-blue-50 ${selectedJob === job.id ? 'border-blue-500 bg-blue-50' : 'bg-white'}`}
              onClick={() => handleSelectJob(job.id)}
            >
              <div className="font-semibold text-blue-700 flex items-center gap-2"><BriefcaseIcon className="w-5 h-5 text-primary" />{job.title}</div>
              <div className="text-gray-500 text-sm">{job.companyName || job.company?.name}</div>
              <div className="text-gray-400 text-xs">ID: {job.id}</div>
            </div>
          ))}
        </div>
        {selectedJob && (
          <div className="bg-white rounded-2xl shadow-xl p-6 overflow-x-auto">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xl font-bold text-blue-700">Danh sách ứng viên ứng tuyển</h3>
              <button className="flex items-center gap-1 px-4 py-2 bg-primary text-white rounded font-semibold shadow hover:bg-accent transition" onClick={handleOpenAddModal}><PlusIcon className="w-5 h-5" /> Thêm ứng viên</button>
            </div>
            {loading ? <div className="text-center py-8 text-lg">Đang tải...</div> : (
              <table className="min-w-full divide-y divide-gray-200">
                <thead>
                  <tr className="bg-blue-50">
                    <th className="px-4 py-3 text-left font-bold text-gray-700">Tên ứng viên</th>
                    <th className="px-4 py-3 text-left font-bold text-gray-700">Email</th>
                    <th className="px-4 py-3 text-left font-bold text-gray-700">Job</th>
                    <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                    <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {applications.filter(app =>
                    (app.userFullname?.toLowerCase().includes(search.toLowerCase()) ||
                     app.userEmail?.toLowerCase().includes(search.toLowerCase()) ||
                     app.jobTitle?.toLowerCase().includes(search.toLowerCase()))
                  ).map(app => (
                    <tr key={app.id} className="hover:bg-blue-50 transition">
                      <td className="px-4 py-2">{app.userFullname}</td>
                      <td className="px-4 py-2">{app.userEmail}</td>
                      <td className="px-4 py-2">{app.jobTitle}</td>
                      <td className="px-4 py-2">{app.status}</td>
                      <td className="px-4 py-2 flex gap-2 justify-center">
                        <button className="p-2 rounded hover:bg-blue-100" onClick={() => handleViewResume(app.resumeId, app.userFullname)} title="Xem CV">
                          <EyeIcon className="w-5 h-5 text-blue-600" />
                        </button>
                        <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDelete(app.id)} title="Xóa">
                          <TrashIcon className="w-5 h-5 text-red-600" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* Modal thêm ứng viên */}
        {showAddModal && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md relative">
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={handleCloseAddModal}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h4 className="text-lg font-bold mb-4 flex items-center gap-2"><UserGroupIcon className="w-5 h-5 text-primary" />Thêm ứng viên vào job</h4>
              <div className="mb-4">
                <label className="block mb-1">Chọn ứng viên:</label>
                <select className="w-full border p-2 rounded" value={selectedUser || ''} onChange={e => { handleSelectUser(e.target.value); setSelectedResume(''); }}>
                  <option value="">-- Chọn ứng viên --</option>
                  {userList.map(u => (
                    <option key={u.id} value={u.id}>{u.fullname} ({u.email})</option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block mb-1">Chọn CV:</label>
                <select className="w-full border p-2 rounded" value={selectedResume || ''} onChange={e => setSelectedResume(e.target.value)} disabled={!userResumes.length}>
                  <option value="">-- Chọn CV --</option>
                  {userResumes.map(r => (
                    <option key={r.id} value={r.id}>{r.title}</option>
                  ))}
                </select>
                {!userResumes.length && selectedUser && <div className="text-red-500 text-sm mt-1">Ứng viên này chưa có CV nào.</div>}
              </div>
              <div className="flex gap-2 justify-end">
                <button className="px-4 py-2 bg-gray-300 rounded font-semibold" onClick={handleCloseAddModal}>Hủy</button>
                <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" onClick={handleAddUserToJob} disabled={!selectedUser || !selectedResume}><CheckIcon className="w-5 h-5" /> Thêm</button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
