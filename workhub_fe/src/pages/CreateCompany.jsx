import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createCompanyByRecruiter } from '../apiService';

export default function CreateCompany() {
  const [form, setForm] = useState({ name: '', address: '', description: '', website: '', industry: '' });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const userId = token ? JSON.parse(atob(token.split('.')[1])).id : null;

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setMessage('');
    try {
      const { name, address, description, website, industry } = form;
      const payloadData = { name, address, description, website, industry, inspection: 'none', inspectionStatus: 'pending', status: 'active' };
      await createCompanyByRecruiter(userId, payloadData, { headers: { Authorization: `Bearer ${token}` } });
      setMessage('Tạo công ty thành công!');
      // Cập nhật lại role trong localStorage (nếu có token)
      if (token) {
        try {
          const userPayload = JSON.parse(atob(token.split('.')[1]));
          userPayload.role = 'recruiter';
          localStorage.setItem('user', JSON.stringify(userPayload));
        } catch (e) {}
      }
      setTimeout(() => window.location.href = '/', 1200);
    } catch (err) {
      setMessage('Tạo công ty thất bại!');
      console.error('Lỗi tạo công ty:', err, { userId, token, payload: form });
      if (err?.response?.data?.message) {
        alert('Chi tiết lỗi: ' + err.response.data.message);
      }
    }
    setLoading(false);
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <div className="bg-white rounded-xl shadow p-8 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-4 text-blue-700">Thành lập công ty mới</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          <input className="w-full border p-2 rounded" name="name" placeholder="Tên công ty" value={form.name} onChange={handleChange} required />
          <input className="w-full border p-2 rounded" name="address" placeholder="Địa chỉ" value={form.address} onChange={handleChange} required />
          <textarea className="w-full border p-2 rounded" name="description" placeholder="Mô tả công ty" value={form.description} onChange={handleChange} />
          <input className="w-full border p-2 rounded" name="industry" placeholder="Lĩnh vực hoạt động" value={form.industry} onChange={handleChange} required />
          <input className="w-full border p-2 rounded" name="website" placeholder="Website (nếu có)" value={form.website} onChange={handleChange} />
          <button className="w-full px-4 py-2 bg-blue-600 text-white rounded" type="submit" disabled={loading}>{loading ? 'Đang tạo...' : 'Tạo công ty'}</button>
        </form>
        {message && <div className="mt-4 text-center text-green-600 font-bold">{message}</div>}
      </div>
    </div>
  );
}
