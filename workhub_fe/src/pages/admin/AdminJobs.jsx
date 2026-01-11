import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAdminJobs, getJobById, getJobCategories, getJobTypes, getJobPositions, getRecruiters, updateAdminJob, createAdminJob, deleteAdminJob } from '../../apiService';
import { BriefcaseIcon, UserGroupIcon, BuildingOffice2Icon, XMarkIcon, PencilSquareIcon, TrashIcon, PlusIcon, EyeIcon, CheckIcon } from '@heroicons/react/24/outline';

export default function AdminJobs() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedJob, setSelectedJob] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ title: '', description: '', salaryRange: '', experience: '', location: '', recruiterId: '', categoryId: '', typeId: '', positionId: '' });
  const [editId, setEditId] = useState(null);
  const [recruiters, setRecruiters] = useState([]);
  const [categories, setCategories] = useState([]);
  const [types, setTypes] = useState([]);
  const [positions, setPositions] = useState([]);
  const [search, setSearch] = useState("");

  const navigate = useNavigate();

  // Lấy token từ localStorage
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  const fetchJobs = async () => {
    setLoading(true);
    try {
      const res = await getAdminJobs(config);
      setJobs(res.data);
    } catch (error) {
      if (error.response && error.response.status === 403) {
        navigate('/login');
      } else {
        setJobs([]);
      }
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchJobs();
    const fetchRecruiters = async () => {
      try { const res = await getRecruiters(config); setRecruiters(res.data); } catch {}
    };
    const fetchCategories = async () => {
      try { const res = await getJobCategories(config); setCategories(res.data); } catch {}
    };
    const fetchTypes = async () => {
      try { const res = await getJobTypes(config); setTypes(res.data); } catch {}
    };
    const fetchPositions = async () => {
      try { const res = await getJobPositions(config); setPositions(res.data); } catch {}
    };
    fetchRecruiters();
    fetchCategories();
    fetchTypes();
    fetchPositions();
  }, []);

  const handleView = async (id) => {
    const res = await getJobById(id, config);
    setSelectedJob(res.data);
  };

  // CRUD handlers
  const handleEdit = async (id) => {
    setEditId(id);
    setShowForm(true);
    const res = await getJobById(id, config);
    setFormData({
      title: res.data.title || '',
      description: res.data.description || '',
      salaryRange: res.data.salaryRange || '',
      experience: res.data.experience || '',
      location: res.data.location || '',
      recruiterId: res.data.recruiterId ? String(res.data.recruiterId) : (res.data.recruiter?.id ? String(res.data.recruiter.id) : ''),
      categoryId: res.data.categoryId ? String(res.data.categoryId) : (res.data.category?.id ? String(res.data.category.id) : ''),
      typeId: res.data.typeId ? String(res.data.typeId) : (res.data.type?.id ? String(res.data.type.id) : ''),
      positionId: res.data.positionId ? String(res.data.positionId) : (res.data.position?.id ? String(res.data.position.id) : '')
    });
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc muốn xóa job này?')) {
      await deleteAdminJob(id, config);
      fetchJobs();
    }
  };

  const buildJobPayload = (formData) => ({
    title: formData.title,
    description: formData.description,
    salaryRange: formData.salaryRange,
    experience: formData.experience,
    location: formData.location,
    recruiter: formData.recruiterId ? { id: Number(formData.recruiterId) } : null,
    category: formData.categoryId ? { id: Number(formData.categoryId) } : null,
    type: formData.typeId ? { id: Number(formData.typeId) } : null,
    position: formData.positionId ? { id: Number(formData.positionId) } : null
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = buildJobPayload(formData);
    if (editId) {
      await updateAdminJob(editId, payload, config);
    } else {
      await createAdminJob(payload, config);
    }
    setShowForm(false);
    setEditId(null);
    setFormData({ title: '', description: '', salaryRange: '', experience: '', location: '', recruiterId: '', categoryId: '', typeId: '', positionId: '' });
    fetchJobs();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-extrabold text-blue-700 flex items-center gap-2">
            <BriefcaseIcon className="w-8 h-8 text-primary" /> Quản lý công việc
          </h2>
        </div>
        <div className="flex items-center mb-4">
          <input
            type="text"
            className="w-full max-w-xs px-4 py-2 border rounded shadow-sm focus:ring-2 focus:ring-primary focus:outline-none"
            placeholder="Tìm kiếm theo tiêu đề, vị trí, địa điểm..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div className="flex items-center justify-between mb-8">
          <button
            className="flex items-center gap-2 bg-primary text-white px-5 py-2 rounded-lg font-semibold shadow hover:bg-accent transition"
            onClick={() => { setShowForm(true); setEditId(null); setFormData({ title: '', description: '', salaryRange: '', experience: '', location: '', recruiterId: '', categoryId: '', typeId: '', positionId: '' }); }}
          >
            <PlusIcon className="w-5 h-5" /> Thêm mới
          </button>
        </div>
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-2xl relative" onSubmit={handleSubmit}>
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setShowForm(false)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-6 text-primary">{editId ? 'Cập nhật công việc' : 'Thêm công việc mới'}</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block font-semibold mb-1">Tiêu đề</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Tiêu đề" value={formData.title} onChange={e => setFormData(f => ({ ...f, title: e.target.value }))} required />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Lương</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Lương" value={formData.salaryRange} onChange={e => setFormData(f => ({ ...f, salaryRange: e.target.value }))} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Kinh nghiệm</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Kinh nghiệm" value={formData.experience} onChange={e => setFormData(f => ({ ...f, experience: e.target.value }))} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Địa điểm</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Địa điểm" value={formData.location} onChange={e => setFormData(f => ({ ...f, location: e.target.value }))} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Nhà tuyển dụng</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.recruiterId} onChange={e => setFormData(f => ({ ...f, recruiterId: e.target.value }))} required>
                    <option value="">Chọn nhà tuyển dụng</option>
                    {recruiters.map(r => <option key={r.id} value={r.id}>{r.fullname}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Ngành nghề</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.categoryId} onChange={e => setFormData(f => ({ ...f, categoryId: e.target.value }))} required>
                    <option value="">Chọn ngành nghề</option>
                    {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Loại công việc</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.typeId} onChange={e => setFormData(f => ({ ...f, typeId: e.target.value }))} required>
                    <option value="">Chọn loại công việc</option>
                    {types.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Vị trí</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.positionId} onChange={e => setFormData(f => ({ ...f, positionId: e.target.value }))} required>
                    <option value="">Chọn vị trí</option>
                    {positions.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
                <div className="md:col-span-2">
                  <label className="block font-semibold mb-1">Mô tả</label>
                  <textarea className="w-full px-4 py-2 border rounded" placeholder="Mô tả" value={formData.description} onChange={e => setFormData(f => ({ ...f, description: e.target.value }))} />
                </div>
              </div>
              <div className="mt-6 flex gap-2 justify-end">
                <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" type="submit">
                  <CheckIcon className="w-5 h-5" /> {editId ? 'Cập nhật' : 'Thêm mới'}
                </button>
                <button className="px-4 py-2 bg-gray-300 rounded font-semibold" type="button" onClick={() => { setShowForm(false); setEditId(null); }}>Hủy</button>
              </div>
            </form>
          </div>
        )}
        <div className="bg-white rounded-2xl shadow-xl p-6 overflow-x-auto">
          {loading ? (
            <div className="text-center py-8 text-lg">Đang tải...</div>
          ) : (
            <table className="min-w-full divide-y divide-gray-200">
              <thead>
                <tr className="bg-blue-50">
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Tiêu đề</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Vị trí</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Địa điểm</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Nhà tuyển dụng</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                  <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {jobs.filter(job =>
                  job.title?.toLowerCase().includes(search.toLowerCase()) ||
                  job.position?.name?.toLowerCase().includes(search.toLowerCase()) ||
                  job.location?.toLowerCase().includes(search.toLowerCase())
                ).map(job => (
                  <tr key={job.id} className="hover:bg-blue-50 transition">
                    <td className="px-4 py-2">{job.title}</td>
                    <td className="px-4 py-2">{job.position?.name}</td>
                    <td className="px-4 py-2">{job.location}</td>
                    <td className="px-4 py-2">{job.recruiter?.fullname}</td>
                    <td className="px-4 py-2">{job.status}</td>
                    <td className="px-4 py-2 flex gap-2 justify-center">
                      <button className="p-2 rounded hover:bg-blue-100" onClick={() => handleEdit(job.id)} title="Sửa">
                        <PencilSquareIcon className="w-5 h-5 text-blue-600" />
                      </button>
                      <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDelete(job.id)} title="Xóa">
                        <TrashIcon className="w-5 h-5 text-red-600" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
        {/* Chi tiết job */}
        {selectedJob && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg relative">
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setSelectedJob(null)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-4 text-primary flex items-center gap-2">
                <BriefcaseIcon className="w-6 h-6 text-primary" /> {selectedJob.title}
              </h3>
              <div className="space-y-2">
                <div><b>Nhà tuyển dụng:</b> {selectedJob.recruiterFullname || selectedJob.recruiter?.fullname || '-'}</div>
                <div><b>Ngành nghề:</b> {selectedJob.category || '-'}</div>
                <div><b>Loại:</b> {selectedJob.type || '-'}</div>
                <div><b>Vị trí:</b> {selectedJob.position || '-'}</div>
                <div><b>Mô tả:</b> {selectedJob.description}</div>
                <div><b>Lương:</b> {selectedJob.salaryRange}</div>
                <div><b>Kinh nghiệm:</b> {selectedJob.experience}</div>
                <div><b>Địa điểm:</b> {selectedJob.location}</div>
                <div><b>Hạn nộp:</b> {selectedJob.deadline}</div>
              </div>
              <button className="mt-6 px-4 py-2 bg-gray-200 rounded font-semibold w-full" onClick={() => setSelectedJob(null)}>Đóng</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
