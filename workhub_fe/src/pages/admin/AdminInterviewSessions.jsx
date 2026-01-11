import React, { useEffect, useState } from "react";
import { getInterviewSessions, deleteInterviewSession } from "../../apiService";
import { TrashIcon } from "@heroicons/react/24/outline";

const AdminInterviewSessions = () => {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    getInterviewSessions()
      .then((sessionsRes) => {
        setSessions(sessionsRes.data);
        setLoading(false);
      })
      .catch(() => {
        setError("Lỗi khi tải dữ liệu phiên phỏng vấn");
        setLoading(false);
      });
  }, []);

  const handleDeleteSession = async (sessionId) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa phiên phỏng vấn này?")) return;
    try {
      await deleteInterviewSession(sessionId);
      setSessions((prev) => prev.filter((s) => s.id !== sessionId));
    } catch (err) {
      alert("Xóa phiên phỏng vấn thất bại!");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-extrabold text-blue-700 mb-8">Quản lý Phiên phỏng vấn</h1>
        {loading ? (
          <div className="text-center text-blue-500 py-8 text-lg">Đang tải dữ liệu...</div>
        ) : error ? (
          <div className="text-center text-red-500 py-8 text-lg">{error}</div>
        ) : (
          <div className="bg-white rounded-2xl shadow-xl p-6 overflow-x-auto">
            <h2 className="text-xl font-bold mb-4 text-primary">Danh sách phiên phỏng vấn</h2>
            <table className="min-w-full divide-y divide-gray-200">
              <thead>
                <tr className="bg-blue-50">
                  <th className="px-4 py-3 text-left font-bold text-gray-700">ID</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Tiêu đề</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Nhà tuyển dụng</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Công việc</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Bắt đầu</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Kết thúc</th>
                  <th className="px-4 py-3 text-left font-bold text-gray-700">Trạng thái</th>
                  <th className="px-4 py-3 text-center font-bold text-gray-700">Hành động</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {sessions.length === 0 ? (
                  <tr><td colSpan={8} className="text-center py-4 text-gray-400">Không có dữ liệu</td></tr>
                ) : (
                  sessions.map((s) => (
                    <tr key={s.id} className="hover:bg-blue-50 transition">
                      <td className="px-4 py-2">{s.id}</td>
                      <td className="px-4 py-2">{s.title}</td>
                      <td className="px-4 py-2">{s.recruiter?.fullname || '-'}</td>
                      <td className="px-4 py-2">{s.job?.title || '-'}</td>
                      <td className="px-4 py-2">{s.startTime ? new Date(s.startTime).toLocaleString() : '-'}</td>
                      <td className="px-4 py-2">{s.endTime ? new Date(s.endTime).toLocaleString() : '-'}</td>
                      <td className="px-4 py-2">{s.status}</td>
                      <td className="px-4 py-2 flex gap-2 justify-center">
                        <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDeleteSession(s.id)} title="Xóa">
                          <TrashIcon className="w-5 h-5 text-red-600" />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminInterviewSessions;
