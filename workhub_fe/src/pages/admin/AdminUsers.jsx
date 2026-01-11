import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUsers, createUser, updateUser, deleteUser, getUserById } from '../../apiService';
import { UserPlusIcon, PencilSquareIcon, TrashIcon, XMarkIcon, CheckIcon } from '@heroicons/react/24/outline';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({ fullname: '', email: '', password: '', phone: '', role: 'candidate', status: 'verified' });
  const [editId, setEditId] = useState(null);
  const [search, setSearch] = useState("");

  const navigate = useNavigate();

  // Lấy token từ localStorage
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  const fetchUsers = async () => {
    setLoading(true);
    try {
      // Lấy toàn bộ user, không truyền params role
      const res = await getUsers(config);
      setUsers(res.data);
    } catch (error) {
      if (error.response && error.response.status === 403) {
        navigate('/login');
      } else {
        setUsers([]);
      }
    }
    setLoading(false);
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleEdit = async (id) => {
    setEditId(id);
    setShowForm(true);
    const res = await getUserById(id, config);
    setFormData({ ...res.data, password: '' });
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc muốn xóa user này?')) {
      await deleteUser(id, config);
      fetchUsers();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Nếu là cập nhật, chỉ gửi password nếu user nhập password mới (không rỗng)
    let submitData = { ...formData };
    if (editId && !formData.password) {
      // Nếu không đổi password, loại bỏ trường password khỏi payload
      const { password, ...rest } = submitData;
      submitData = rest;
    }
    if (editId) {
      await updateUser(editId, submitData, config);
    } else {
      await createUser(submitData, config);
    }
    setShowForm(false);
    setEditId(null);
    setFormData({ fullname: '', email: '', password: '', phone: '', role: 'candidate', status: 'verified' });
    fetchUsers();
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <h2 className="text-3xl font-extrabold text-blue-700">Quản lý người dùng</h2>
          <button
            className="flex items-center gap-2 bg-primary text-white px-5 py-2 rounded-lg font-semibold shadow hover:bg-accent transition"
            onClick={() => { setShowForm(true); setEditId(null); setFormData({ fullname: '', email: '', password: '', phone: '', role: 'candidate', status: 'verified' }); }}
          >
            <UserPlusIcon className="w-5 h-5" /> Thêm mới
          </button>
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
          {loading ? (
            <div className="text-center py-8 text-lg">Đang tải...</div>
          ) : (
            <table className="min-w-full divide-y divide-gray-200">
              <thead>
                <tr className="bg-blue-50">
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Họ tên</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Email</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Số điện thoại</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Vai trò</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                  <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {users.filter(user =>
                  user.fullname?.toLowerCase().includes(search.toLowerCase()) ||
                  user.email?.toLowerCase().includes(search.toLowerCase()) ||
                  user.phone?.toLowerCase().includes(search.toLowerCase())
                ).map(user => (
                  <tr key={user.id} className="hover:bg-blue-50 transition">
                    <td className="px-4 py-2">{user.fullname}</td>
                    <td className="px-4 py-2">{user.email}</td>
                    <td className="px-4 py-2">{user.phone}</td>
                    <td className="px-4 py-2 capitalize">{user.role}</td>
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
          )}
        </div>
        {/* Form thêm/sửa user */}
        {showForm && (
          <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
            <form className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md relative" onSubmit={handleSubmit}>
              <button type="button" className="absolute top-3 right-3 p-1 hover:bg-gray-100 rounded" onClick={() => setShowForm(false)}>
                <XMarkIcon className="w-6 h-6 text-gray-500" />
              </button>
              <h3 className="text-xl font-bold mb-6 text-primary">{editId ? 'Cập nhật người dùng' : 'Thêm người dùng mới'}</h3>
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
                  <label className="block font-semibold mb-1">Mật khẩu {editId && <span className="text-xs text-gray-400">(Để trống nếu không đổi)</span>}</label>
                  <input type="password" className="w-full px-4 py-2 border rounded" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} placeholder={editId ? '••••••••' : ''} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Số điện thoại</label>
                  <input type="text" className="w-full px-4 py-2 border rounded" value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })} />
                </div>
                <div>
                  <label className="block font-semibold mb-1">Vai trò</label>
                  <select className="w-full px-4 py-2 border rounded" value={formData.role} onChange={e => setFormData({ ...formData, role: e.target.value })}>
                    <option value="candidate">Ứng viên</option>
                    <option value="recruiter">Nhà tuyển dụng</option>
                    <option value="admin">Admin</option>
                  </select>
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
              <div className="flex justify-end gap-4 mt-8">
                <button type="button" className="px-4 py-2 rounded bg-gray-200 text-gray-700 font-semibold hover:bg-gray-300 flex items-center gap-2" onClick={() => setShowForm(false)}>
                  <XMarkIcon className="w-5 h-5" /> Hủy
                </button>
                <button type="submit" className="px-4 py-2 rounded bg-primary text-white font-semibold hover:bg-accent flex items-center gap-2">
                  <CheckIcon className="w-5 h-5" /> {editId ? 'Cập nhật' : 'Thêm mới'}
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
