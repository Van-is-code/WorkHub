import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { updateUserPackage, deleteUserPackage, createUserPackage } from '../../apiService';
import { getUsers, getServicePackages } from '../../apiService';
import { GiftIcon, UserGroupIcon, PencilSquareIcon, TrashIcon, XMarkIcon, CheckIcon, PlusIcon } from '@heroicons/react/24/outline';

const API_BASE_URL = 'http://localhost:8080/workhub/api/v1';

export default function AdminRecruiterPackages() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [editItem, setEditItem] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ userId: '', packageId: '', purchaseDate: '', expirationDate: '', status: 'active' });
  const [users, setUsers] = useState([]);
  const [packages, setPackages] = useState([]);
  const [search, setSearch] = useState("");
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    setLoading(true);
    Promise.all([
      axios.get(`${API_BASE_URL}/user-packages`, config),
      getUsers(config),
      getServicePackages(config)
    ])
      .then(([res, userRes, pkgRes]) => {
        setData(res.data);
        setUsers(userRes.data);
        setPackages(pkgRes.data);
      })
      .catch(() => {
        setData([]);
        setUsers([]);
        setPackages([]);
      })
      .finally(() => setLoading(false));
  }, []);

  const handleEdit = (item) => {
    setEditItem(item);
    setForm({
      userId: item.user?.id || '',
      packageId: item.servicePackage?.id || '',
      purchaseDate: item.purchaseDate?.slice(0, 10) || '',
      expirationDate: item.expirationDate?.slice(0, 10) || '',
      status: item.status || 'active',
    });
    setShowForm(true);
  };
  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc muốn xóa?')) return;
    await deleteUserPackage(id, config);
    window.location.reload();
  };
  const handleAdd = () => {
    setEditItem(null);
    setForm({ userId: '', packageId: '', purchaseDate: '', expirationDate: '', status: 'active' });
    setShowForm(true);
  };
  const handleSubmit = async (e) => {
    e.preventDefault();
    // Lấy thông tin gói để tự động điền price, description
    const selectedPackage = packages.find(p => String(p.id) === String(form.packageId));
    const payload = {
      user: { id: form.userId },
      servicePackage: { id: form.packageId },
      purchaseDate: form.purchaseDate ? form.purchaseDate + 'T00:00:00' : null,
      expirationDate: form.expirationDate ? form.expirationDate + 'T00:00:00' : null,
      status: form.status,
      price: selectedPackage ? selectedPackage.price : 0,
      description: selectedPackage ? selectedPackage.description : '',
    };
    if (editItem) {
      await updateUserPackage(editItem.id, payload, config);
    } else {
      await createUserPackage(payload, config);
    }
    setShowForm(false);
    window.location.reload();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-5xl mx-auto">
        <div className="flex items-center gap-2 mb-8">
          <GiftIcon className="w-8 h-8 text-primary" />
          <h2 className="text-3xl font-extrabold text-blue-700">Quản lý tài khoản recruiter đã mua gói</h2>
        </div>
        <div className="flex items-center mb-4">
          <input
            type="text"
            className="w-full max-w-xs px-4 py-2 border rounded shadow-sm focus:ring-2 focus:ring-primary focus:outline-none"
            placeholder="Tìm kiếm theo tên, email, gói dịch vụ..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-8 overflow-x-auto">
          <button className="flex items-center gap-2 mb-6 bg-primary text-white px-5 py-2 rounded-lg font-semibold shadow hover:bg-accent transition" onClick={handleAdd} type="button">
            <PlusIcon className="w-5 h-5" /> Thêm mới
          </button>
          {loading ? <div className="text-center py-8 text-lg">Đang tải...</div> : (
            <table className="min-w-full divide-y divide-gray-200">
              <thead>
                <tr className="bg-blue-50">
                  <th className="px-4 py-3 text-left font-bold text-gray-700">ID</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Recruiter</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Email</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Gói dịch vụ</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Ngày mua</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Ngày hết hạn</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                  <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {data.filter(up => up.role === 'recruiter')
                  .filter(up =>
                    (up.fullname || '').toLowerCase().includes(search.toLowerCase()) ||
                    (up.email || '').toLowerCase().includes(search.toLowerCase()) ||
                    (up.servicePackageName || '').toLowerCase().includes(search.toLowerCase())
                  )
                  .map(up => (
                    <tr key={up.id} className="hover:bg-blue-50 transition">
                      <td className="px-4 py-2">{up.userId}</td>
                      <td className="px-4 py-2">{up.fullname}</td>
                      <td className="px-4 py-2">{up.email}</td>
                      <td className="px-4 py-2">{up.servicePackageName}</td>
                      <td className="px-4 py-2">{up.purchaseDate?.slice(0,10)}</td>
                      <td className="px-4 py-2">{up.expirationDate?.slice(0,10)}</td>
                      <td className="px-4 py-2">
                        <span className={`px-3 py-1 rounded-full text-xs font-semibold ${up.status === 'active' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>{up.status}</span>
                      </td>
                      <td className="px-4 py-2 flex gap-2 justify-center">
                        <button className="p-2 rounded hover:bg-blue-100" onClick={() => handleEdit(up)} title="Sửa">
                          <PencilSquareIcon className="w-5 h-5 text-blue-600" />
                        </button>
                        <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDelete(up.id)} title="Xóa">
                          <TrashIcon className="w-5 h-5 text-red-600" />
                        </button>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          )}
        </div>
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-lg relative" onSubmit={handleSubmit}>
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setShowForm(false)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-6 text-primary">{editItem ? 'Cập nhật gói recruiter' : 'Thêm gói recruiter mới'}</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block font-semibold mb-1">Recruiter</label>
                  <select className="w-full px-4 py-2 border rounded" name="userId" value={form.userId} onChange={e => setForm(f => ({ ...f, userId: e.target.value }))} required>
                    <option value="">Chọn user</option>
                    {users.map(u => <option key={u.id} value={u.id}>{u.fullname} ({u.email}) - {u.role}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Gói dịch vụ</label>
                  <select className="w-full px-4 py-2 border rounded" name="packageId" value={form.packageId} onChange={e => setForm(f => ({ ...f, packageId: e.target.value }))} required>
                    <option value="">Chọn gói dịch vụ</option>
                    {packages.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block font-semibold mb-1">Ngày mua</label>
                  <input className="w-full px-4 py-2 border rounded" name="purchaseDate" type="date" placeholder="Ngày mua" value={form.purchaseDate} onChange={e => setForm(f => ({ ...f, purchaseDate: e.target.value }))} required />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Ngày hết hạn</label>
                  <input className="w-full px-4 py-2 border rounded" name="expirationDate" type="date" placeholder="Ngày hết hạn" value={form.expirationDate} onChange={e => setForm(f => ({ ...f, expirationDate: e.target.value }))} required />
                </div>
                <div className="md:col-span-2">
                  <label className="block font-semibold mb-1">Trạng thái</label>
                  <select className="w-full px-4 py-2 border rounded" name="status" value={form.status} onChange={e => setForm(f => ({ ...f, status: e.target.value }))} required>
                    <option value="active">active</option>
                    <option value="expired">expired</option>
                    <option value="cancelled">cancelled</option>
                  </select>
                </div>
              </div>
              <div className="mt-6 flex gap-2 justify-end">
                <button className="px-4 py-2 bg-primary text-white rounded font-semibold flex items-center gap-1" type="submit">
                  <CheckIcon className="w-5 h-5" /> {editItem ? 'Cập nhật' : 'Thêm mới'}
                </button>
                <button className="px-4 py-2 bg-gray-300 rounded font-semibold" type="button" onClick={() => setShowForm(false)}>Hủy</button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
