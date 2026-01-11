import React, { useEffect, useState } from 'react';
import { getCompanies, getCompanyById, getRecruiters, createCompanyByRecruiter, updateCompanyById, deleteCompanyById } from '../../apiService';
import { BuildingOffice2Icon, UserGroupIcon, XMarkIcon, PencilSquareIcon, TrashIcon, PlusIcon, CheckIcon } from '@heroicons/react/24/outline';

export default function AdminCompanyManager() {
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [recruiters, setRecruiters] = useState([]);
  const [formData, setFormData] = useState({ recruiterId: '', name: '', industry: '', location: '', description: '', website: '', status: 'active', inspection: 'none' });
  const [editMode, setEditMode] = useState(false);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const fetchCompanies = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem('token');
        const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
        const res = await getCompanies(config);
        setCompanies(res.data);
      } catch {
        setCompanies([]);
      }
      setLoading(false);
    };
    const fetchRecruiters = async () => {
      const token = localStorage.getItem('token');
      const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
      const res = await getRecruiters(config);
      setRecruiters(res.data);
    };
    fetchCompanies();
    fetchRecruiters();
  }, []);

  const handleSelectCompany = async (companyId) => {
    setLoading(true);
    try {
      const token = localStorage.getItem('token');
      const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
      const res = await getCompanyById(companyId, config);
      setSelectedCompany(res.data);
    } catch {
      setSelectedCompany(null);
    }
    setLoading(false);
  };

  const handleCreateCompany = async (e) => {
    e.preventDefault();
    if (!formData.recruiterId) return alert('Vui lòng chọn nhà tuyển dụng');
    const token = localStorage.getItem('token');
    const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
    await createCompanyByRecruiter(formData.recruiterId, formData, config);
    setShowForm(false);
    setFormData({ recruiterId: '', name: '', industry: '', location: '', description: '', website: '', status: 'active', inspection: 'none' });
    // Refresh list
    const res = await getCompanies(config);
    setCompanies(res.data);
  };

  const handleEditCompany = () => {
    setEditMode(true);
    setFormData({
      recruiterId: selectedCompany.recruiter?.id || '',
      name: selectedCompany.name || '',
      industry: selectedCompany.industry || '',
      location: selectedCompany.location || '',
      description: selectedCompany.description || '',
      website: selectedCompany.website || '',
      status: selectedCompany.status || 'active',
      inspection: selectedCompany.inspection || 'none',
    });
    setShowForm(true);
  };

  const handleUpdateCompany = async (e) => {
    e.preventDefault();
    if (!selectedCompany) return;
    const token = localStorage.getItem('token');
    const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
    await updateCompanyById(selectedCompany.id, formData, config);
    setEditMode(false);
    setShowForm(false);
    setSelectedCompany(null);
    setFormData({ recruiterId: '', name: '', industry: '', location: '', description: '', website: '', status: 'active', inspection: 'none' });
    const res = await getCompanies(config);
    setCompanies(res.data);
  };

  const handleDeleteCompany = async (companyId) => {
    if (!window.confirm('Bạn có chắc muốn xóa công ty này?')) return;
    const token = localStorage.getItem('token');
    const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
    await deleteCompanyById(companyId, config);
    setSelectedCompany(null);
    const res = await getCompanies(config);
    setCompanies(res.data);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-extrabold text-blue-700 flex items-center gap-2">
            <BuildingOffice2Icon className="w-8 h-8 text-primary" /> Quản lý công ty
          </h2>
          <button
            className="flex items-center gap-2 bg-primary text-white px-5 py-2 rounded-lg font-semibold shadow hover:bg-accent transition"
            onClick={() => setShowForm(f => !f)}
          >
            <PlusIcon className="w-5 h-5" /> {showForm ? 'Đóng' : 'Tạo công ty mới'}
          </button>
        </div>
        <div className="flex items-center mb-4">
          <input
            type="text"
            className="w-full max-w-xs px-4 py-2 border rounded shadow-sm focus:ring-2 focus:ring-primary focus:outline-none"
            placeholder="Tìm kiếm theo tên, ngành, địa điểm..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-2xl relative" onSubmit={editMode ? handleUpdateCompany : handleCreateCompany}>
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setShowForm(false)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-6 text-primary">{editMode ? 'Cập nhật công ty' : 'Tạo công ty mới'}</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block font-semibold mb-1">Nhà tuyển dụng</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.recruiterId} onChange={e => setFormData(f => ({ ...f, recruiterId: e.target.value }))} required>
                    <option value="">Chọn nhà tuyển dụng</option>
                    {recruiters.map(r => <option key={r.id} value={r.id}>{r.fullname} ({r.email})</option>)}
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Tên công ty</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Tên công ty" value={formData.name} onChange={e => setFormData(f => ({ ...f, name: e.target.value }))} required />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Ngành nghề</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Ngành nghề" value={formData.industry} onChange={e => setFormData(f => ({ ...f, industry: e.target.value }))} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Địa chỉ</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Địa chỉ" value={formData.location} onChange={e => setFormData(f => ({ ...f, location: e.target.value }))} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Website</label>
                  <input className="w-full px-4 py-2 border rounded" placeholder="Website" value={formData.website} onChange={e => setFormData(f => ({ ...f, website: e.target.value }))} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Trạng thái</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.status} onChange={e => setFormData(f => ({ ...f, status: e.target.value }))}>
                    <option value="active">Hoạt động</option>
                    <option value="pending">Chờ duyệt</option>
                    <option value="inactive">Ngừng hoạt động</option>
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Kiểm định</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.inspection} onChange={e => setFormData(f => ({ ...f, inspection: e.target.value }))}>
                    <option value="none">Chưa kiểm định</option>
                    <option value="prestige">Uy tín</option>
                  </select>
                </div>
                <div className="md:col-span-2">
                  <label className="block font-semibold mb-1">Mô tả</label>
                  <textarea className="w-full px-4 py-2 border rounded" placeholder="Mô tả" value={formData.description} onChange={e => setFormData(f => ({ ...f, description: e.target.value }))} />
                </div>
              </div>
              <div className="mt-6 flex gap-2 justify-end">
                <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" type="submit">
                  <CheckIcon className="w-5 h-5" /> {editMode ? 'Cập nhật công ty' : 'Tạo công ty'}
                </button>
                <button className="px-4 py-2 bg-gray-300 rounded font-semibold" type="button" onClick={() => setShowForm(false)}>Hủy</button>
              </div>
            </form>
          </div>
        )}
        <div className="bg-white rounded-2xl shadow-xl p-6 overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead>
              <tr className="bg-blue-50">
                <th className="px-4 py-3 text-left font-bold text-gray-700">Tên công ty</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Ngành</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Địa điểm</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {companies.filter(company =>
                company.name?.toLowerCase().includes(search.toLowerCase()) ||
                company.industry?.toLowerCase().includes(search.toLowerCase()) ||
                company.location?.toLowerCase().includes(search.toLowerCase())
              ).map(company => (
                <tr key={company.id} className="hover:bg-blue-50 transition">
                  <td className="px-4 py-2">{company.name}</td>
                  <td className="px-4 py-2">{company.industry}</td>
                  <td className="px-4 py-2">{company.location}</td>
                  <td className="px-4 py-2">{company.status}</td>
                  <td className="px-4 py-2 flex gap-2 justify-center">
                    <button className="p-2 rounded hover:bg-blue-100" onClick={() => handleEditCompany(company)} title="Sửa">
                      <PencilSquareIcon className="w-5 h-5 text-blue-600" />
                    </button>
                    <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDeleteCompany(company.id)} title="Xóa">
                      <TrashIcon className="w-5 h-5 text-red-600" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {selectedCompany && !showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg relative">
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setSelectedCompany(null)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-4 text-primary flex items-center gap-2">
                <BuildingOffice2Icon className="w-6 h-6 text-primary" /> {selectedCompany.name}
              </h3>
              <div className="space-y-2">
                <div><b>ID:</b> {selectedCompany.id}</div>
                <div><b>Tên công ty:</b> {selectedCompany.name}</div>
                <div><b>Email recruiter:</b> {selectedCompany.recruiter?.email || '-'}</div>
                <div><b>Recruiter ID:</b> {selectedCompany.recruiter?.id || '-'}</div>
                <div><b>Industry:</b> {selectedCompany.industry || '-'}</div>
                <div><b>Location:</b> {selectedCompany.location || '-'}</div>
                <div><b>Description:</b> {selectedCompany.description || '-'}</div>
                <div><b>Website:</b> {selectedCompany.website || '-'}</div>
                <div><b>Inspection Status:</b> {selectedCompany.inspectionStatus || '-'}</div>
                <div><b>Inspection:</b> {selectedCompany.inspection || '-'}</div>
                <div><b>Status:</b> {selectedCompany.status || '-'}</div>
                <div><b>Created At:</b> {selectedCompany.createdAt || '-'}</div>
                <div><b>Logo:</b> {selectedCompany.logo ? <span className="text-green-600">Có file</span> : 'Không có'}</div>
                <div className="flex gap-2 mt-4">
                  <button className="p-2 rounded hover:bg-yellow-100" onClick={handleEditCompany} title="Sửa">
                    <PencilSquareIcon className="w-5 h-5 text-yellow-600" />
                  </button>
                  <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDeleteCompany(selectedCompany.id)} title="Xóa">
                    <TrashIcon className="w-5 h-5 text-red-600" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
      {/* Kết thúc max-w-6xl mx-auto */}
    </div>
    // Kết thúc min-h-screen ...
  );
}
