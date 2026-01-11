import React, { useEffect, useState } from 'react';
import { getUsers, createUser, updateUser, deleteUser, getUserById, getResumesByUser, createResumeByAdmin, deleteResume, getResumeFileBase64 } from '../../apiService';
import { UserGroupIcon, XMarkIcon, PencilSquareIcon, TrashIcon, PlusIcon, CheckIcon, DocumentArrowDownIcon, EyeIcon } from '@heroicons/react/24/outline';

export default function AdminCandidates() {
  const [candidates, setCandidates] = useState([]);
  const [selectedCandidate, setSelectedCandidate] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ fullname: '', email: '', password: '', phone: '', role: 'candidate', status: 'verified' });
  const [resumes, setResumes] = useState([]);
  const [showResumeForm, setShowResumeForm] = useState(false);
  const [resumeTitle, setResumeTitle] = useState('');
  const [resumeFile, setResumeFile] = useState(null);
  const [search, setSearch] = useState("");
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  // Lấy danh sách ứng viên
  const fetchCandidates = async () => {
    try {
      const res = await getUsers({ ...config, params: { role: 'candidate' } });
      setCandidates(res.data);
    } catch {
      setCandidates([]);
    }
  };
  useEffect(() => { fetchCandidates(); }, []);

  // Lấy CV của ứng viên
  const fetchResumes = async (userId) => {
    try {
      const res = await getResumesByUser(userId, config);
      setResumes(res.data);
    } catch {
      setResumes([]);
    }
  };

  // Thêm/sửa ứng viên
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (selectedCandidate) {
        await updateUser(selectedCandidate.id, formData, config);
      } else {
        await createUser(formData, config);
      }
      setShowForm(false);
      setSelectedCandidate(null);
      setFormData({ fullname: '', email: '', password: '', phone: '', role: 'candidate', status: 'verified' });
      fetchCandidates();
    } catch {
      alert('Lưu thất bại!');
    }
  };
  // Xóa ứng viên
  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc muốn xóa ứng viên này?')) return;
    try {
      await deleteUser(id, config);
      fetchCandidates();
    } catch {
      alert('Xóa thất bại!');
    }
  };
  // Xem/sửa ứng viên
  const handleEdit = async (id) => {
    setSelectedCandidate(candidates.find(u => u.id === id));
    setShowForm(true);
    const res = await getUserById(id, config);
    setFormData({ ...res.data, password: '' });
    fetchResumes(id);
  };
  // Thêm CV
  const handleAddResume = async (e) => {
    e.preventDefault();
    if (!resumeFile || !resumeTitle) return;
    const form = new FormData();
    form.append('file', resumeFile);
    form.append('title', resumeTitle);
    form.append('content', '');
    form.append('skillIds', []);
    try {
      await createResumeByAdmin(selectedCandidate.id, form, config);
      setShowResumeForm(false);
      setResumeFile(null);
      setResumeTitle('');
      fetchResumes(selectedCandidate.id);
    } catch {
      alert('Thêm CV thất bại!');
    }
  };
  // Xóa CV
  const handleDeleteResume = async (resumeId) => {
    if (!window.confirm('Bạn có chắc muốn xóa CV này?')) return;
    try {
      await deleteResume(resumeId, config);
      fetchResumes(selectedCandidate.id);
    } catch {
      alert('Xóa CV thất bại!');
    }
  };
  // Xem CV
  const handleViewResume = async (resumeId, title) => {
    try {
      const response = await getResumeFileBase64(resumeId, config);
      const base64 = response.data;
      const pdfWindow = window.open('');
      pdfWindow.document.write(`<title>${title || 'CV'}</title><iframe width='100%' height='100%' src='data:application/pdf;base64,${base64}'></iframe>`);
    } catch {
      alert('Không xem được CV!');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-extrabold text-blue-700 flex items-center gap-2">
            <UserGroupIcon className="w-8 h-8 text-primary" /> Quản lý ứng viên
          </h2>
        </div>
        <div className="flex items-center mb-4">
          <input
            type="text"
            className="w-full max-w-xs px-4 py-2 border rounded shadow-sm focus:ring-2 focus:ring-primary focus:outline-none"
            placeholder="Tìm kiếm theo tên, email, số điện thoại..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead>
              <tr className="bg-blue-50">
                <th className="px-4 py-3 text-left font-bold text-gray-700">Họ tên</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Email</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Số điện thoại</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {candidates.filter(user =>
                user.fullname?.toLowerCase().includes(search.toLowerCase()) ||
                user.email?.toLowerCase().includes(search.toLowerCase()) ||
                user.phone?.toLowerCase().includes(search.toLowerCase())
              ).map(user => (
                <tr key={user.id} className="hover:bg-blue-50 transition">
                  <td className="px-4 py-2">{user.fullname}</td>
                  <td className="px-4 py-2">{user.email}</td>
                  <td className="px-4 py-2">{user.phone}</td>
                  <td className="px-4 py-2">
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${user.status === 'verified' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>{user.status}</span>
                  </td>
                  <td className="px-4 py-2 flex gap-2 justify-center">
                    <button className="p-2 rounded hover:bg-blue-100" onClick={() => handleEdit(user.id)} title="Sửa">
                      <PencilSquareIcon className="w-5 h-5 text-blue-600" />
                    </button>
                    <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDelete(user.id)} title="Xóa">
                      <TrashIcon className="w-5 h-5 text-red-600" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {/* Form thêm/sửa ứng viên */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md relative" onSubmit={handleSubmit}>
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => { setShowForm(false); setSelectedCandidate(null); }}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-6 text-primary">{selectedCandidate ? 'Cập nhật ứng viên' : 'Thêm ứng viên mới'}</h3>
              <div className="space-y-4">
                <div>
                  <label className="block font-semibold mb-1">Họ tên</label>
                  <input type="text" className="w-full px-4 py-2 border rounded" value={formData.fullname} onChange={e => setFormData({ ...formData, fullname: e.target.value })} required />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Email</label>
                  <input type="email" className="w-full px-4 py-2 border rounded" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} required />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Số điện thoại</label>
                  <input type="text" className="w-full px-4 py-2 border rounded" value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Mật khẩu {selectedCandidate && <span className="text-xs text-gray-400">(Để trống nếu không đổi)</span>}</label>
                  <input type="password" className="w-full px-4 py-2 border rounded" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} placeholder={selectedCandidate ? '••••••••' : ''} required={!selectedCandidate} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Trạng thái</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.status} onChange={e => setFormData({ ...formData, status: e.target.value })}>
                    <option value="verified">Đã xác thực</option>
                    <option value="unverified">Chưa xác thực</option>
                    <option value="suspended">Tạm ngưng</option>
                    <option value="banned">Bị cấm</option>
                  </select>
                </div>
              </div>
              <div className="mt-6 flex gap-2 justify-end">
                <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" type="submit">
                  <CheckIcon className="w-5 h-5" /> {selectedCandidate ? 'Cập nhật' : 'Thêm mới'}
                </button>
                <button className="px-4 py-2 bg-gray-300 rounded font-semibold" type="button" onClick={() => { setShowForm(false); setSelectedCandidate(null); }}>Hủy</button>
              </div>
              {/* Quản lý CV của ứng viên */}
              {selectedCandidate && (
                <div className="mt-8">
                  <h4 className="font-bold mb-2 flex items-center gap-2"><DocumentArrowDownIcon className="w-5 h-5 text-primary" />CV của ứng viên</h4>
                  <button className="mb-2 px-3 py-1 bg-green-600 text-white rounded flex items-center gap-1" type="button" onClick={() => setShowResumeForm(true)}><PlusIcon className="w-4 h-4" /> Thêm CV</button>
                  <ul className="list-disc ml-6">
                    {resumes.map(r => (
                      <li key={r.id} className="mb-1 flex items-center gap-2">
                        <span>{r.title}</span>
                        <button className="text-blue-600 underline flex items-center gap-1" type="button" onClick={() => handleViewResume(r.id, r.title)}><EyeIcon className="w-4 h-4" />Xem</button>
                        <button className="text-red-500 underline flex items-center gap-1" type="button" onClick={() => handleDeleteResume(r.id)}><TrashIcon className="w-4 h-4" />Xóa</button>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </form>
          </div>
        )}
        {/* Form thêm CV */}
        {showResumeForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md relative" onSubmit={handleAddResume}>
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setShowResumeForm(false)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h4 className="font-bold mb-4 flex items-center gap-2"><PlusIcon className="w-5 h-5 text-primary" />Thêm CV mới</h4>
              <input className="border p-2 rounded mb-2 w-full" placeholder="Tiêu đề CV" value={resumeTitle} onChange={e => setResumeTitle(e.target.value)} required />
              <input className="border p-2 rounded mb-2 w-full" type="file" accept="application/pdf" onChange={e => setResumeFile(e.target.files[0])} required />
              <div className="flex gap-2 justify-end">
                <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" type="submit"><CheckIcon className="w-5 h-5" /> Thêm</button>
                <button className="px-4 py-2 bg-gray-300 rounded font-semibold" type="button" onClick={() => setShowResumeForm(false)}>Hủy</button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
