import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { createInterviewSession, createInterviewSlot } from '../apiService';

const CreateSessionAndSlots = ({ jobId: propJobId, onSessionCreated }) => {
  const { jobId: paramJobId } = useParams();
  const jobId = propJobId || paramJobId;

  const [sessionTitle, setSessionTitle] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [slots, setSlots] = useState([{ startTime: '', endTime: '' }]);
  const [message, setMessage] = useState('');

  const handleSlotChange = (idx, field, value) => {
    setSlots(slots => slots.map((slot, i) => i === idx ? { ...slot, [field]: value } : slot));
  };

  const addSlot = () => setSlots([...slots, { startTime: '', endTime: '' }]);
  const removeSlot = idx => setSlots(slots => slots.filter((_, i) => i !== idx));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    try {
      const token = localStorage.getItem('token');
      // 1. Tạo session cho job
      const sessionRes = await createInterviewSession({
        title: sessionTitle,
        startTime,
        endTime,
        jobId: jobId,
      }, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const session = sessionRes.data;
      // 2. Tạo các slot cho session (không truyền candidateId nếu chưa có ứng viên)
      for (const slot of slots) {
        await createInterviewSlot(null, {
          params: {
            sessionId: session.id,
            jobId: jobId,
            startTime: slot.startTime,
            endTime: slot.endTime,
          },
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
      }
      setMessage('Tạo phiên phỏng vấn và các slot thành công!');
      if (onSessionCreated) onSessionCreated(session);
    } catch (err) {
      setMessage('Có lỗi khi tạo phiên phỏng vấn!');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded shadow p-6 max-w-xl mx-auto mt-8 space-y-4">
      <h2 className="text-xl font-bold mb-4">Tạo phiên phỏng vấn và slot cho Job #{jobId}</h2>
      <div>
        <label className="block mb-1">Tiêu đề phiên phỏng vấn</label>
        <input type="text" value={sessionTitle} onChange={e => setSessionTitle(e.target.value)} className="border rounded px-2 py-1 w-full" required />
      </div>
      <div>
        <label className="block mb-1">Thời gian bắt đầu phiên</label>
        <input type="datetime-local" value={startTime} onChange={e => setStartTime(e.target.value)} className="border rounded px-2 py-1 w-full" required />
      </div>
      <div>
        <label className="block mb-1">Thời gian kết thúc phiên</label>
        <input type="datetime-local" value={endTime} onChange={e => setEndTime(e.target.value)} className="border rounded px-2 py-1 w-full" required />
      </div>
      <div>
        <label className="block mb-1">Danh sách slot phỏng vấn</label>
        {slots.map((slot, idx) => (
          <div key={idx} className="flex gap-2 mb-2 items-center">
            <input type="datetime-local" value={slot.startTime} onChange={e => handleSlotChange(idx, 'startTime', e.target.value)} className="border rounded px-2 py-1" required />
            <input type="datetime-local" value={slot.endTime} onChange={e => handleSlotChange(idx, 'endTime', e.target.value)} className="border rounded px-2 py-1" required />
            {slots.length > 1 && <button type="button" className="text-red-500" onClick={() => removeSlot(idx)}>X</button>}
          </div>
        ))}
        <button type="button" className="bg-primary text-white px-3 py-1 rounded mt-2" onClick={addSlot}>+ Thêm slot</button>
      </div>
      <button type="submit" className="bg-primary text-white px-4 py-2 rounded">Tạo phiên & slot</button>
      {message && <div className="mt-4 text-center text-primary">{message}</div>}
    </form>
  );
};

export default CreateSessionAndSlots;
