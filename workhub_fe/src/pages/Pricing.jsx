import React, { useEffect, useState } from 'react';
import { getServicePackages } from '../apiService';
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';

export default function Pricing() {
  const [packages, setPackages] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getServicePackages()
      .then(res => setPackages(res.data))
      .catch(() => setPackages([]))
      .finally(() => setLoading(false));
  }, []);

  // Xác định gói nổi bật (giả sử gói có giá cao nhất)
  const featuredId = packages.length ? packages.reduce((max, p) => p.price > max.price ? p : max, packages[0]).id : null;

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 to-accent/10 py-12">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-4xl font-extrabold text-primary mb-10 text-center tracking-tight drop-shadow">Bảng giá dịch vụ</h1>
        {loading ? (
          <div>Đang tải gói dịch vụ...</div>
        ) : packages.length ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-10">
            {packages.map(pkg => (
              <div
                key={pkg.id}
                className={`relative bg-white rounded-3xl shadow-2xl p-8 flex flex-col items-center border-2 ${pkg.id === featuredId ? 'border-yellow-400 scale-105 z-10' : 'border-blue-100'} hover:shadow-3xl transition-all duration-200`}
              >
                {pkg.id === featuredId && (
                  <span className="absolute -top-4 left-1/2 -translate-x-1/2 bg-yellow-400 text-white px-4 py-1 rounded-full text-xs font-bold shadow">Nổi bật</span>
                )}
                <MonetizationOnIcon className="text-primary mb-2" style={{ fontSize: 40 }} />
                <div className="text-xl font-bold text-blue-700 mb-2">{pkg.name}</div>
                <div className="text-3xl font-extrabold text-primary mb-4 flex items-end gap-1">
                  {pkg.price ? pkg.price.toLocaleString('vi-VN') : '-'} <span className="text-base font-normal mb-1">VNĐ</span>
                </div>
                <ul className="text-gray-700 text-sm mb-4 w-full space-y-2">
                  <li className="flex items-center gap-2"><CheckCircleIcon className="text-green-500" fontSize="small" /> Thời hạn: <span className="font-semibold ml-1">{pkg.duration} ngày</span></li>
                  <li className="flex items-center gap-2"><CheckCircleIcon className="text-green-500" fontSize="small" /> Đăng tối đa: <span className="font-semibold ml-1">{pkg.jobPostLimit} tin</span></li>
                  <li className="flex items-center gap-2"><CheckCircleIcon className="text-green-500" fontSize="small" /> Xem tối đa: <span className="font-semibold ml-1">{pkg.cvLimit} CV</span></li>
                </ul>
                <div className="text-gray-500 text-xs mb-6 text-center whitespace-pre-line min-h-[40px]">{pkg.description}</div>
                <button className={`mt-auto px-8 py-2 rounded-full font-bold transition text-white shadow-lg ${pkg.id === featuredId ? 'bg-yellow-400 hover:bg-yellow-500' : 'bg-primary hover:bg-accent'}`}>Chọn gói</button>
              </div>
            ))}
          </div>
        ) : (
          <div>Chưa có gói dịch vụ nào.</div>
        )}
      </div>
    </div>
  );
}
