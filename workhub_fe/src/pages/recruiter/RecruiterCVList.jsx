import React, { useEffect, useState } from 'react';
import { getAppliedJobs } from '../../apiService';

export default function RecruiterCVList() {
  const [cvs, setCVs] = useState([]);
  const [selectedCVs, setSelectedCVs] = useState([]);
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    fetchCVs();
    // eslint-disable-next-line
  }, []);

  const fetchCVs = async () => {
    const res = await getAppliedJobs(config);
    setCVs(res.data);
  };

  const handleSelectCV = (cvId) => {
    setSelectedCVs((prev) =>
      prev.includes(cvId) ? prev.filter((id) => id !== cvId) : [...prev, cvId]
    );
  };

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      setSelectedCVs(cvs.map((cv) => cv.applicationId));
    } else {
      setSelectedCVs([]);
    }
  };

  return (
    <div className="p-8">
      <h2 className="text-2xl font-bold mb-6 text-blue-700">Danh sách CV ứng viên đã ứng tuyển</h2>
      <div className="mb-4">
        <label className="font-medium">
          <input
            type="checkbox"
            checked={selectedCVs.length === cvs.length && cvs.length > 0}
            onChange={handleSelectAll}
            className="mr-2"
          />
          Chọn tất cả
        </label>
      </div>
      <ul>
        {cvs.map((cv) => (
          <li key={cv.applicationId} className="border-b py-2 flex flex-col md:flex-row md:items-center md:gap-4">
            <div className="flex items-center mb-2 md:mb-0">
              <input
                type="checkbox"
                checked={selectedCVs.includes(cv.applicationId)}
                onChange={() => handleSelectCV(cv.applicationId)}
                className="mr-3"
              />
              <span className="font-bold mr-2">{cv.resumeTitle}</span>
            </div>
            <div className="flex-1 flex flex-wrap gap-4 text-sm">
              <span><b>Job:</b> {cv.job?.title}</span>
              <span><b>Công ty:</b> {cv.job?.companyName}</span>
              <span><b>Trạng thái:</b> {cv.status}</span>
              <span><b>Ngày nộp:</b> {cv.appliedAt ? new Date(cv.appliedAt).toLocaleString() : '-'}</span>
              {cv.interviewTime && <span><b>Phỏng vấn:</b> {new Date(cv.interviewTime).toLocaleString()}</span>}
            </div>
          </li>
        ))}
      </ul>
      {selectedCVs.length > 0 && (
        <div className="mt-6">
          <span className="font-medium">Đã chọn {selectedCVs.length} CV</span>
          {/* Thêm nút hoặc hành động xử lý các CV đã chọn tại đây */}
        </div>
      )}
    </div>
  );
}
