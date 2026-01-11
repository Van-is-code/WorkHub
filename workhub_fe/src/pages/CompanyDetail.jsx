import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getCompanyById, getJobs } from '../apiService';

export default function CompanyDetail() {
  const { id } = useParams();
  const [company, setCompany] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      getCompanyById(id),
      getJobs(`companyId=${id}`)
    ])
      .then(([companyRes, jobsRes]) => {
        setCompany(companyRes.data);
        setJobs(jobsRes.data || []);
      })
      .catch(() => {
        setCompany(null);
        setJobs([]);
      })
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="p-8">Đang tải thông tin công ty...</div>;
  if (!company) return <div className="p-8 text-red-500">Không tìm thấy công ty.</div>;

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 to-accent/10 py-12">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-2xl shadow-xl p-8 mb-8 flex flex-col md:flex-row gap-8 items-center">
          {company.logoUrl && <img src={company.logoUrl} alt="Logo" className="h-24 w-24 object-contain rounded-full border-4 border-primary" />}
          <div className="flex-1">
            <h1 className="text-3xl font-extrabold text-blue-700 mb-2">{company.name}</h1>
            <div className="text-sm text-gray-600 mb-1">{company.industry}</div>
            <div className="text-xs text-gray-500 mb-1">Địa chỉ: {company.location}</div>
            {company.website && <a href={company.website} target="_blank" rel="noopener noreferrer" className="text-primary underline text-xs">{company.website}</a>}
            {company.description && <div className="text-xs text-gray-500 mt-2 whitespace-pre-line">{company.description}</div>}
          </div>
        </div>
        <h2 className="text-xl font-bold text-primary mb-4">Danh sách việc làm của công ty</h2>
        {jobs.length ? (
          <div className="space-y-4">
            {jobs.map(job => (
              <Link to={`/jobs/${job.id}`} key={job.id} className="block bg-white rounded-xl shadow p-5 hover:shadow-lg transition border border-blue-100">
                <div className="text-lg font-bold text-blue-700 mb-1">{job.title}</div>
                <div className="text-sm text-gray-600 mb-1">{job.location}</div>
                <div className="text-xs text-gray-500 mb-1">{job.salaryRange && `Lương: ${job.salaryRange}`}</div>
                <div className="text-xs text-gray-500 mb-1">{job.category?.name && `Ngành: ${job.category.name}`}</div>
                <div className="text-xs text-gray-500 mb-1">{job.type?.name && `Loại hình: ${job.type.name}`}</div>
                <div className="text-xs text-gray-400">Hạn nộp: {job.deadline ? new Date(job.deadline).toLocaleDateString('vi-VN') : '-'}</div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-gray-500">Công ty chưa có việc làm nào.</div>
        )}
      </div>
    </div>
  );
}
