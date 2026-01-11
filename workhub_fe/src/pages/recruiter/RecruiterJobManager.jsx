import React, { useEffect, useState } from 'react';
import { getJobs, createJob, getJobCategories, getJobTypes, getJobPositions, getUserPackages, deleteJob } from '../../apiService';
import { Link } from 'react-router-dom';

export default function RecruiterJobManager() {
  const [jobs, setJobs] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    title: '',
    description: '',
    jobType: '',
    salary: '',
    location: '',
    portAt: '',
    category: '',
    type: '',
    position: '',
    experience: '',
    salaryRange: ''
  });
  const [userPackages, setUserPackages] = useState([]);
  const [categories, setCategories] = useState([]);
  const [types, setTypes] = useState([]);
  const [positions, setPositions] = useState([]);
  const [error, setError] = useState(null);
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    fetchJobs();
    fetchUserPackages();
    getJobCategories(config).then(res => setCategories(res.data || []));
    getJobTypes(config).then(res => setTypes(res.data || []));
    getJobPositions(config).then(res => setPositions(res.data || []));
  }, []);

  const fetchJobs = async () => {
    try {
      const res = await getJobs('', config);
      console.log('DEBUG jobs:', res.data);
      setJobs(res.data); // Bỏ filter isMine để hiển thị tất cả job
    } catch (err) {
      setError('Lỗi khi tải danh sách job: ' + (err?.response?.data?.message || err.message));
      setJobs([]);
      console.error('Lỗi khi fetch jobs:', err);
    }
  };

  const fetchUserPackages = async () => {
    try {
      const token = localStorage.getItem('token');
      const user = token ? JSON.parse(atob(token.split('.')[1])) : null;
      if (!user?.id) return;
      const res = await getUserPackages(user.id, config);
      console.log('DEBUG userPackages:', res.data); // Log tổng thể
      if (res.data && res.data.length > 0) {
        res.data.forEach((pkg, idx) => {
          console.log(`DEBUG servicePackage[${idx}]:`, pkg.servicePackage);
        });
      }
      setUserPackages(res.data || []);
      if (!res.data || res.data.length === 0) {
        setError('Bạn chưa có gói dịch vụ nào. Vui lòng mua gói để đăng tin tuyển dụng.');
      }
    } catch (err) {
      setError('Lỗi khi tải gói dịch vụ: ' + (err?.response?.data?.message || err.message));
      setUserPackages([]);
      console.error('Lỗi khi fetch userPackages:', err);
    }
  };

  // Helper: lấy danh sách postAt recruiter có quyền
  // ĐÃ KHÔNG DÙNG NỮA, giữ lại để tham khảo
  // const getAllowedPostAt = () => { ... }
  // const allowedPostAt = getAllowedPostAt();

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Sau khi tạo job thành công hoặc khi mở form, luôn fetch lại userPackages để lấy quyền mới nhất
    await fetchUserPackages();
    const payload = {
      ...form,
      postAt: form.portAt,
      salaryRange: form.salaryRange || form.salary
    };
    // Map các trường category, type, position thành object { id: value } nếu là số hoặc string
    ['category', 'type', 'position'].forEach(field => {
      if (payload[field]) {
        payload[field] = { id: Number(payload[field]) };
      }
    });
    // Loại bỏ các trường có giá trị là ""
    Object.keys(payload).forEach(key => {
      if (payload[key] === "") delete payload[key];
    });
    console.log('DEBUG submit payload:', payload);
    try {
      await createJob(payload, config);
      setShowForm(false);
      setForm({
        title: '',
        description: '',
        jobType: '',
        salary: '',
        location: '',
        portAt: '',
        category: '',
        type: '',
        position: '',
        experience: '',
        salaryRange: ''
      });
      fetchJobs();
    } catch (err) {
      console.error('Create job error:', err?.response?.data || err);
      alert('Tạo job thất bại: ' + (err?.response?.data?.message || err.message));
      // Nếu lỗi quota hoặc hết hạn gói, gợi ý mua thêm gói dịch vụ
      if (err?.response?.data?.message?.includes('quota') || err?.response?.data?.message?.includes('gói dịch vụ')) {
        setError('Bạn đã hết lượt đăng tin hoặc gói dịch vụ đã hết hạn. Vui lòng mua thêm gói để tiếp tục đăng tin.');
      }
    }
  };

  const handleDeleteJob = async (jobId) => {
    if (!window.confirm('Bạn có chắc chắn muốn xóa job này? Hành động này không thể hoàn tác!')) return;
    try {
      await deleteJob(jobId, config);
      fetchJobs();
    } catch (err) {
      alert('Xóa job thất bại: ' + (err?.response?.data?.message || err.message));
    }
  };

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <h2 className="text-3xl font-bold mb-8 text-primary text-center">Quản lý tin tuyển dụng</h2>
      {error && <div className="text-red-600 font-semibold mb-4 text-center">{error}</div>}
      <div className="flex justify-end mb-6">
        <button className="px-5 py-2 bg-primary text-white rounded-full font-semibold shadow hover:bg-accent transition" onClick={() => setShowForm(true)} disabled={userPackages.length === 0}>+ Thêm tin mới</button>
      </div>
      {showForm && (
        <form className="bg-white rounded-2xl shadow-card p-8 mb-8" onSubmit={handleSubmit}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <input className="border p-3 rounded" name="title" placeholder="Tiêu đề" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value.slice(0, 100) }))} required maxLength={100} />
            <input className="border p-3 rounded" name="jobType" placeholder="Loại công việc" value={form.jobType} onChange={e => setForm(f => ({ ...f, jobType: e.target.value }))} required />
            <input className="border p-3 rounded" name="salary" placeholder="Mức lương" value={form.salary} onChange={e => setForm(f => ({ ...f, salary: e.target.value }))} required />
            <input className="border p-3 rounded" name="location" placeholder="Địa điểm" value={form.location} onChange={e => setForm(f => ({ ...f, location: e.target.value }))} required />
            <select className="border p-3 rounded" name="portAt" value={form.portAt} onChange={e => setForm(f => ({ ...f, portAt: e.target.value }))} required>
              <option value="">Chọn hình thức đăng bài</option>
              <option value="standard">Thông thường</option>
              <option value="proposal">Đề xuất</option>
              <option value="urgent">Khẩn cấp</option>
            </select>
            <select className="border p-3 rounded" name="category" value={form.category} onChange={e => setForm(f => ({ ...f, category: e.target.value }))} required>
              <option value="">Chọn ngành nghề</option>
              {categories.map(c => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
            <select className="border p-3 rounded" name="type" value={form.type} onChange={e => setForm(f => ({ ...f, type: e.target.value }))} required>
              <option value="">Chọn loại hình công việc</option>
              {types.map(t => (
                <option key={t.id} value={t.id}>{t.name}</option>
              ))}
            </select>
            <select className="border p-3 rounded" name="position" value={form.position} onChange={e => setForm(f => ({ ...f, position: e.target.value }))} required>
              <option value="">Chọn vị trí</option>
              {positions.map(p => (
                <option key={p.id} value={p.id}>{p.name}</option>
              ))}
            </select>
            <input className="border p-3 rounded" name="experience" placeholder="Kinh nghiệm (VD: 2 năm, Fresher, Senior...)" value={form.experience} onChange={e => setForm(f => ({ ...f, experience: e.target.value }))} required />
            <textarea className="border p-3 rounded col-span-2" name="description" placeholder="Mô tả công việc" value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} required />
          </div>
          <div className="mt-6 flex gap-3 justify-end">
            <button className="px-6 py-2 bg-primary text-white rounded-full font-semibold hover:bg-accent transition" type="submit">Lưu</button>
            <button className="px-6 py-2 bg-gray-300 rounded-full font-semibold" type="button" onClick={() => setShowForm(false)}>Hủy</button>
          </div>
        </form>
      )}
      <div className="bg-white rounded-2xl shadow-card p-8">
        <h3 className="text-xl font-bold mb-4 text-primary">Danh sách tin tuyển dụng của bạn</h3>
        {jobs.length === 0 ? (
          <div className="text-gray-500 text-center py-8">Bạn chưa có tin tuyển dụng nào.</div>
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
                {jobs.map(job => (
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
                        onClick={() => handleDeleteJob(job.id)}
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
  );
}
