import React from 'react';

export default function ServiceFeatureInput({ features, setFeatures }) {
  const safeFeatures = Array.isArray(features) ? features : [];
  const handleChange = (idx, field, value) => {
    setFeatures(f => (Array.isArray(f) ? f.map((item, i) => i === idx ? { ...item, [field]: value } : item) : []));
  };
  const handleAdd = () => setFeatures(f => (Array.isArray(f) ? [...f, { featureName: '', description: '', postAt: 'standard', jobPostLimit: 5, cvLimit: 5 }] : [{ featureName: '', description: '', postAt: 'standard', jobPostLimit: 5, cvLimit: 5 }]));
  const handleRemove = idx => setFeatures(f => (Array.isArray(f) ? f.filter((_, i) => i !== idx) : []));

  React.useEffect(() => {
    if (!Array.isArray(features) || features.length === 0) {
      setFeatures([{ featureName: '', description: '', postAt: 'standard', jobPostLimit: 5, cvLimit: 5 }]);
    }
    // eslint-disable-next-line
  }, []);

  return (
    <div className="space-y-4">
      <div className="font-bold mb-2">Tính năng/giới hạn của gói</div>
      {safeFeatures.map((f, idx) => (
        <div key={idx} className="border rounded p-3 mb-2 flex flex-col gap-2 bg-gray-50">
          <input className="border p-2 rounded" placeholder="Tên tính năng" value={f.featureName} onChange={e => handleChange(idx, 'featureName', e.target.value)} required />
          <input className="border p-2 rounded" placeholder="Mô tả" value={f.description} onChange={e => handleChange(idx, 'description', e.target.value)} />
          <div className="flex gap-2">
            <select className="border p-2 rounded" value={f.postAt} onChange={e => handleChange(idx, 'postAt', e.target.value)}>
              <option value="standard">standard</option>
              <option value="urgent">urgent</option>
              <option value="proposal">proposal</option>
            </select>
            <input className="border p-2 rounded w-32" type="number" min="1" placeholder="Giới hạn job" value={f.jobPostLimit} onChange={e => handleChange(idx, 'jobPostLimit', e.target.value)} required />
            <input className="border p-2 rounded w-32" type="number" min="1" placeholder="Giới hạn CV" value={f.cvLimit} onChange={e => handleChange(idx, 'cvLimit', e.target.value)} required />
          </div>
          <button type="button" className="px-2 py-1 bg-red-500 text-white rounded w-fit" onClick={() => handleRemove(idx)}>Xóa</button>
        </div>
      ))}
      <button type="button" className="px-4 py-2 bg-blue-600 text-white rounded" onClick={handleAdd}>Thêm tính năng</button>
    </div>
  );
}
