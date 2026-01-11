import React, { useState, useEffect } from 'react';
import { createJob, getJobCategories, getJobTypes, getJobPositions, getSkills, getUserBenefits } from '../../apiService';
import { useNavigate } from 'react-router-dom';

function getUserIdFromToken() {
  // Ưu tiên lấy userId từ localStorage nếu có
  const userId = localStorage.getItem('userId');
  if (userId) return userId;
  // Nếu không có, giải mã từ JWT token
  const token = localStorage.getItem('token');
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.id || null;
  } catch {
    return null;
  }
}

export default function RecruiterCreateJob() {
  const [form, setForm] = useState({
    title: '',
    description: '',
    category: { id: '' },
    type: { id: '' },
    position: { id: '' },
    skills: [],
    salaryRange: '',
    experience: '',
    location: '',
    deadline: '',
    postAt: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [postAtLabel, setPostAtLabel] = useState('');
  const [hasBenefit, setHasBenefit] = useState(true);
  const [categories, setCategories] = useState([]);
  const [types, setTypes] = useState([]);
  const [positions, setPositions] = useState([]);
  const [skillsList, setSkillsList] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const userId = getUserIdFromToken();
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    console.log('userId:', userId, 'token:', token); // Log kiểm tra giá trị userId và token
    if (userId) {
      getUserBenefits(userId, config)
        .then(res => {
          console.log('UserBenefits:', res.data); // Log dữ liệu trả về từ API
          const options = Array.isArray(res.data) ? res.data.map(b => b.postAt) : [];
          if (options.length > 0) {
            setForm(f => ({ ...f, postAt: options[0] }));
            setPostAtLabel(options[0]);
            setHasBenefit(true);
          } else {
            setHasBenefit(false);
            setPostAtLabel('');
          }
        })
        .catch((err) => {
          console.error('UserBenefits API error:', err); // Log lỗi nếu có
          setHasBenefit(false);
          setPostAtLabel('');
        });
    } else {
      setHasBenefit(false);
      setPostAtLabel('');
    }
  }, [userId]);

  useEffect(() => {
    // Lấy danh mục từ DB
    getJobCategories(config).then(res => setCategories(res.data)).catch(() => {});
    getJobTypes(config).then(res => setTypes(res.data)).catch(() => {});
    getJobPositions(config).then(res => setPositions(res.data)).catch(() => {});
    getSkills(config).then(res => setSkillsList(res.data)).catch(() => {});
  }, [userId]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (!form.postAt) {
      setError('Bạn không có quyền lợi phù hợp để đăng tin.');
      return;
    }
    // Build object đúng cấu trúc backend yêu cầu
    const jobData = {
      title: form.title,
      description: form.description,
      category: { id: form.category.id },
      type: { id: form.type.id },
      position: { id: form.position.id },
      skills: form.skills.map(id => ({ id })),
      salaryRange: form.salaryRange,
      experience: form.experience,
      location: form.location,
      deadline: form.deadline || null,
      postAt: form.postAt
    };
    try {
      await createJob(jobData, config);
      setSuccess('Tạo tin thành công!');
      setTimeout(() => navigate('/recruiter/jobs'), 1200);
    } catch (err) {
      setError(err?.response?.data?.message || 'Tạo tin thất bại!');
    }
  };

  return (
    <div className="p-8 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold mb-6 text-blue-700">Tạo tin tuyển dụng mới</h2>
      <form className="bg-white rounded-xl shadow p-6 mb-6" onSubmit={handleSubmit}>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input className="border p-2 rounded" name="title" placeholder="Tiêu đề" value={form.title} onChange={e => setForm(f => ({ ...f, title: e.target.value }))} required disabled={!hasBenefit} />
          <select name="jobType" className="border p-2 rounded" value={form.type.id} onChange={e => setForm(f => ({ ...f, type: { id: e.target.value } }))} disabled={!hasBenefit}>
            <option value="">Chọn loại công việc</option>
            {types.map(type => (
              <option key={type.id} value={type.id}>{type.name}</option>
            ))}
          </select>
          <input className="border p-2 rounded" name="salary" placeholder="Mức lương" value={form.salaryRange} onChange={e => setForm(f => ({ ...f, salaryRange: e.target.value }))} required disabled={!hasBenefit} />
          <input className="border p-2 rounded" name="location" placeholder="Địa điểm" value={form.location} onChange={e => setForm(f => ({ ...f, location: e.target.value }))} required disabled={!hasBenefit} />
          <input className="border p-2 rounded" name="experience" placeholder="Kinh nghiệm" value={form.experience} onChange={e => setForm(f => ({ ...f, experience: e.target.value }))} required disabled={!hasBenefit} />
          <input className="border p-2 rounded" name="deadline" placeholder="Hạn nộp hồ sơ (YYYY-MM-DD)" value={form.deadline} onChange={e => setForm(f => ({ ...f, deadline: e.target.value }))} disabled={!hasBenefit} />
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
          <select className="border p-2 rounded" name="categoryId" value={form.category.id} onChange={e => setForm(f => ({ ...f, category: { id: e.target.value } }))} required disabled={!hasBenefit}>
            <option value="">Chọn ngành nghề</option>
            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <select className="border p-2 rounded" name="typeId" value={form.type.id} onChange={e => setForm(f => ({ ...f, type: { id: e.target.value } }))} required disabled={!hasBenefit}>
            <option value="">Chọn loại hình</option>
            {types.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
          </select>
          <select className="border p-2 rounded" name="positionId" value={form.position.id} onChange={e => setForm(f => ({ ...f, position: { id: e.target.value } }))} required disabled={!hasBenefit}>
            <option value="">Chọn vị trí</option>
            {positions.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>
          <select className="border p-2 rounded" name="skills" multiple value={form.skills} onChange={e => setForm(f => ({ ...f, skills: Array.from(e.target.selectedOptions).map(opt => opt.value) }))} required={!hasBenefit}>
            {skillsList.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </div>
        <textarea className="border p-2 rounded w-full mt-4" name="description" placeholder="Mô tả công việc" value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} required disabled={!hasBenefit} />
        {postAtLabel && (
          <div className="mt-4">
            <label className="block mb-1 font-semibold">Loại ưu tiên hiển thị (postAt):</label>
            <input className="border p-2 rounded bg-gray-100" name="postAt" value={postAtLabel} disabled readOnly />
          </div>
        )}
        {!hasBenefit && <div className="mt-4 text-red-600 font-bold">Bạn không có quyền lợi phù hợp để đăng tin.</div>}
        <div className="mt-4 flex gap-2">
          <button className="px-4 py-2 bg-blue-600 text-white rounded" type="submit" disabled={!hasBenefit}>Tạo tin</button>
          <button className="px-4 py-2 bg-gray-300 rounded" type="button" onClick={() => navigate('/recruiter/jobs')}>Hủy</button>
        </div>
        {error && <div className="mt-4 text-red-600 font-bold">{error}</div>}
        {success && <div className="mt-4 text-green-600 font-bold">{success}</div>}
      </form>
    </div>
  );
}
