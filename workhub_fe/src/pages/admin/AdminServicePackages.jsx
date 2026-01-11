import React, { useEffect, useState } from 'react';
import { getServicePackages, createServicePackage, updateServicePackage, deleteServicePackage } from '../../apiService';
import { GiftIcon, PencilSquareIcon, TrashIcon, XMarkIcon, CheckIcon, PlusIcon } from '@heroicons/react/24/outline';

export default function AdminServicePackages() {
  const [packages, setPackages] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);
  const [formData, setFormData] = useState({ name: '', price: '', duration: '', description: '', status: 'active', jobPostLimit: 5, cvLimit: 5, postAt: 'standard' });
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  const fetchPackages = async () => {
    const res = await getServicePackages(config);
    setPackages(res.data);
  };
  useEffect(() => { fetchPackages(); }, []);

  const handleEdit = (pkg) => {
    setEditId(pkg.id);
    setFormData({
      name: pkg.name || '',
      price: pkg.price || '',
      duration: pkg.duration || '',
      description: pkg.description || '',
      status: pkg.status || 'active',
      jobPostLimit: pkg.jobPostLimit || 5,
      cvLimit: pkg.cvLimit || 5,
      postAt: pkg.postAt || 'standard',
    });
    setShowForm(true);
  };
  const handleAdd = () => {
    setEditId(null);
    setFormData({ name: '', price: '', duration: '', description: '', status: 'active', jobPostLimit: 5, cvLimit: 5, postAt: 'standard' });
    setShowForm(true);
  };
  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc muốn xóa gói này?')) return;
    await deleteServicePackage(id, config);
    fetchPackages();
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      price: formData.price ? parseFloat(formData.price) : 0,
      duration: formData.duration ? parseInt(formData.duration) : 0,
      jobPostLimit: formData.jobPostLimit ? parseInt(formData.jobPostLimit) : 5,
      cvLimit: formData.cvLimit ? parseInt(formData.cvLimit) : 5,
      postAt: formData.postAt,
      createdAt: new Date().toISOString(),
    };
    if (editId) {
      await updateServicePackage(editId, payload, config);
    } else {
      await createServicePackage(payload, config);
    }
    setShowForm(false);
    setEditId(null);
    setFormData({ name: '', price: '', duration: '', description: '', status: 'active', jobPostLimit: 5, cvLimit: 5, postAt: 'standard' });
    fetchPackages();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center gap-2 mb-8">
          <GiftIcon className="w-8 h-8 text-primary" />
          <h2 className="text-3xl font-extrabold text-blue-700">Quản lý gói dịch vụ</h2>
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-8">
          <button className="flex items-center gap-2 mb-6 bg-primary text-white px-5 py-2 rounded-lg font-semibold shadow hover:bg-accent transition" onClick={handleAdd} type="button">
            <PlusIcon className="w-5 h-5" /> Thêm gói mới
          </button>
          {showForm && (
            <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
              <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg relative" onSubmit={handleSubmit}>
                <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setShowForm(false)}>
                  <XMarkIcon className="w-6 h-6 text-gray-500" />
                </button>
                <h3 className="text-xl font-bold mb-6 text-primary">{editId ? 'Cập nhật gói' : 'Thêm gói mới'}</h3>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block font-semibold mb-1">Tên gói</label>
                    <input className="w-full px-4 py-2 border rounded" name="name" placeholder="Tên gói" value={formData.name} onChange={e => setFormData(f => ({ ...f, name: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">Giá</label>
                    <input className="w-full px-4 py-2 border rounded" name="price" type="number" placeholder="Giá" value={formData.price} onChange={e => setFormData(f => ({ ...f, price: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">Thời hạn (ngày)</label>
                    <input className="w-full px-4 py-2 border rounded" name="duration" type="number" placeholder="Thời hạn (ngày)" value={formData.duration} onChange={e => setFormData(f => ({ ...f, duration: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">Giới hạn tin tuyển dụng</label>
                    <input className="w-full px-4 py-2 border rounded" name="jobPostLimit" type="number" placeholder="Giới hạn tin tuyển dụng" value={formData.jobPostLimit} onChange={e => setFormData(f => ({ ...f, jobPostLimit: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">Giới hạn CV</label>
                    <input className="w-full px-4 py-2 border rounded" name="cvLimit" type="number" placeholder="Giới hạn CV" value={formData.cvLimit} onChange={e => setFormData(f => ({ ...f, cvLimit: e.target.value }))} required />
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">Loại đăng</label>
                    <select className="w-full px-4 py-2 border rounded" name="postAt" value={formData.postAt} onChange={e => setFormData(f => ({ ...f, postAt: e.target.value }))} required>
                      <option value="standard">standard</option>
                      <option value="urgent">urgent</option>
                      <option value="proposal">proposal</option>
                    </select>
                  </div>
                  <div className="md:col-span-2">
                    <label className="block font-semibold mb-1">Mô tả</label>
                    <textarea className="w-full px-4 py-2 border rounded" name="description" placeholder="Mô tả" value={formData.description} onChange={e => setFormData(f => ({ ...f, description: e.target.value }))} />
                  </div>
                  <div className="md:col-span-2">
                    <label className="block font-semibold mb-1">Trạng thái</label>
                    <select className="w-full px-4 py-2 border rounded" name="status" value={formData.status} onChange={e => setFormData(f => ({ ...f, status: e.target.value }))}>
                      <option value="active">Hoạt động</option>
                      <option value="inactive">Ngừng hoạt động</option>
                    </select>
                  </div>
                </div>
                <div className="mt-6 flex gap-2 justify-end">
                  <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" type="submit">
                    <CheckIcon className="w-5 h-5" /> {editId ? 'Cập nhật' : 'Thêm mới'}
                  </button>
                  <button className="px-4 py-2 bg-gray-300 rounded font-semibold" type="button" onClick={() => setShowForm(false)}>Hủy</button>
                </div>
              </form>
            </div>
          )}
          <table className="min-w-full divide-y divide-gray-200">
            <thead>
              <tr className="bg-blue-50">
                <th className="px-4 py-3 text-left font-bold text-gray-700">ID</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Tên gói</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Giá</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Thời hạn (ngày)</th>
                <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {packages.map(pkg => (
                <tr key={pkg.id} className="hover:bg-blue-50 transition">
                  <td className="px-4 py-2">{pkg.id}</td>
                  <td className="px-4 py-2">{pkg.name}</td>
                  <td className="px-4 py-2">{pkg.price}</td>
                  <td className="px-4 py-2">{pkg.duration}</td>
                  <td className="px-4 py-2">
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${pkg.status === 'active' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>{pkg.status}</span>
                  </td>
                  <td className="px-4 py-2 flex gap-2 justify-center">
                    <button className="p-2 rounded hover:bg-yellow-100" onClick={() => handleEdit(pkg)} title="Sửa">
                      <PencilSquareIcon className="w-5 h-5 text-yellow-600" />
                    </button>
                    <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDelete(pkg.id)} title="Xóa">
                      <TrashIcon className="w-5 h-5 text-red-600" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
