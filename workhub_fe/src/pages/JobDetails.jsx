import { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getUserResumes,
  getInterviewSlotsByJob,
  applyJob,
  applyJobWithSlot,
  getJobById,
  getSavedJobs,
  saveJob,
  unsaveJob,
  getJobs
} from '../apiService';

const JobDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [selectedResumeId, setSelectedResumeId] = useState(null);
  const [selectedSlotId, setSelectedSlotId] = useState(null);

  const { data: user } = useQuery({
    queryKey: ['profile'],
    queryFn: async () => {
      // Lấy user từ token, không gọi API /me
      const token = localStorage.getItem('token');
      if (!token) return null;
      try {
        return JSON.parse(atob(token.split('.')[1]));
      } catch {
        return null;
      }
    },
    staleTime: Infinity,
  });

  // Query để lấy danh sách CV của user
  const { data: resumes, isLoading: isLoadingResumes } = useQuery({
    queryKey: ['resumes', user?.id],
    queryFn: async () => {
      if (!user?.id) return [];
      const token = localStorage.getItem('token');
      const response = await getUserResumes({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!user?.id,
  });

  // Lấy danh sách slot phỏng vấn còn trống theo job
  const { data: slots, isLoading: isLoadingSlots } = useQuery({
    queryKey: ['slots', id],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const res = await getInterviewSlotsByJob(id, {
        headers: token ? { Authorization: `Bearer ${token}` } : {}
      });
      return res.data.filter(slot => !slot.booked);
    },
    enabled: showApplyModal && !!id,
  });

  // Mutation để nộp CV
  const applyJobMutation = useMutation({
    mutationFn: async ({ jobId, resumeId }) => {
      const token = localStorage.getItem('token');
      const response = await applyJob(jobId, resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      setShowApplyModal(false);
      alert('Nộp CV thành công!');
    },
    onError: (error) => {
      console.error('Error applying job:', error);
      alert('Có lỗi xảy ra khi nộp CV. Vui lòng thử lại sau.');
    },
  });

  // Mutation để nộp CV và chọn slot
  const applyJobWithSlotMutation = useMutation({
    mutationFn: async ({ jobId, resumeId, slotId }) => {
      const token = localStorage.getItem('token');
      const response = await applyJobWithSlot(jobId, resumeId, slotId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      setShowApplyModal(false);
      alert('Nộp CV thành công!');
    },
    onError: (error) => {
      alert('Có lỗi xảy ra khi nộp CV hoặc chọn slot.');
    },
  });

  const { data: job, isLoading, isError } = useQuery({
    queryKey: ['job', id],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getJobById(id, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!id,
  });

  const { data: savedJobs, isLoading: isLoadingSaved, isError: isErrorSaved } = useQuery({
    queryKey: ['savedJobs'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getSavedJobs({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!user,
  });

  const saveJobMutation = useMutation({
    mutationFn: async (jobId) => {
      const token = localStorage.getItem('token');
      const response = await saveJob(jobId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['savedJobs']);
    },
    onError: (error) => {
      console.error('Error saving job:', error);
      alert('Có lỗi xảy ra khi lưu việc làm.');
    },
  });

  const unsaveJobMutation = useMutation({
    mutationFn: async (jobId) => {
      const token = localStorage.getItem('token');
      const response = await unsaveJob(jobId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['savedJobs']);
    },
    onError: (error) => {
      console.error('Error unsaving job:', error);
      alert('Có lỗi xảy ra khi bỏ lưu việc làm.');
    },
  });

  // Sửa lại hàm kiểm tra job đã lưu
  const isJobSaved = savedJobs?.some(savedJob => {
    // savedJob có thể là object có job hoặc jobId tuỳ backend
    if (savedJob.job) {
      return savedJob.job.id === parseInt(id);
    }
    return savedJob.jobId === parseInt(id);
  });

  const handleSaveUnsaveClick = () => {
    if (!user) {
      alert('Vui lòng đăng nhập để lưu việc làm.');
      navigate('/login');
      return;
    }

    if (isJobSaved) {
      unsaveJobMutation.mutate(parseInt(id));
    } else {
      saveJobMutation.mutate(parseInt(id));
    }
  };

  const handleApplyClick = () => {
    if (!user) {
      alert('Vui lòng đăng nhập để nộp CV.');
      navigate('/login');
      return;
    }

    if (!resumes || resumes.length === 0) {
      alert('Bạn chưa có CV nào. Vui lòng tạo CV trước khi nộp.');
      navigate(user?.id ? `/profile/${user.id}` : '/profile');
      return;
    }

    setShowApplyModal(true);
  };

  // Sửa lại handleSubmitApplication để gửi kèm slotId
  const handleSubmitApplication = () => {
    if (!selectedResumeId) {
      alert('Vui lòng chọn một CV để nộp.');
      return;
    }
    if (!selectedSlotId) {
      alert('Vui lòng chọn một khung giờ phỏng vấn.');
      return;
    }
    applyJobWithSlotMutation.mutate({
      jobId: parseInt(id),
      resumeId: selectedResumeId,
      slotId: selectedSlotId,
    });
  };

  const { data: similarJobs, isLoading: isLoadingSimilar, isError: isErrorSimilar } = useQuery({
    queryKey: ['similarJobs', job?.category?.id, job?.skills?.map(s => s.id)],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (job?.category?.id) {
        params.append('categoryId', job.category.id);
      }
      const token = localStorage.getItem('token');
      const response = await getJobs(params.toString(), {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data.filter(similarJob => similarJob.id !== job.id).slice(0, 5);
    },
    enabled: !!job,
  });

  if (isLoading) {
    return <div className="text-center py-8">Đang tải chi tiết việc làm...</div>;
  }

  if (isError) {
    return <div className="text-center text-red-500 py-8">Không thể tải chi tiết việc làm. Vui lòng thử lại sau.</div>;
  }

  if (!job) {
    return <div className="text-center py-8">Không tìm thấy thông tin việc làm.</div>;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/5 to-accent/10 py-10">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Content */}
          <div className="lg:col-span-2">
            {/* Job Header */}
            <div className="bg-white rounded-2xl shadow-xl p-8 mb-8 border border-primary/10">
              <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
                <div className="flex items-center gap-4">
                  <img
                    src={job.recruiter?.company?.logoUrl || "https://via.placeholder.com/80"}
                    alt={job.recruiter?.companyName || "Company Logo"}
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
                      {job.postAt && <span className="inline-flex items-center gap-1"><i className="fas fa-star"></i> Hình thức đăng: {job.postAt}</span>}
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
                <div className="flex flex-col gap-3 min-w-[160px]">
                  <button
                    onClick={handleSaveUnsaveClick}
                    className={`w-full px-4 py-2 rounded-lg font-semibold border transition shadow-sm ${isJobSaved ? 'bg-primary/10 text-primary border-primary' : 'bg-white text-dark border-gray-300 hover:bg-primary/5'}`}
                    aria-label={isJobSaved ? 'Bỏ lưu việc làm' : 'Lưu việc làm'}
                    disabled={saveJobMutation.isLoading || unsaveJobMutation.isLoading}
                  >
                    {saveJobMutation.isLoading || unsaveJobMutation.isLoading ? (
                      'Đang xử lý...'
                    ) : isJobSaved ? (
                      'Bỏ lưu việc làm'
                    ) : (
                      'Lưu việc làm'
                    )}
                  </button>
                 
                  <button
                    onClick={handleApplyClick}
                    className="w-full bg-primary text-white px-4 py-2 rounded-lg font-bold hover:bg-accent transition shadow"
                  >
                    Ứng tuyển ngay
                  </button>
                </div>
              </div>
              <div className="mt-6 flex flex-wrap gap-4 text-base text-gray-700">
    
                {job.category?.name && <div className="flex items-center gap-1"><span className="font-semibold">Ngành nghề:</span> {job.category.name}</div>}
                {job.position?.name && <div className="flex items-center gap-1"><span className="font-semibold">Vị trí:</span> {job.position.name}</div>}
              </div>
            </div>

            {/* Job Requirements */}
            <div className="bg-white rounded-2xl shadow p-8 mb-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Yêu cầu công việc</h2>
              {job.experience && (
                <div className="mb-2 text-base text-gray-700">
                  <span className="font-semibold">Kinh nghiệm:</span> {job.experience}
                </div>
              )}
              <div className="prose max-w-none text-gray-800" dangerouslySetInnerHTML={{ __html: job.requirements }}></div>
            </div>

            {/* Job Benefits */}
            <div className="bg-white rounded-2xl shadow p-8 mb-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Quyền lợi</h2>
              {job.salaryRange && (
                <div className="mb-2 text-base text-gray-700">
                  <span className="font-semibold">Mức lương:</span> {job.salaryRange}
                </div>
              )}
              <div className="prose max-w-none text-gray-800" dangerouslySetInnerHTML={{ __html: job.benefits }}></div>
            </div>

            {/* Job Description */}
            <div className="bg-white rounded-2xl shadow p-8 mb-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Mô tả công việc</h2>
              <div className="prose max-w-none text-gray-800" dangerouslySetInnerHTML={{ __html: job.description }}></div>
            </div>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-2xl shadow-xl p-8 mb-8 border border-primary/10">
              <h2 className="text-xl font-bold mb-4 text-primary">Thông tin công ty</h2>
              {job.recruiter?.companyProfile ? (
                <>
                  <div className="flex items-center mb-4">
                    <img
                      src={job.recruiter.companyProfile.logoUrl || "https://via.placeholder.com/50"}
                      alt={job.recruiter.companyProfile.name || "Company Logo"}
                      className="w-14 h-14 rounded-lg object-cover border border-primary/20 shadow"
                    />
                    <div className="ml-4">
                      <h3 className="font-semibold text-lg text-dark">{job.recruiter.companyProfile.name}</h3>
                      {job.recruiter.companyProfile.industry && <p className="text-gray-600 text-sm">Ngành: {job.recruiter.companyProfile.industry}</p>}
                    </div>
                  </div>
                  <div className="space-y-2 text-gray-600 text-sm">
                    {job.recruiter.companyProfile.location && (
                      <div><span className="font-semibold">Địa chỉ:</span> {job.recruiter.companyProfile.location}</div>
                    )}
                    {job.recruiter.companyProfile.website && (
                      <div><span className="font-semibold">Website:</span> <a href={job.recruiter.companyProfile.website} className="text-primary underline" target="_blank" rel="noopener noreferrer">{job.recruiter.companyProfile.website}</a></div>
                    )}
                    {job.recruiter.companyProfile.description && (
                      <div><span className="font-semibold">Mô tả:</span> {job.recruiter.companyProfile.description}</div>
                    )}
                  </div>
                </>
              ) : (
                <div className="space-y-2 text-gray-600 text-sm">
                  <div><span className="font-semibold">Nhà tuyển dụng:</span> {job.recruiter?.fullname}</div>
                  <div><span className="font-semibold">Email:</span> {job.recruiter?.email}</div>
                  {job.recruiter?.phone && <div><span className="font-semibold">SĐT:</span> {job.recruiter.phone}</div>}
                </div>
              )}
            </div>

            {/* Similar Jobs */}
            {similarJobs && similarJobs.length > 0 && (
              <div className="bg-white rounded-2xl shadow-xl p-8 border border-primary/10">
                <h2 className="text-xl font-bold mb-4 text-primary">Việc làm tương tự</h2>
                <ul className="space-y-3">
                  {similarJobs.map(similar => (
                    <li key={similar.id} className="border-b last:border-b-0 pb-2">
                      <Link to={`/jobs/${similar.id}`} className="font-semibold text-dark hover:text-primary transition">
                        {similar.title}
                      </Link>
                      <div className="text-xs text-gray-500">{similar.companyName || similar.recruiter?.companyName || '-'}</div>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modal nộp CV */}
      {showApplyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
            <h2 className="text-xl font-semibold mb-4">Chọn CV để nộp</h2>
            {isLoadingResumes ? (
              <div className="animate-pulse space-y-4">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="h-12 bg-gray-200 rounded"></div>
                ))}
              </div>
            ) : resumes?.length > 0 ? (
              <div className="space-y-4">
                {resumes.map((resume) => (
                  <div
                    key={resume.id}
                    className={`p-4 border rounded-lg cursor-pointer ${
                      selectedResumeId === resume.id ? 'border-primary bg-primary/5' : 'border-gray-200'
                    }`}
                    onClick={() => setSelectedResumeId(resume.id)}
                  >
                    <h3 className="font-medium">{resume.title}</h3>
                    <p className="text-sm text-gray-600 mt-1">{resume.content}</p>
                    <p className="text-xs text-gray-500 mt-2">
                      Tạo ngày: {new Date(resume.createdAt).toLocaleDateString('vi-VN')}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center text-gray-500 py-4">
                Bạn chưa có CV nào. Vui lòng tạo CV trước khi nộp.
              </div>
            )}
            <h3 className="mt-6 mb-2 font-medium">Chọn khung giờ phỏng vấn</h3>
            {isLoadingSlots ? (
              <div>Đang tải khung giờ...</div>
            ) : slots?.length > 0 ? (
              <div className="space-y-2">
                {slots.map(slot => (
                  <div
                    key={slot.id}
                    className={`p-2 border rounded cursor-pointer ${selectedSlotId === slot.id ? 'border-primary bg-primary/10' : 'border-gray-200'}`}
                    onClick={() => setSelectedSlotId(slot.id)}
                  >
                    {slot.startTime ? new Date(slot.startTime).toLocaleString('vi-VN') : 'Chưa xác định'}
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-gray-500">Không còn khung giờ trống.</div>
            )}
            <div className="mt-6 flex justify-end space-x-4">
              <button
                onClick={() => setShowApplyModal(false)}
                className="px-4 py-2 text-gray-600 hover:text-gray-800"
              >
                Hủy
              </button>
              <button
                onClick={handleSubmitApplication}
                disabled={!selectedResumeId || applyJobMutation.isLoading}
                className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {applyJobMutation.isLoading ? 'Đang xử lý...' : 'Nộp CV'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// Helper: parse ISO date string safely (support createdAt, createAt, postedAt)
function formatDate(dateStr) {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return '-';
  return d.toLocaleDateString('vi-VN');
}

export default JobDetails;