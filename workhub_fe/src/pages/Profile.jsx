import { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import {
  Edit,
  LocationOn,
  Work,
  School,
  Email,
  Phone,
  LinkedIn,
  GitHub,
  Bookmark,
  History,
  Settings,
} from '@mui/icons-material';
import PersonIcon from '@mui/icons-material/Person';
import ArticleIcon from '@mui/icons-material/Article';
import Dashboard from './Dashboard';
import { 
  getSavedJobs, getAppliedJobs, getUserResumes, saveJob, unsaveJob, createResume, deleteResume, getResumeFileBase64, getCurrentUser, getUserById, getCompanyById, getCompanyByRecruiter, updateCompanyById, uploadImageToImgur, getUserPackages
} from '../apiService';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/workhub/api/v1';

const Profile = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [activeTab, setActiveTab] = useState('profile');
  const [newResumeTitle, setNewResumeTitle] = useState('');
  const [newResumeContent, setNewResumeContent] = useState('');
  const [newResumeFile, setNewResumeFile] = useState(null);
  const [selectedSkillIds, setSelectedSkillIds] = useState([]);
  const [removingJobId, setRemovingJobId] = useState(null);
  const [localSavedJobs, setLocalSavedJobs] = useState([]);
  const [company, setCompany] = useState(null);
  const [userPackages, setUserPackages] = useState([]);
  const [isLoadingPackages, setIsLoadingPackages] = useState(false);

  // Lấy id user hiện tại từ token (nếu có)
  let currentUserId = null;
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      currentUserId = payload.id;
    } catch {}
  }

  // Nếu có id trên URL nhưng khác id user hiện tại (và không phải admin), điều hướng về đúng profile của mình
  useEffect(() => {
    if (id && id !== String(currentUserId)) {
      navigate(`/profile/${currentUserId}`);
    }
  }, [id, currentUserId, navigate]);

  // Lấy user info từ API /users/me (chỉ lấy của chính mình)
  const { data: user, isLoading: isLoadingUser, error: userError } = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getCurrentUser({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    staleTime: 5 * 60 * 1000,
  });

  // Query để lấy danh sách công việc đã lưu (API mới: /saved-jobs, không truyền userId, backend lấy từ JWT)
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

  // Query để lấy danh sách công việc đã ứng tuyển (API mới: /applications/appliedJobs, không truyền userId)
  const { data: appliedJobs, isLoading: isLoadingApplied } = useQuery({
    queryKey: ['appliedJobs'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getAppliedJobs({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!user,
  });

  // Query để lấy danh sách CV của người dùng (API mới: /resumes/me)
  const { data: resumes, isLoading: isLoadingResumes, refetch: refetchResumes } = useQuery({
    queryKey: ['userResumes'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getUserResumes({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: !!user,
  });

  // Mutation để lưu công việc
  const saveJobMutation = useMutation({
    mutationFn: async (jobId) => {
      const token = localStorage.getItem('token');
      const response = await saveJob(jobId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      refetchSavedJobs();
      alert('Đã lưu công việc thành công!');
    },
    onError: (error) => {
      alert('Lưu công việc thất bại!');
    },
  });

  // Mutation để bỏ lưu công việc
  const unsaveJobMutation = useMutation({
    mutationFn: async (jobId) => {
      const token = localStorage.getItem('token');
      const response = await unsaveJob(jobId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response; // Trả về toàn bộ response, không phải response.data
    },
    onSuccess: (response, jobId) => {
      setLocalSavedJobs(prev => prev.filter(saved => (saved.job?.id || saved.id) !== jobId));
      alert('Đã bỏ lưu công việc!');
    },
    onError: (error, jobId) => {
      const status = error?.response?.status;
      if (status === 204 || status === 200) {
        setLocalSavedJobs(prev => prev.filter(saved => (saved.job?.id || saved.id) !== jobId));
        alert('Đã bỏ lưu công việc!');
      } else {
        refetchSavedJobs();
        alert('Bỏ lưu công việc thất bại!');
      }
    },
  });

  // Mutation để tải lên CV mới
  const createResumeMutation = useMutation({
    mutationFn: async (formData) => {
      const token = localStorage.getItem('token');
      const response = await createResume(formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
      });
      return response.data;
    },
    onSuccess: () => {
      refetchResumes();
      setNewResumeTitle('');
      setNewResumeContent('');
      setNewResumeFile(null);
      setSelectedSkillIds([]);
      alert('CV đã được tải lên thành công!');
    },
    onError: (error) => {
      alert('Đã xảy ra lỗi khi tải lên CV.');
    },
  });

  // Mutation để xóa CV
  const deleteResumeMutation = useMutation({
    mutationFn: async (resumeId) => {
      const token = localStorage.getItem('token');
      const response = await deleteResume(resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    onSuccess: () => {
      refetchResumes();
      alert('CV đã được xóa thành công!');
    },
    onError: (error) => {
      alert('Đã xảy ra lỗi khi xóa CV.');
    },
  });

  useEffect(() => {
    if (userError && (userError.response?.status === 401 || userError.response?.status === 403)) {
      navigate('/login');
    }
  }, [userError, navigate]);

  useEffect(() => {
    if (savedJobs) setLocalSavedJobs(savedJobs);
  }, [savedJobs]);

  // Lấy thông tin công ty nếu là recruiter và chưa có companyProfile
  useEffect(() => {
    async function fetchCompany() {
      if (user && user.role === 'recruiter') {
        try {
          if (user.companyProfile) {
            setCompany(user.companyProfile);
            console.log('Company from user:', user.companyProfile);
          } else {
            const res = await getCompanyByRecruiter(user.id);
            setCompany(res.data);
            console.log('Company from API:', res.data);
          }
        } catch (e) {
          setCompany(null);
          console.log('Company fetch error:', e);
        }
      }
    }
    fetchCompany();
  }, [user]);

  useEffect(() => {
    if (user && user.id) {
      setIsLoadingPackages(true);
      getUserPackages(user.id, { headers: token ? { Authorization: `Bearer ${token}` } : {} })
        .then(res => setUserPackages(res.data))
        .catch(() => setUserPackages([]))
        .finally(() => setIsLoadingPackages(false));
    }
  }, [user, token]);

  if (isLoadingUser) return <div>Đang tải thông tin người dùng...</div>;
  if (!user || !user.id) {
    return (
      <div>
        Không tìm thấy thông tin người dùng hoặc user không hợp lệ.<br/>
        {userError && (
          <pre style={{color: 'red', marginTop: 8}}>
            {userError.message || JSON.stringify(userError)}
          </pre>
        )}
      </div>
    );
  }

  // Nếu là recruiter thì chỉ hiển thị 2 tab: Thông tin cá nhân và Thông tin công ty
  const isRecruiter = user.role === 'recruiter';

  // Hàm để hiển thị trạng thái ứng tuyển với màu sắc tương ứng
  const getStatusBadge = (status) => {
    const statusConfig = {
      pending: { color: 'bg-yellow-100 text-yellow-800', text: 'Đang chờ duyệt' },
      accepted: { color: 'bg-green-100 text-green-800', text: 'Đã chấp nhận' },
      rejected: { color: 'bg-red-100 text-red-800', text: 'Đã từ chối' },
    };

    const config = statusConfig[status.toLowerCase()] || statusConfig.pending;
    return (
      <span className={`px-2 py-1 rounded-full text-xs font-medium ${config.color}`}>
        {config.text}
      </span>
    );
  };

  // Hàm xử lý tải lên CV mới
  const handleResumeUpload = async (event) => {
    event.preventDefault();

    if (!user?.id) {
      alert('Vui lòng đăng nhập để tải lên CV.');
      return;
    }

    if (!newResumeFile) {
      alert('Vui lòng chọn file CV để tải lên.');
      return;
    }

    const formData = new FormData();
    formData.append('file', newResumeFile);
    formData.append('title', newResumeTitle || 'CV không tiêu đề'); // Use a default title if empty
    formData.append('content', newResumeContent || '');

    // Handle skillIds
    if (selectedSkillIds.length > 0) {
       selectedSkillIds.forEach(skillId => formData.append('skillIds', skillId.toString()));
    } else {
       // If no skills selected, explicitly send an empty parameter
       formData.append('skillIds', '');
    }

    // Log FormData contents (for debugging, not sent in production)
    // for (let pair of formData.entries()) {
    //     console.log(pair[0]+ ': ' + pair[1]);
    // }


    createResumeMutation.mutate(formData);
  };

  // Hàm xử lý xóa CV
  const handleDeleteResume = (resumeId) => {
    if (window.confirm('Bạn có chắc chắn muốn xóa CV này không?')) {
      deleteResumeMutation.mutate(resumeId);
    }
  };

  // Hàm xử lý tải về CV (sẽ implement sau)
  const handleDownloadResume = async (resumeId, resumeTitle) => {
    try {
      const token = localStorage.getItem('token');
      const response = await getResumeFileBase64(resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const base64 = response.data;
      const link = document.createElement('a');
      link.href = `data:application/pdf;base64,${base64}`;
      link.download = `${resumeTitle || 'CV'}.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error) {
      alert('Đã xảy ra lỗi khi tải về CV.');
    }
  };

  // Xem trực tiếp CV trên web (PDF viewer)
  const handleViewResume = async (resumeId, resumeTitle) => {
    try {
      const token = localStorage.getItem('token');
      const response = await getResumeFileBase64(resumeId, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      const base64 = response.data;
      const pdfWindow = window.open('');
      pdfWindow.document.write(
        `<title>${resumeTitle || 'CV'}</title><iframe width='100%' height='100%' src='data:application/pdf;base64,${base64}'></iframe>`
      );
    } catch (error) {
      alert('Đã xảy ra lỗi khi xem CV.');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 to-accent/10 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Profile Header */}
        <div className="bg-white rounded-2xl shadow-2xl p-8 mb-10 flex flex-col md:flex-row items-center gap-8">
          <div className="h-32 w-32 rounded-full bg-gray-200 flex items-center justify-center border-4 border-primary shadow-lg">
            <PersonIcon className="h-20 w-20 text-primary" />
          </div>
          <div className="flex-1">
            <h1 className="text-3xl font-extrabold text-primary font-heading mb-2">{(user.fullname && user.fullname !== user.email) ? user.fullname : (user.name && user.name !== user.email) ? user.name : (user.username && user.username !== user.email) ? user.username : ''}</h1>
            <div className="flex flex-wrap gap-4 items-center text-muted mb-2">
              <span className="flex items-center gap-1"><Email className="w-5 h-5" />{user.email || user.sub}</span>
              <span className="flex items-center gap-1"><Work className="w-5 h-5" />{user.role}</span>
            </div>
            {/* Thêm các trường khác nếu có */}
            {user.phone && <div className="flex items-center gap-1 text-muted"><Phone className="w-5 h-5" />{user.phone}</div>}
            {user.address && <div className="flex items-center gap-1 text-muted"><LocationOn className="w-5 h-5" />{user.address}</div>}
            {user.linkedin && <div className="flex items-center gap-1 text-muted"><LinkedIn className="w-5 h-5" />{user.linkedin}</div>}
            {user.github && <div className="flex items-center gap-1 text-muted"><GitHub className="w-5 h-5" />{user.github}</div>}
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Sidebar */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-2xl shadow p-4 sticky top-24">
              <nav className="flex flex-col gap-2">
                <button
                  onClick={() => setActiveTab('profile')}
                  className={`w-full flex items-center gap-2 px-4 py-3 rounded-lg font-semibold transition ${activeTab === 'profile' ? 'bg-primary text-white' : 'text-dark hover:bg-primary/10'}`}
                >
                  <PersonIcon /> Thông tin cá nhân
                </button>
                {isRecruiter && (
                  <>
                    <button
                      onClick={() => setActiveTab('company')}
                      className={`w-full flex items-center gap-2 px-4 py-3 rounded-lg font-semibold transition ${activeTab === 'company' ? 'bg-primary text-white' : 'text-dark hover:bg-primary/10'}`}
                    >
                      <Work /> Thông tin công ty
                    </button>
                    <button
                      onClick={() => setActiveTab('purchase-history')}
                      className={`w-full flex items-center gap-2 px-4 py-3 rounded-lg font-semibold transition ${activeTab === 'purchase-history' ? 'bg-primary text-white' : 'text-dark hover:bg-primary/10'}`}
                    >
                      <History /> Lịch sử mua gói
                    </button>
                  </>
                )}
              </nav>
            </div>
          </div>
          {/* Main Content */}
          <div className="md:col-span-3">
            {activeTab === 'profile' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Thông tin cá nhân</h2>
                <div className="space-y-2 text-lg">
                  <div><span className="font-semibold">Họ tên:</span> {(user.fullname && user.fullname !== user.email) ? user.fullname : (user.name && user.name !== user.email) ? user.name : (user.username && user.username !== user.email) ? user.username : ''}</div>
                  <div><span className="font-semibold">Email:</span> {user.email || user.username || user.sub || '-'}</div>
                  <div><span className="font-semibold">Vai trò:</span> {user.role}</div>
                  {user.phone && <div><span className="font-semibold">Số điện thoại:</span> {user.phone}</div>}
                  {user.address && <div><span className="font-semibold">Địa chỉ:</span> {user.address}</div>}
                  {user.linkedin && <div><span className="font-semibold">LinkedIn:</span> {user.linkedin}</div>}
                  {user.github && <div><span className="font-semibold">GitHub:</span> {user.github}</div>}
                </div>
              </div>
            )}
            {isRecruiter && activeTab === 'company' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Thông tin công ty</h2>
                {company && company.logoUrl && (
                  <div className="flex justify-center mb-6">
                    <img src={company.logoUrl} alt="Logo công ty" className="h-24 w-24 object-contain rounded-full border-4 border-primary shadow" />
                  </div>
                )}
                {company ? (
                  <CompanyInfoWithEdit company={company} setCompany={setCompany} />
                ) : (
                  <div className="text-gray-500">Chưa có thông tin công ty.</div>
                )}
              </div>
            )}
            {!isRecruiter && activeTab === 'resume' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Quản lý CV</h2>
                {isLoadingResumes ? (
                  <div>Đang tải CV...</div>
                ) : resumes && resumes.length ? (
                  <div className="space-y-4">
                    {resumes.map(resume => (
                      <div key={resume.id || resume._id || Math.random()} className="border rounded-lg p-4 flex flex-col md:flex-row md:items-center gap-4">
                        <div className="flex-1">
                          <div className="font-semibold text-lg text-dark">{resume.title}</div>
                          <div className="text-muted text-sm mb-2">{resume.content}</div>
                          <div className="text-xs text-gray-400">Tải lên: {resume.createdAt ? new Date(resume.createdAt).toLocaleString() : ''}</div>
                        </div>
                        <div className="flex gap-2">
                          <button className="px-4 py-2 rounded bg-primary text-white font-semibold hover:bg-accent transition" onClick={() => handleViewResume(resume.id, resume.title)}>Xem</button>
                          <button className="px-4 py-2 rounded bg-green-600 text-white font-semibold hover:bg-green-700 transition" onClick={() => handleDownloadResume(resume.id, resume.title)}>Tải về</button>
                          <button className="px-4 py-2 rounded bg-red-500 text-white font-semibold hover:bg-red-600 transition" onClick={() => handleDeleteResume(resume.id)}>Xóa</button>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div>Chưa có CV nào.</div>
                )}
                {/* Form upload CV */}
                <form className="mt-8 space-y-4" onSubmit={handleResumeUpload}>
                  <div>
                    <label className="block font-semibold mb-1">Tiêu đề CV</label>
                    <input type="text" className="w-full px-4 py-2 border rounded" value={newResumeTitle} onChange={e => setNewResumeTitle(e.target.value)} placeholder="Nhập tiêu đề CV..." />
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">Mô tả</label>
                    <textarea className="w-full px-4 py-2 border rounded" value={newResumeContent} onChange={e => setNewResumeContent(e.target.value)} placeholder="Mô tả ngắn về CV..."></textarea>
                  </div>
                  <div>
                    <label className="block font-semibold mb-1">File CV (PDF)</label>
                    <input type="file" accept="application/pdf" onChange={e => setNewResumeFile(e.target.files[0])} />
                  </div>
                  <button type="submit" className="px-6 py-2 rounded-full bg-primary text-white font-bold hover:bg-accent transition">Tải lên CV mới</button>
                </form>
              </div>
            )}
            {!isRecruiter && activeTab === 'saved' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Việc đã lưu</h2>
                {isLoadingSaved ? (
                  <div>Đang tải...</div>
                ) : localSavedJobs && localSavedJobs.length ? (
                  <div className="space-y-4">
                    {localSavedJobs.map(saved => (
                      <div key={saved.id || saved.job?.id || Math.random()} className="border rounded-lg p-4 flex flex-col md:flex-row md:items-center gap-4">
                        <div className="flex-1">
                          <Link to={saved.job?.id ? `/jobs/${saved.job.id}` : '#'} className="font-semibold text-lg text-dark hover:text-primary transition">
                            {saved.job?.title || '-'}
                          </Link>
                          <div className="text-muted text-sm mb-1">{saved.job?.companyName || saved.job?.recruiter?.fullname || '-'}</div>
                          <div className="text-xs text-gray-400 mb-1">Địa điểm: {saved.job?.location || '-'}</div>
                          {saved.job?.salaryRange && <div className="text-xs text-gray-400 mb-1">Mức lương: {saved.job.salaryRange}</div>}
                          {saved.job?.category?.name && <div className="text-xs text-gray-400 mb-1">Ngành nghề: {saved.job.category.name}</div>}
                          {saved.job?.type?.name && <div className="text-xs text-gray-400 mb-1">Loại hình: {saved.job.type.name}</div>}
                          {saved.job?.position?.name && <div className="text-xs text-gray-400 mb-1">Vị trí: {saved.job.position.name}</div>}
                          {saved.job?.deadline && <div className="text-xs text-gray-400 mb-1">Hạn nộp: {new Date(saved.job.deadline).toLocaleDateString('vi-VN')}</div>}
                          {saved.savedAt && <div className="text-xs text-gray-400 mb-1">Đã lưu: {new Date(saved.savedAt).toLocaleString()}</div>}
                        </div>
                        <button
                          className="px-4 py-2 rounded bg-red-500 text-white font-semibold hover:bg-red-600 transition"
                          onClick={() => {
                            if (window.confirm('Bạn có chắc chắn muốn bỏ lưu công việc này?')) {
                              setRemovingJobId(saved.job?.id || saved.id);
                              unsaveJobMutation.mutate(saved.job?.id || saved.id, {
                                onSettled: () => setRemovingJobId(null)
                              });
                            }
                          }}
                          disabled={removingJobId === (saved.job?.id || saved.id)}
                        >
                          {removingJobId === (saved.job?.id || saved.id) ? 'Đang bỏ lưu...' : 'Bỏ lưu'}
                        </button>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div>Chưa có việc làm nào được lưu.</div>
                )}
              </div>
            )}
            {!isRecruiter && activeTab === 'applied' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Việc đã ứng tuyển</h2>
                {isLoadingApplied ? (
                  <div>Đang tải...</div>
                ) : appliedJobs && appliedJobs.length ? (
                  <div className="space-y-4">
                    {appliedJobs.map(app => (
                      <div key={app.applicationId || app.id || Math.random()} className="border rounded-lg p-4 flex flex-col md:flex-row md:items-center gap-4">
                        <div className="flex-1">
                          <div className="font-semibold text-lg text-dark">{app.job?.title || '-'}</div>
                          <div className="text-muted text-sm mb-1">{app.job?.companyName || '-'}</div>
                          <div className="text-xs text-gray-400 mb-1">Địa điểm: {app.job?.location || '-'}</div>
                          {app.job?.salaryRange && <div className="text-xs text-gray-400 mb-1">Mức lương: {app.salaryRange}</div>}
                          {/* Trạng thái đơn ứng tuyển */}
                          <div className={`inline-block px-3 py-1 rounded-full text-xs font-semibold mb-1 ${
                            app.status === 'pending' ? 'bg-yellow-100 text-yellow-700' :
                            app.status === 'accepted' ? 'bg-green-100 text-green-700' :
                            app.status === 'rejected' ? 'bg-red-100 text-red-700' :
                            'bg-gray-100 text-gray-700'
                          }`}>
                            {app.status === 'pending' && 'Đang chờ duyệt'}
                            {app.status === 'accepted' && 'Đã được nhận'}
                            {app.status === 'rejected' && 'Đã bị từ chối'}
                            {!['pending','accepted','rejected'].includes(app.status) && app.status}
                          </div>
                          <div className="text-xs text-gray-400 mb-1">Nộp lúc: {app.appliedAt ? new Date(app.appliedAt).toLocaleString() : '-'}</div>
                          {app.resumeTitle && <div className="text-xs text-gray-400 mb-1">CV: {app.resumeTitle}</div>}
                          {app.interviewTime && <div className="text-xs text-gray-400 mb-1">Lịch PV: {new Date(app.interviewTime).toLocaleString()}</div>}
                        </div>
                        {/* Có thể thêm nút/tính năng khác ở đây */}
                      </div>
                    ))}
                  </div>
                ) : (
                  <div>Bạn chưa ứng tuyển công việc nào.</div>
                )}
              </div>
            )}
            {!isRecruiter && activeTab === 'notification' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Thông báo</h2>
                <NotificationList userId={user.id} />
              </div>
            )}
            {isRecruiter && activeTab === 'purchase-history' && (
              <div className="bg-white rounded-2xl shadow p-8 mb-8">
                <h2 className="text-2xl font-bold mb-4 text-primary">Lịch sử mua gói dịch vụ</h2>
                {isLoadingPackages ? (
                  <div>Đang tải...</div>
                ) : userPackages && userPackages.length ? (
                  <div className="overflow-x-auto">
                    <table className="min-w-full text-sm border rounded-xl overflow-hidden shadow">
                      <thead>
                        <tr className="bg-gradient-to-r from-blue-100 to-blue-50 text-blue-800">
                          <th className="px-4 py-3 font-bold text-left">Tên gói</th>
                          <th className="px-4 py-3 font-bold text-left">Giá</th>
                          <th className="px-4 py-3 font-bold text-left">Ngày mua</th>
                          <th className="px-4 py-3 font-bold text-left">Ngày hết hạn</th>
                          <th className="px-4 py-3 font-bold text-left">Trạng thái</th>
                          
                        </tr>
                      </thead>
                      <tbody>
                        {userPackages.map((pkg, idx) => (
                          <tr key={pkg.id || idx} className="border-b hover:bg-blue-50 transition">
                            <td className="px-4 py-2 font-semibold text-blue-700">{pkg.servicePackageName || pkg.servicePackage?.name || '-'}</td>
                            <td className="px-4 py-2 text-primary">{pkg.price ? pkg.price.toLocaleString('vi-VN') + ' VNĐ' : '-'}</td>
                            <td className="px-4 py-2">{pkg.purchaseDate ? new Date(pkg.purchaseDate).toLocaleString() : '-'}</td>
                            <td className="px-4 py-2">{pkg.expirationDate ? new Date(pkg.expirationDate).toLocaleString() : '-'}</td>
                            <td className="px-4 py-2">
                              <span className={`px-3 py-1 rounded-full text-xs font-semibold ${pkg.status === 'active' ? 'bg-green-100 text-green-700' : 'bg-gray-200 text-gray-600'}`}>{pkg.status === 'active' ? 'Đang sử dụng' : 'Đã hết hạn'}</span>
                            </td>
                            
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div>Bạn chưa từng mua gói dịch vụ nào.</div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Thay thế CompanyEditForm bằng CompanyInfoWithEdit
function CompanyInfoWithEdit({ company, setCompany }) {
  const [editing, setEditing] = useState(false);
  return editing ? (
    <CompanyEditForm company={company} setCompany={setCompany} onCancel={() => setEditing(false)} onSaved={() => setEditing(false)} />
  ) : (
    <div className="space-y-2 text-lg">
      <div><span className="font-semibold">Tên công ty:</span> {company.name}</div>
      <div><span className="font-semibold">Địa chỉ:</span> {company.location}</div>
      <div><span className="font-semibold">Ngành nghề:</span> {company.industry}</div>
      <div><span className="font-semibold">Website:</span> <a href={company.website} className="text-primary underline" target="_blank" rel="noopener noreferrer">{company.website}</a></div>
      <div><span className="font-semibold">Mô tả:</span> {company.description}</div>
      {company.logoUrl && <div><span className="font-semibold">Logo:</span><br/><img src={company.logoUrl} alt="Logo" className="h-16 mt-2" /></div>}
      <button className="mt-4 px-6 py-2 rounded-full bg-primary text-white font-bold hover:bg-accent transition" onClick={() => setEditing(true)}>
        Sửa thông tin
      </button>
    </div>
  );
}

// Sửa CompanyEditForm để nhận onCancel, onSaved
function CompanyEditForm({ company, setCompany, onCancel, onSaved }) {
  const [form, setForm] = useState({
    name: company.name || '',
    location: company.location || '',
    industry: company.industry || '',
    website: company.website || '',
    description: company.description || '',
    logoUrl: company.logoUrl || '',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleChange = e => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleLogoChange = async e => {
    const file = e.target.files[0];
    if (!file) return;
    setLoading(true);
    setMessage('Đang upload logo...');
    try {
      const link = await uploadImageToImgur(file);
      setForm(f => ({ ...f, logoUrl: link }));
      setMessage('Upload thành công!');
    } catch {
      setMessage('Upload thất bại!');
    }
    setLoading(false);
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true);
    setMessage('Đang cập nhật...');
    try {
      const token = localStorage.getItem('token');
      await updateCompanyById(company.id, form, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
      setCompany({ ...company, ...form });
      setMessage('Cập nhật thành công!');
      if (onSaved) onSaved();
    } catch (err) {
      setMessage('Cập nhật thất bại!');
    }
    setLoading(false);
  };

  return (
    <form className="space-y-4" onSubmit={handleSubmit}>
      <div>
        <label className="block font-semibold mb-1">Tên công ty</label>
        <input name="name" value={form.name} onChange={handleChange} className="w-full px-4 py-2 border rounded" />
      </div>
      <div>
        <label className="block font-semibold mb-1">Địa chỉ</label>
        <input name="location" value={form.location} onChange={handleChange} className="w-full px-4 py-2 border rounded" />
      </div>
      <div>
        <label className="block font-semibold mb-1">Ngành nghề</label>
        <input name="industry" value={form.industry} onChange={handleChange} className="w-full px-4 py-2 border rounded" />
      </div>
      <div>
        <label className="block font-semibold mb-1">Website</label>
        <input name="website" value={form.website} onChange={handleChange} className="w-full px-4 py-2 border rounded" />
      </div>
      <div>
        <label className="block font-semibold mb-1">Mô tả</label>
        <textarea name="description" value={form.description} onChange={handleChange} className="w-full px-4 py-2 border rounded" />
      </div>
      <div>
        <label className="block font-semibold mb-1">Logo công ty (link hoặc upload)</label>
        <input name="logoUrl" value={form.logoUrl} onChange={handleChange} className="w-full px-4 py-2 border rounded mb-2" placeholder="Dán link ảnh hoặc upload bên dưới" />
        <input type="file" accept="image/*" onChange={handleLogoChange} />
        {form.logoUrl && <img src={form.logoUrl} alt="Logo" className="h-16 mt-2" />}
      </div>
      <div className="flex gap-2">
        <button type="submit" className="px-6 py-2 rounded-full bg-primary text-white font-bold hover:bg-accent transition" disabled={loading}>
          {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
        </button>
        <button type="button" className="px-6 py-2 rounded-full bg-gray-300 text-dark font-bold hover:bg-gray-400 transition" onClick={onCancel} disabled={loading}>
          Hủy
        </button>
      </div>
      {message && <div className="mt-2 text-sm text-primary">{message}</div>}
    </form>
  );
}

export default Profile;