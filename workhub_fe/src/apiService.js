// src/apiService.js
import axios from 'axios';
import { Client } from '@stomp/stompjs';

const API_BASE_URL = 'http://localhost:8080/workhub/api/v1';

// Add a request interceptor to always attach Authorization header if token exists
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && !config.headers?.Authorization) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Example API functions
export const getInterviewSessions = (config) => axios.get(`${API_BASE_URL}/interview-sessions`, config);
export const getInterviewSlotsScheduleCandidate = (config) => axios.get(`${API_BASE_URL}/interview-slots/schedule/candidate`, config);
export const createInterviewSession = (data, config) => axios.post(`${API_BASE_URL}/interview-sessions`, data, config);
export const getSavedJobs = (config) => axios.get(`${API_BASE_URL}/saved-jobs`, config);
export const getAppliedJobs = (config) => axios.get(`${API_BASE_URL}/applications/appliedJobs`, config);
export const getUserResumes = (config) => axios.get(`${API_BASE_URL}/resumes/me`, config);
export const saveJob = (jobId, config) => axios.post(`${API_BASE_URL}/saved-jobs`, null, { ...config, params: { jobId } });
export const unsaveJob = (jobId, config) => axios.delete(`${API_BASE_URL}/saved-jobs`, { ...config, params: { jobId } });
export const createResume = (formData, config) => axios.post(`${API_BASE_URL}/resumes`, formData, config);
export const deleteResume = (resumeId, config) => axios.delete(`${API_BASE_URL}/resumes/${resumeId}`, config);
export const getResumeFileBase64 = (resumeId, config) => axios.get(`${API_BASE_URL}/resumes/${resumeId}/file-base64`, config);
export const registerCandidate = (data, config) => axios.post(`${API_BASE_URL}/candidate/register`, data, config);
export const login = (data) => axios.post(`${API_BASE_URL}/login`, null, {
  params: {
    email: data.email,
    password: data.password,
  },
  withCredentials: true,
});
export const getJobs = (params, config) => axios.get(`${API_BASE_URL}/jobs?${params}`, config);
export const getJobCategories = (config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.get(`${API_BASE_URL}/job-categories`, config);
};
export const getJobTypes = (config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.get(`${API_BASE_URL}/job-types`, config);
};
export const getJobPositions = (config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.get(`${API_BASE_URL}/job-positions`, config);
};
export const getSkills = (config) => axios.get(`${API_BASE_URL}/skill`, config);
export const getInterviewSlotsByJob = (jobId, config) => axios.get(`${API_BASE_URL}/interview-slots/by-job/${jobId}`, config);
export const applyJob = (jobId, resumeId, config) => axios.post(`${API_BASE_URL}/applications/${jobId}`, null, { ...config, params: { resumeId } });
export const applyJobWithSlot = (jobId, resumeId, slotId, config) => axios.post(`${API_BASE_URL}/applications/${jobId}/with-slot`, null, { ...config, params: { resumeId, slotId } });
export const getJobById = (jobId, config) => axios.get(`${API_BASE_URL}/jobs/${jobId}`, config);
export const createInterviewSlot = (data, config) => axios.post(`${API_BASE_URL}/interview-slots`, data, config);
export const getAdminCandidates = (config) => axios.get(`${API_BASE_URL}/admin/candidates`, config);
export const getResumesByUser = (userId, config) => axios.get(`${API_BASE_URL}/resumes/user/${userId}`, config);
export const getSavedJobsByUser = (userId, config) => axios.get(`${API_BASE_URL}/saved-jobs/user/${userId}`, config);
export const getApplicationsByUser = (userId, config) => axios.get(`${API_BASE_URL}/applications/user/${userId}`, config);
export const deleteUser = (userId, config) => axios.delete(`${API_BASE_URL}/user/${userId}`, config);
export const getUsers = (paramsOrConfig = {}) => {
  let config = paramsOrConfig;
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.get(`${API_BASE_URL}/users`, config);
};
export const getUserById = (userId, config) => axios.get(`${API_BASE_URL}/users/${userId}`, config);
export const createUser = (data, config) => axios.post(`${API_BASE_URL}/users`, data, config);
export const updateUser = (userId, data, config) => axios.put(`${API_BASE_URL}/users/${userId}`, data, config);
export const getRecruiters = (config = {}) =>
  axios.get(`${API_BASE_URL}/users`, {
    ...config,
    params: { ...(config?.params || {}), role: 'recruiter' }
  });
export const updateJob = (jobId, data, config) => axios.put(`${API_BASE_URL}/jobs/${jobId}`, data, config);
export const createJob = (data, config) => axios.post(`${API_BASE_URL}/jobs`, data, config);
export const getAdminJobs = (config) => axios.get(`${API_BASE_URL}/admin/jobs`, config);
export const getApplicationsByJobId = (jobId, config) => axios.get(`${API_BASE_URL}/applications/${jobId}/resumes`, config);
export const addUserToJob = (jobId, candidateId, resumeId, config) =>
  axios.post(`${API_BASE_URL}/admin/jobs/${jobId}/applications`, { candidateId, resumeId }, config);

export const deleteUserFromJob = (jobId, applicationId, config) =>
  axios.delete(`${API_BASE_URL}/admin/jobs/${jobId}/applications/${applicationId}`, config);
export const getCompanies = (config) => axios.get(`${API_BASE_URL}/companies`, config);
export const getCompanyById = (companyId, config) => axios.get(`${API_BASE_URL}/companies/${companyId}`, config);
export const createJobType = (data, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.post(`${API_BASE_URL}/job-types`, data, config);
};
export const updateJobType = (id, data, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.put(`${API_BASE_URL}/job-types/${id}`, data, config);
};
export const deleteJobType = (id, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.delete(`${API_BASE_URL}/job-types/${id}`, config);
};

export const createJobPosition = (data, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.post(`${API_BASE_URL}/job-positions`, data, config);
};
export const updateJobPosition = (id, data, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.put(`${API_BASE_URL}/job-positions/${id}`, data, config);
};
export const deleteJobPosition = (id, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.delete(`${API_BASE_URL}/job-positions/${id}`, config);
};

export const createJobCategory = (data, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.post(`${API_BASE_URL}/job-categories`, data, config);
};
export const updateJobCategory = (id, data, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.put(`${API_BASE_URL}/job-categories/${id}`, data, config);
};
export const deleteJobCategory = (id, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.delete(`${API_BASE_URL}/job-categories/${id}`, config);
};
export const deleteAdminJob = (jobId, config) => axios.delete(`${API_BASE_URL}/admin/jobs/${jobId}`, config);
export const createAdminJob = (data, config) => axios.post(`${API_BASE_URL}/admin/jobs`, data, config);
export const updateAdminJob = (jobId, data, config) => axios.put(`${API_BASE_URL}/admin/jobs/${jobId}`, data, config);
export const createCompanyByRecruiter = (userId, data, config) => axios.post(`${API_BASE_URL}/companies/user/${userId}`, data, config);
export const updateCompanyById = (companyId, data, config) => axios.put(`${API_BASE_URL}/companies/${companyId}`, data, config);
export const deleteCompanyById = (companyId, config) => axios.delete(`${API_BASE_URL}/companies/${companyId}`, config);
export const createResumeByAdmin = (userId, formData, config) => axios.post(`${API_BASE_URL}/resumes/admin/${userId}`, formData, config);
export const getServicePackages = (config) => axios.get(`${API_BASE_URL}/service-packages`, config);
export const createServicePackage = (data, config) => axios.post(`${API_BASE_URL}/service-packages`, data, config);
export const updateServicePackage = (id, data, config) => axios.put(`${API_BASE_URL}/service-packages/${id}`, data, config);
export const deleteServicePackage = (id, config) => axios.delete(`${API_BASE_URL}/service-packages/${id}`, config);
export const buyServicePackage = (userId, packageId, config) => axios.post(`${API_BASE_URL}/user-packages/buy`, null, { ...config, params: { userId, packageId } });
export const getRecruiterJobs = (config = {}) => axios.get(`${API_BASE_URL}/jobs/recruiter`, config);

// Notification APIs
export const getNotifications = (userId, config) => axios.get(`${API_BASE_URL}/notifications/${userId}`, config);

// User Benefits API
export const getUserBenefits = (userId, config) => axios.get(`${API_BASE_URL}/user-benefits/user/${userId}`, config);

// User Packages API
export const getUserPackages = (userId, config) => axios.get(`${API_BASE_URL}/user-packages/user/${userId}`, config);
export const updateUserPackage = (id, data, config) => axios.put(`${API_BASE_URL}/user-packages/${id}`, data, config);
export const deleteUserPackage = (id, config) => axios.delete(`${API_BASE_URL}/user-packages/${id}`, config);
export const createUserPackage = (data, config) => axios.post(`${API_BASE_URL}/user-packages`, data, config);
export const getAllUserPackages = (config) => axios.get(`${API_BASE_URL}/user-packages`, config);

// Notification WebSocket (STOMP) - native WebSocket only, không dùng SockJS để tránh lỗi global
export function createNotificationSocket(userId, onMessage) {
  const client = new Client({
    brokerURL: 'ws://localhost:8080/ws', // native WebSocket
    reconnectDelay: 5000,
    onConnect: () => {
      client.subscribe(`/topic/notifications/${userId}`, (msg) => {
        if (msg.body) {
          onMessage(JSON.parse(msg.body));
        }
      });
    },
    // Nếu cần auth, thêm header ở đây
    // connectHeaders: { Authorization: 'Bearer ...' }
  });
  client.activate();
  return client;
}

export const getCurrentUser = (config = {}) => axios.get(`${API_BASE_URL}/users/me`, config);
export const getCompanyByRecruiter = (userId, config) => axios.get(`${API_BASE_URL}/companies/by-recruiter/${userId}`, config);
export const deleteInterviewSlot = (slotId, config) => axios.delete(`${API_BASE_URL}/interview-slots/${slotId}`, config);
export const deleteInterviewSession = (sessionId, config) => axios.delete(`${API_BASE_URL}/interview-sessions/${sessionId}`, config);
export const createVnpayPaymentUrl = (packageId, price, orderInfo, renew = false) =>
  axios.post(`${API_BASE_URL}/payments/vnpay/create`, null, {
    params: { packageId, price, orderInfo, renew },
  }).then(res => res.data);
export const createPaypalOrder = (amount, currency, returnUrl, cancelUrl) =>
  axios.post(`${API_BASE_URL}/payments/paypal/create`, null, {
    params: { amount, currency, returnUrl, cancelUrl },
  }).then(res => res.data);

export const capturePaypalOrder = (orderId) =>
  axios.post(`${API_BASE_URL}/payments/paypal/capture`, null, {
    params: { orderId },
  }).then(res => res.data);
export const deleteJob = (jobId, config = {}) => {
  const token = localStorage.getItem('token');
  if (!config.headers) config.headers = {};
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return axios.delete(`${API_BASE_URL}/jobs/${jobId}`, config);
};
export const getViewedResumeIdsByRecruiter = (recruiterId, config) => axios.get(`${API_BASE_URL}/resume-views/by-recruiter/${recruiterId}`, config);
export const getApplicationsByJob = (jobId, config) => axios.get(`${API_BASE_URL}/applications/${jobId}/resumes`, config);
export const closeJob = (jobId, config) => axios.put(`${API_BASE_URL}/jobs/${jobId}/close`, null, config);
// Upload ảnh lên Imgur, trả về link ảnh
export const uploadImageToImgur = async (file) => {
  const clientId = 'YOUR_IMGUR_CLIENT_ID'; // Thay bằng client ID thực tế
  const formData = new FormData();
  formData.append('image', file);
  try {
    const res = await axios.post('https://api.imgur.com/3/image', formData, {
      headers: {
        Authorization: `Client-ID ${clientId}`,
      },
    });
    return res.data.data.link;
  } catch (err) {
    console.error('Lỗi upload ảnh lên Imgur:', err);
    throw err;
  }
};
