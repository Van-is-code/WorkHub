import React, { useEffect, useState } from "react";
import { getInterviewSlotsByJob, getAdminJobs, deleteInterviewSlot } from "../../apiService";
import { TrashIcon, ChevronDownIcon, ChevronRightIcon } from "@heroicons/react/24/outline";

const AdminInterviewSlots = () => {
  const [jobs, setJobs] = useState([]);
  const [slotsByJob, setSlotsByJob] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [openJobIds, setOpenJobIds] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    setLoading(true);
    getAdminJobs()
      .then(async (res) => {
        setJobs(res.data);
        // Lấy slot cho từng job
        const slotsObj = {};
        await Promise.all(
          (res.data || []).map(async (job) => {
            try {
              const slotRes = await getInterviewSlotsByJob(job.id);
              slotsObj[job.id] = slotRes.data || [];
            } catch {
              slotsObj[job.id] = [];
            }
          })
        );
        setSlotsByJob(slotsObj);
        setLoading(false);
      })
      .catch(() => {
        setError("Lỗi khi tải dữ liệu công việc");
        setLoading(false);
      });
  }, []);

  const handleDeleteSlot = async (jobId, slotId) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa slot này?")) return;
    try {
      await deleteInterviewSlot(slotId);
      setSlotsByJob((prev) => ({
        ...prev,
        [jobId]: prev[jobId].filter((s) => s.id !== slotId),
      }));
    } catch (err) {
      alert("Xóa slot thất bại!");
    }
  };

  const toggleJob = (jobId) => {
    setOpenJobIds((prev) =>
      prev.includes(jobId) ? prev.filter((id) => id !== jobId) : [...prev, jobId]
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-purple-100 py-10 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-extrabold text-blue-700 mb-8">Quản lý Slot phỏng vấn</h1>
        {/* Search box */}
        <div className="mb-6 flex items-center gap-2">
          <input
            type="text"
            placeholder="Tìm kiếm công việc..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="border border-gray-300 rounded px-3 py-2 w-full max-w-md focus:outline-none focus:ring-2 focus:ring-blue-300"
          />
        </div>
        {loading ? (
          <div className="text-center text-blue-500 py-8 text-lg">Đang tải dữ liệu...</div>
        ) : error ? (
          <div className="text-center text-red-500 py-8 text-lg">{error}</div>
        ) : (
          <div className="bg-white rounded-2xl shadow-xl p-6">
            <h2 className="text-xl font-bold mb-4 text-primary">Danh sách công việc</h2>
            {jobs.length === 0 ? (
              <div className="text-center text-gray-400 py-4">Không có công việc nào</div>
            ) : (
              <div className="divide-y divide-gray-200">
                {jobs.filter(job => job.title.toLowerCase().includes(search.toLowerCase())).map((job) => (
                  <div key={job.id}>
                    <button
                      className="w-full flex items-center justify-between py-3 px-2 hover:bg-blue-50 transition font-semibold text-left"
                      onClick={() => toggleJob(job.id)}
                    >
                      <span>{job.title}</span>
                      {openJobIds.includes(job.id) ? (
                        <ChevronDownIcon className="w-5 h-5 text-blue-600" />
                      ) : (
                        <ChevronRightIcon className="w-5 h-5 text-blue-600" />
                      )}
                    </button>
                    {openJobIds.includes(job.id) && (
                      <div className="bg-blue-50 rounded-xl p-4 mb-4">
                        <h3 className="font-bold mb-2 text-blue-700">Slot phỏng vấn</h3>
                        <table className="min-w-full divide-y divide-gray-200">
                          <thead>
                            <tr className="bg-blue-100">
                              <th className="px-4 py-2 text-left font-bold text-gray-700">ID</th>
                              <th className="px-4 py-2 text-left font-bold text-gray-700">Bắt đầu</th>
                              <th className="px-4 py-2 text-left font-bold text-gray-700">Đã đặt?</th>
                              <th className="px-4 py-2 text-left font-bold text-gray-700">Ứng viên</th>
                              <th className="px-4 py-2 text-center font-bold text-gray-700">Hành động</th>
                            </tr>
                          </thead>
                          <tbody className="divide-y divide-gray-100">
                            {slotsByJob[job.id] && slotsByJob[job.id].length > 0 ? (
                              slotsByJob[job.id].map((slot) => (
                                <tr key={slot.id} className="hover:bg-blue-50 transition">
                                  <td className="px-4 py-2">{slot.id}</td>
                                  <td className="px-4 py-2">{slot.startTime ? new Date(slot.startTime).toLocaleString() : '-'}</td>
                                  <td className="px-4 py-2">{slot.booked ? 'Đã đặt' : 'Chưa đặt'}</td>
                                  <td className="px-4 py-2">{slot.candidateName || '-'}</td>
                                  <td className="px-4 py-2 flex gap-2 justify-center">
                                    <button className="p-2 rounded hover:bg-red-100" onClick={() => handleDeleteSlot(job.id, slot.id)} title="Xóa">
                                      <TrashIcon className="w-5 h-5 text-red-600" />
                                    </button>
                                  </td>
                                </tr>
                              ))
                            ) : (
                              <tr><td colSpan={5} className="text-center py-4 text-gray-400">Không có slot nào</td></tr>
                            )}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminInterviewSlots;
