import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getApplicationsByJobId, getResumeFileBase64, getViewedResumeIdsByRecruiter } from '../../apiService';

export default function RecruiterJobApplications() {
  const { jobId } = useParams();
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [viewedResumes, setViewedResumes] = useState([]);
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    fetchApplications();
    fetchViewedResumes();
    // eslint-disable-next-line
  }, [jobId]);

  const fetchApplications = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getApplicationsByJobId(jobId, config);
      setApplications(res.data);
    } catch (err) {
      setError('Lỗi khi tải danh sách ứng viên: ' + (err?.response?.data?.message || err.message));
      setApplications([]);
    }
    setLoading(false);
  };

  // Lấy recruiterId từ token (giả sử payload có id)
  const getRecruiterId = () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return null;
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.id;
    } catch {
      return null;
    }
  };

  const fetchViewedResumes = async () => {
    const recruiterId = getRecruiterId();
    if (!recruiterId) return;
    try {
      const res = await getViewedResumeIdsByRecruiter(recruiterId, config);
      setViewedResumes(res.data || []);
    } catch {
      setViewedResumes([]);
    }
  };

  // Hàm xử lý khi xem CV
  const handleViewCV = async (resumeId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await getResumeFileBase64(resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const base64 = response.data;
      const pdfWindow = window.open('');
      pdfWindow.document.write(
        `<title>CV Ứng viên</title><iframe width='100%' height='100%' src='data:application/pdf;base64,${base64}'></iframe>`
      );
      // Sau khi xem, refetch lại danh sách CV đã xem để đồng bộ highlight
      fetchViewedResumes();
    } catch (error) {
      let msg = 'Đã xảy ra lỗi khi xem CV.';
      if (error?.response?.data?.message) msg = error.response.data.message;
      else if (error?.response?.status === 403) msg = 'Bạn không đủ quyền hoặc đã hết quota xem CV.';
      alert(msg);
    }
  };

  return (
    <div className="p-8 max-w-6xl mx-auto">
      <h2 className="text-3xl font-bold mb-8 text-primary text-center">Danh sách ứng viên ứng tuyển</h2>
      {error && <div className="text-red-600 font-semibold mb-4 text-center">{error}</div>}
      <div className="bg-white rounded-2xl shadow-card p-8">
        {loading ? (
          <div className="text-center text-lg">Đang tải...</div>
        ) : applications.length === 0 ? (
          <div className="text-gray-500 text-center py-8">Chưa có ứng viên nào ứng tuyển.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-100">
                  <th className="py-2 px-4">Tên ứng viên</th>
                  <th className="py-2 px-4">Email</th>
                  <th className="py-2 px-4">Số điện thoại</th>
                  <th className="py-2 px-4">Trạng thái</th>
                  <th className="py-2 px-4">Ngày nộp</th>
                  <th className="py-2 px-4">Mô tả CV</th>
                  <th className="py-2 px-4">CV</th>
                </tr>
              </thead>
              <tbody>
                {applications.map(app => (
                  <tr
                    key={app.id}
                    className={`border-b transition ${viewedResumes.includes(app.resumeId) ? 'bg-yellow-50' : 'hover:bg-gray-50'}`}
                  >
                    <td className="py-2 px-4 font-semibold text-dark">{app.userFullname}</td>
                    <td className="py-2 px-4">{app.userEmail}</td>
                    <td className="py-2 px-4">{app.userPhone}</td>
                    <td className="py-2 px-4">
                      <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${app.status === 'accepted' ? 'bg-green-100 text-green-700' : app.status === 'rejected' ? 'bg-red-100 text-red-700' : 'bg-gray-200 text-gray-700'}`}>{app.status}</span>
                    </td>
                    <td className="py-2 px-4">{app.appliedAt ? new Date(app.appliedAt).toLocaleString() : '-'}</td>
                    <td className="py-2 px-4 max-w-xs truncate" title={app.resumeDescription || app.resumeContent || app.content || ''}>
                      {app.resumeDescription || app.resumeContent || app.content || <span className="text-gray-400">-</span>}
                    </td>
                    <td className="py-2 px-4">
                      {app.resumeId && (
                        <button
                          className={`px-4 py-1 rounded-full font-semibold shadow text-white transition ${viewedResumes.includes(app.resumeId) ? 'bg-yellow-500 hover:bg-yellow-600' : 'bg-blue-600 hover:bg-blue-700'}`}
                          onClick={e => {
                            e.preventDefault();
                            handleViewCV(app.resumeId);
                          }}
                        >
                          Xem CV
                        </button>
                      )}
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
