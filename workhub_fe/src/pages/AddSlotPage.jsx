import { useState } from 'react';
import axios from 'axios';

const AddSlotPage = () => {
  const [jobId, setJobId] = useState('');
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    try {
      const token = localStorage.getItem('token');
      await axios.post(`http://localhost:8080/workhub/api/v1/interview-slots/by-job`, null, {
        params: {
          jobId,
          startTime,
          endTime,
        },
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      setMessage('Tạo slot thành công!');
    } catch (err) {
      setMessage('Tạo slot thất bại!');
    }
  };

  return (
    <div className="max-w-md mx-auto py-8">
      <h1 className="text-2xl font-bold mb-6">Tạo khung giờ phỏng vấn</h1>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block mb-1">Job ID</label>
          <input type="text" value={jobId} onChange={e => setJobId(e.target.value)} className="border rounded px-2 py-1 w-full" required />
        </div>
        <div>
          <label className="block mb-1">Thời gian bắt đầu</label>
          <input type="datetime-local" value={startTime} onChange={e => setStartTime(e.target.value)} className="border rounded px-2 py-1 w-full" required />
        </div>
        <div>
          <label className="block mb-1">Thời gian kết thúc (tùy chọn)</label>
          <input type="datetime-local" value={endTime} onChange={e => setEndTime(e.target.value)} className="border rounded px-2 py-1 w-full" />
        </div>
        <button type="submit" className="bg-primary text-white px-4 py-2 rounded">Tạo slot</button>
      </form>
      {message && <div className="mt-4 text-center text-primary">{message}</div>}
    </div>
  );
};

export default AddSlotPage;
