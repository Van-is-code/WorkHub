import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getInterviewSessions, getInterviewSlotsScheduleCandidate, createInterviewSession } from '../apiService';
import { useState } from 'react';

const InterviewDashboard = () => {
  const [tab, setTab] = useState('sessions');
  const [showCreate, setShowCreate] = useState(false);
  const [newSession, setNewSession] = useState({ name: '' });
  const token = localStorage.getItem('token');
  const queryClient = useQueryClient();

  // Lấy tất cả phiên phỏng vấn (cho recruiter)
  const { data: sessions, isLoading: loadingSessions } = useQuery({
    queryKey: ['interviewSessions'],
    queryFn: async () => {
      const res = await getInterviewSessions({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return res.data;
    },
    enabled: !!token,
  });

  // Lấy lịch phỏng vấn của ứng viên
  const { data: candidateSchedules, isLoading: loadingSchedules } = useQuery({
    queryKey: ['candidateSchedules'],
    queryFn: async () => {
      const res = await getInterviewSlotsScheduleCandidate({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return res.data;
    },
    enabled: !!token,
  });

  const createSessionMutation = useMutation({
    mutationFn: async (data) => {
      const res = await createInterviewSession(data, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return res.data;
    },
    onSuccess: () => {
      setShowCreate(false);
      setNewSession({ name: '' });
      queryClient.invalidateQueries(['interviewSessions']);
    },
    onError: () => {
      alert('Tạo phiên phỏng vấn thất bại!');
    }
  });

  return (
    <div className="max-w-4xl mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">Dashboard Phỏng vấn</h1>
      <div className="flex space-x-4 mb-6">
        <button onClick={() => setTab('sessions')} className={`px-4 py-2 rounded ${tab === 'sessions' ? 'bg-primary text-white' : 'bg-gray-200'}`}>Phiên phỏng vấn</button>
        <button onClick={() => setTab('candidate')} className={`px-4 py-2 rounded ${tab === 'candidate' ? 'bg-primary text-white' : 'bg-gray-200'}`}>Lịch phỏng vấn của tôi</button>
      </div>
      {tab === 'sessions' && (
        <div>
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">Tất cả phiên phỏng vấn</h2>
            <button
              className="bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark"
              onClick={() => setShowCreate(true)}
            >
              + Thêm phiên phỏng vấn
            </button>
          </div>
          {showCreate && (
            <form
              className="mb-4 flex gap-2"
              onSubmit={e => {
                e.preventDefault();
                createSessionMutation.mutate(newSession);
              }}
            >
              <input
                type="text"
                placeholder="Tên phiên phỏng vấn"
                value={newSession.name}
                onChange={e => setNewSession({ ...newSession, name: e.target.value })}
                className="border rounded px-2 py-1"
                required
              />
              <button
                type="submit"
                className="bg-primary text-white px-4 py-1 rounded hover:bg-primary-dark"
                disabled={createSessionMutation.isLoading}
              >
                {createSessionMutation.isLoading ? 'Đang tạo...' : 'Tạo'}
              </button>
              <button
                type="button"
                className="ml-2 px-4 py-1 border rounded"
                onClick={() => setShowCreate(false)}
              >
                Hủy
              </button>
            </form>
          )}
          {loadingSessions ? (
            <div>Đang tải...</div>
          ) : sessions?.length > 0 ? (
            <table className="w-full border">
              <thead>
                <tr className="bg-gray-100">
                  <th className="p-2">ID</th>
                  <th className="p-2">Tên phiên</th>
                  <th className="p-2">Trạng thái</th>
                  <th className="p-2">Ngày tạo</th>
                </tr>
              </thead>
              <tbody>
                {sessions.map(session => (
                  <tr key={session.id} className="border-t">
                    <td className="p-2">{session.id}</td>
                    <td className="p-2">{session.name || 'Phiên phỏng vấn'}</td>
                    <td className="p-2">{session.status}</td>
                    <td className="p-2">{session.createdAt ? new Date(session.createdAt).toLocaleString('vi-VN') : ''}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div>Không có phiên phỏng vấn nào.</div>
          )}
        </div>
      )}
      {tab === 'candidate' && (
        <div>
          <h2 className="text-xl font-semibold mb-4">Lịch phỏng vấn của tôi</h2>
          {loadingSchedules ? (
            <div>Đang tải...</div>
          ) : candidateSchedules?.length > 0 ? (
            <table className="w-full border">
              <thead>
                <tr className="bg-gray-100">
                  <th className="p-2">Tên công việc</th>
                  <th className="p-2">Thời gian</th>
                  <th className="p-2">Trạng thái</th>
                  <th className="p-2">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {candidateSchedules.map(slot => (
                  <tr key={slot.id} className="border-t">
                    <td className="p-2">{slot.jobTitle}</td>
                    <td className="p-2">{slot.time ? new Date(slot.time).toLocaleString('vi-VN') : ''}</td>
                    <td className="p-2">{slot.status}</td>
                    <td className="p-2">
                      {slot.codeCandidate && (
                        <button
                          className="bg-green-500 text-white px-3 py-1 rounded"
                          onClick={() => window.open(`https://workhub.app.100ms.live/preview/${slot.codeCandidate}`, "_blank")}
                        >
                          Tham gia phỏng vấn
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div>Bạn chưa có lịch phỏng vấn nào.</div>
          )}
        </div>
      )}
    </div>
  );
};

export default InterviewDashboard;
