import React, { useEffect, useState } from 'react';
import { getCompanies } from '../apiService';
import { Link } from 'react-router-dom';

export default function Companies() {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    getCompanies()
      .then(res => setCompanies(res.data))
      .catch(() => setCompanies([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 to-accent/10 py-12">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-extrabold text-primary mb-8">Danh sách công ty</h1>
        {loading ? (
          <div>Đang tải danh sách công ty...</div>
        ) : companies.length ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {companies.map(company => (
              <div key={company.id} className="bg-white rounded-xl shadow p-6 flex gap-4 items-center hover:shadow-lg transition">
                {company.logoUrl && (
                  <img src={company.logoUrl} alt="Logo" className="h-16 w-16 object-contain rounded-full border-2 border-primary" />
                )}
                <div className="flex-1">
                  <div className="text-xl font-bold text-blue-700 mb-1">{company.name}</div>
                  <div className="text-sm text-gray-600 mb-1">{company.industry}</div>
                  <div className="text-xs text-gray-500 mb-1">Địa chỉ: {company.location}</div>
                  {company.website && <a href={company.website} target="_blank" rel="noopener noreferrer" className="text-primary underline text-xs">{company.website}</a>}
                  {company.description && <div className="text-xs text-gray-500 mt-1 line-clamp-2">{company.description}</div>}
                </div>
                <Link to={`/company/${company.id}`} className="ml-auto px-4 py-2 rounded bg-primary text-white font-semibold hover:bg-accent transition text-sm">Xem chi tiết</Link>
              </div>
            ))}
          </div>
        ) : (
          <div>Không có công ty nào.</div>
        )}
      </div>
    </div>
  );
}
