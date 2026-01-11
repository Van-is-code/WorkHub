import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getJobById, getApplicationsByJob, closeJob, deleteJob, getInterviewSlotsByJob } from '../../apiService';

const RecruiterJobDetail = () => {
  const { jobId } = useParams();
  const navigate = useNavigate();

  // Lấy chi tiết job
  const { data: job, isLoading, isError } = useQuery({
    queryKey: ['job', jobId],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getJobById(jobId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!jobId,
  });

  // Lấy danh sách ứng viên đã apply vào job này
  const { data: applications, isLoading: isLoadingApps } = useQuery({
    queryKey: ['applications', jobId],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getApplicationsByJob(jobId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!jobId,
  });

  // Thêm state loading xóa job
  const [deleting, setDeleting] = useState(false);

  // State cho slot
  const [slots, setSlots] = useState([]);
  const [loadingSlots, setLoadingSlots] = useState(false);
  useEffect(() => {
    async function fetchSlots() {
      setLoadingSlots(true);
      try {
        const token = localStorage.getItem('token');
        const res = await getInterviewSlotsByJob(jobId, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
        setSlots(res.data);
      } catch (e) {
        setSlots([]);
      } finally {
        setLoadingSlots(false);
      }
    }
    if (jobId) fetchSlots();
  }, [jobId]);

  if (isLoading) return <div className="text-center py-8">Đang tải chi tiết job...</div>;
  if (isError || !job) return <div className="text-center text-red-500 py-8">Không thể tải chi tiết job.</div>;

  // Hàm xử lý xóa job
  async function handleDeleteJob(jobId) {
    if (window.confirm('Bạn có chắc chắn muốn xóa job này?')) {
      setDeleting(true);
      try {
        const token = localStorage.getItem('token');
        await deleteJob(jobId, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
        alert('Đã xóa job thành công!');
        navigate('/recruiter/jobs');
      } catch (err) {
        alert('Xóa job thất bại: ' + (err?.response?.data?.message || err.message));
      } finally {
        setDeleting(false);
      }
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/5 to-accent/10 py-10">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-2xl shadow-xl p-8 border border-primary/10 flex flex-col md:flex-row gap-8 mb-8">
          {/* Thông tin job bên trái */}
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-4 mb-4">
              <img
                src={job.recruiter?.company?.logoUrl || 'https://via.placeholder.com/80'}
                alt={job.recruiter?.companyName || 'Company Logo'}
                className="w-20 h-20 rounded-xl object-cover border border-primary/20 shadow"
              />
              <div>
                <h1 className="text-3xl font-extrabold text-primary mb-1">{job.title}</h1>
                <p className="text-lg text-dark font-semibold">{job.recruiter?.companyName}</p>
                <div className="flex flex-wrap gap-3 mt-2 text-gray-500 text-sm">
                  <span className="inline-flex items-center gap-1"><i className="fas fa-map-marker-alt"></i> {job.location}</span>
                  <span className="inline-flex items-center gap-1"><i className="fas fa-briefcase"></i> {job.type?.name}</span>
                  <span className="inline-flex items-center gap-1"><i className="fas fa-calendar"></i> Đăng: {formatDate(job.createdAt)}</span>
                  {job.deadline && <span className="inline-flex items-center gap-1"><i className="fas fa-hourglass-end"></i> Hạn nộp: {new Date(job.deadline).toLocaleDateString('vi-VN')}</span>}
                </div>
                {job.skills && job.skills.length > 0 && (
                  <div className="mt-2 flex flex-wrap gap-2">
                    <span className="font-semibold text-primary">Kỹ năng:</span>
                    {job.skills.map(skill => (
                      <span key={skill.id || skill} className="px-2 py-1 bg-primary/10 text-primary rounded-full text-xs font-medium">{skill.name || skill}</span>
                    ))}
                  </div>
                )}
              </div>
            </div>
            <div className="flex flex-wrap gap-4 text-base text-gray-700 mb-2">
              {job.category?.name && <div className="flex items-center gap-1"><span className="font-semibold">Ngành nghề:</span> {job.category.name}</div>}
              {job.position?.name && <div className="flex items-center gap-1"><span className="font-semibold">Vị trí:</span> {job.position.name}</div>}
            </div>
          </div>
          {/* Cột thao tác bên phải */}
          <div className="flex flex-col gap-4 justify-center min-w-[180px]">
            <button
              className="w-full bg-accent text-white px-4 py-2 rounded-lg font-bold hover:bg-primary transition shadow"
              onClick={() => navigate(`/recruiter/job/${jobId}/edit`)}
            >
              Sửa job
            </button>
            <button
              className="w-full bg-red-500 text-white px-4 py-2 rounded-lg font-bold hover:bg-red-600 transition shadow disabled:opacity-60"
              onClick={() => handleDeleteJob(jobId)}
              disabled={deleting}
            >
              {deleting ? 'Đang xóa...' : 'Xóa job'}
            </button>
          </div>
        </div>
        {/* Thông tin mô tả job và slot */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="space-y-8">
            <div className="bg-white rounded-2xl shadow p-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Yêu cầu công việc</h2>
              {job.experience && (
                <div className="mb-2 text-base text-gray-700">
                  <span className="font-semibold">Kinh nghiệm:</span> {job.experience}
                </div>
              )}
              <div className="prose max-w-none text-gray-800" dangerouslySetInnerHTML={{ __html: job.requirements }}></div>
            </div>
            <div className="bg-white rounded-2xl shadow p-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Quyền lợi</h2>
              {job.salaryRange && (
                <div className="mb-2 text-base text-gray-700">
                  <span className="font-semibold">Mức lương:</span> {job.salaryRange}
                </div>
              )}
              <div className="prose max-w-none text-gray-800" dangerouslySetInnerHTML={{ __html: job.benefits }}></div>
            </div>
            <div className="bg-white rounded-2xl shadow p-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Mô tả công việc</h2>
              <div className="prose max-w-none text-gray-800" dangerouslySetInnerHTML={{ __html: job.description }}></div>
            </div>
          </div>
          <div>
            <div className="bg-white rounded-2xl shadow p-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Danh sách slot phỏng vấn</h2>
              {loadingSlots ? (
                <div>Đang tải slot...</div>
              ) : slots.length > 0 ? (
                <ul className="divide-y divide-gray-200">
                  {slots.map(slot => (
                    <li key={slot.id} className="py-3 flex flex-col md:flex-row md:items-center md:justify-between gap-2">
                      <div>
                        <span className="font-semibold text-dark">{slot.title || 'Slot #' + slot.id}</span>
                        <span className="ml-2 text-gray-500 text-sm">{slot.startTime ? new Date(slot.startTime).toLocaleString('vi-VN') : ''}</span>
                      </div>
                      <div className="text-xs text-gray-500">{slot.booked ? 'Đã được đặt' : 'Còn trống'}</div>
                    </li>
                  ))}
                </ul>
              ) : (
                <div>Chưa có slot phỏng vấn nào.</div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

function formatDate(dateStr) {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return '-';
  return d.toLocaleDateString('vi-VN');
}

export default RecruiterJobDetail;
