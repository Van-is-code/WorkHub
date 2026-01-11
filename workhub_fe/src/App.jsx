import { Routes, Route } from 'react-router-dom';
import Navbar from './components/layout/Navbar';
import Footer from './components/layout/Footer';
import Home from './pages/Home';
import Jobs from './pages/Jobs';
import JobDetails from './pages/JobDetails';
import Profile from './pages/Profile';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Register from './pages/Register';
import InterviewDashboard from './pages/InterviewDashboard';
import AddSlotPage from './pages/AddSlotPage';
import AdminDashboard from './pages/AdminDashboard';
import CreateSessionAndSlots from './pages/CreateSessionAndSlots';
import AdminUsers from './pages/admin/AdminUsers';
import AdminJobs from './pages/admin/AdminJobs';
import AdminStats from './pages/admin/AdminStats';
import AdminApplications from './pages/admin/AdminApplications';
import AdminCandidates from './pages/admin/AdminCandidates';
import AdminCompanyManager from './pages/admin/AdminCompanyManager';
import AdminJobTypes from './pages/admin/AdminJobTypes';
import AdminJobPositions from './pages/admin/AdminJobPositions';
import AdminJobCategories from './pages/admin/AdminJobCategories';
import AdminServicePackages from './pages/admin/AdminServicePackages';
import AdminSidebar from './components/admin/AdminSidebar';
import { Outlet } from 'react-router-dom';
import PaymentPage from './pages/PaymentPage';
import PaymentConfirm from './pages/PaymentConfirm';
import CreateCompany from './pages/CreateCompany';
import AdminRecruiterPackages from './pages/admin/AdminRecruiterPackages';
import RecruiterJobManager from './pages/recruiter/RecruiterJobManager';
import RecruiterCVList from './pages/recruiter/RecruiterCVList';
import RecruiterCreateJob from './pages/recruiter/RecruiterCreateJob';
import RecruiterJobList from './pages/recruiter/RecruiterJobList';
import AdminInterviewSessions from './pages/admin/AdminInterviewSessions';
import AdminInterviewSlots from './pages/admin/AdminInterviewSlots';
import RecruiterJobApplications from './pages/recruiter/RecruiterJobApplications';
import RecruiterJobDetail from './pages/recruiter/RecruiterJobDetail';
import Companies from './pages/Companies';
import CompanyDetail from './pages/CompanyDetail';
import Pricing from './pages/Pricing';

function RequireRecruiterOrAdmin({ children }) {
  const token = localStorage.getItem('token');
  let role = null;
  if (token) {
    try {
      role = JSON.parse(atob(token.split('.')[1])).role?.toLowerCase();
    } catch {}
  }
  if (role === 'recruiter' || role === 'admin') {
    return children;
  }
  return <div className="text-center text-red-500 py-10">Bạn không có quyền truy cập trang này.</div>;
}

function AdminLayout() {
  return (
    <div className="min-h-screen bg-gray-100 flex">
      <AdminSidebar />
      <div className="flex-1 flex flex-col">
        <Outlet />
      </div>
    </div>
  );
}

function App() {
  return (
    <div className="min-h-screen flex flex-col">
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route
          path="/admin/*"
          element={<AdminLayout />}
        >
          <Route index element={<div className="p-8">Chào mừng đến dashboard admin!</div>} />
          <Route path="stats" element={<AdminStats />} />
          <Route path="users" element={<AdminUsers />} />
          <Route path="jobs" element={<AdminJobs />} />
          <Route path="companies" element={<AdminCompanyManager />} />
          <Route path="applications" element={<AdminApplications />} />
          <Route path="candidates" element={<AdminCandidates />} />
          <Route path="company-manager" element={<AdminCompanyManager />} />
          <Route path="job-types" element={<AdminJobTypes />} />
          <Route path="job-positions" element={<AdminJobPositions />} />
          <Route path="job-categories" element={<AdminJobCategories />} />
          <Route path="service-packages" element={<AdminServicePackages />} />
          <Route path="recruiter-packages" element={<AdminRecruiterPackages />} />
          <Route path="interview-sessions" element={<AdminInterviewSessions />} />
          <Route path="interview-slots" element={<AdminInterviewSlots />} />
          {/* Thêm các route quản lý khác tại đây */}
        </Route>
        <Route
          path="/*"
          element={
            <>
              <Navbar />
              <main className="flex-grow">
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/jobs" element={<Jobs />} />
                  <Route path="/jobs/:id" element={<JobDetails />} />
                  <Route path="/profile/:id" element={<Profile />} />
                  <Route path="/profile" element={<Profile />} />
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/interview-dashboard" element={<InterviewDashboard />} />
                  <Route path="/add-slot" element={<AddSlotPage />} />
                  <Route path="/create-session-and-slots/:jobId" element={
                    <RequireRecruiterOrAdmin>
                      <CreateSessionAndSlots jobId={null} />
                    </RequireRecruiterOrAdmin>
                  } />
                  <Route path="/payment" element={<PaymentPage />} />
                  <Route path="/payment-confirm" element={<PaymentConfirm />} />
                  <Route path="/create-company" element={<CreateCompany />} />
                  <Route path="/recruiter/jobs" element={<RecruiterJobManager />} />
                  <Route path="/recruiter/cvs" element={<RecruiterCVList />} />
                  <Route path="/recruiter/cv-list" element={<RecruiterCVList />} />
                  <Route path="/recruiter/create-job" element={<RecruiterCreateJob />} />
                  <Route path="/recruiter/job-list" element={<RecruiterJobList />} />
                  <Route
                    path="/recruiter/jobs/:jobId/create-session"
                    element={
                      <RequireRecruiterOrAdmin>
                        <CreateSessionAndSlots />
                      </RequireRecruiterOrAdmin>
                    }
                  />
                  <Route path="/recruiter/jobs/:jobId/applications" element={<RequireRecruiterOrAdmin><RecruiterJobApplications /></RequireRecruiterOrAdmin>} />
                  <Route path="/recruiter/jobs/:jobId/detail" element={<RequireRecruiterOrAdmin><RecruiterJobDetail /></RequireRecruiterOrAdmin>} />
                  <Route path="/companies" element={<Companies />} />
                  <Route path="/company/:id" element={<CompanyDetail />} />
                  <Route path="/pricing" element={<Pricing />} />
                </Routes>
              </main>
              <Footer />
            </>
          }
        />
      </Routes>
    </div>
  );
}

export default App;
