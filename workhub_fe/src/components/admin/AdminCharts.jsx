import React, { useEffect, useState } from 'react';
import {
  Bar,
  Pie,
  Line,
} from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement,
} from 'chart.js';
import { getUsers, getAllUserPackages, getJobCategories, getAdminJobs, getCompanies, getRecruiters, getAdminCandidates } from '../../apiService';
import { UserGroupIcon, ClockIcon } from '@heroicons/react/24/outline';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement
);

export default function AdminCharts() {
  const [userCount, setUserCount] = useState(0);
  const [expiringRecruiters, setExpiringRecruiters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [userStats, setUserStats] = useState([]); // [{month: '2025-01', count: 10}, ...]
  const [jobStats, setJobStats] = useState([]); // [{category: 'IT', count: 10}, ...]
  const [revenueStats, setRevenueStats] = useState([]); // [{month: '2025-01', revenue: 100}, ...]
  const [packageStats, setPackageStats] = useState([]); // [{name, count}]
  const [jobCount, setJobCount] = useState(0);
  const [recruiterCount, setRecruiterCount] = useState(0);
  const [candidateCount, setCandidateCount] = useState(0);
  const [cvCount, setCvCount] = useState(0);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      getUsers(),
      getAllUserPackages(),
      getJobCategories(),
      getAdminJobs(),
      getCompanies(),
      getRecruiters(),
      getAdminCandidates(),
      // Lấy tất cả job để đếm tổng số application
    ]).then(async ([userRes, packageRes, catRes, jobsRes, companiesRes, recruitersRes, candidatesRes]) => {
      setUserCount(userRes.data.length);
      setJobCount(jobsRes.data.length);
      setRecruiterCount(recruitersRes.data.length);
      setCandidateCount(candidatesRes.data.length);
      // Đếm tổng số CV đã ứng tuyển
      let totalApplications = 0;
      const token = localStorage.getItem('token');
      const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};
      const jobs = jobsRes.data || [];
      // Lấy tất cả application của từng job (song song)
      if (jobs.length > 0) {
        const appResults = await Promise.all(
          jobs.map(j =>
            import('../../apiService').then(api =>
              api.getApplicationsByJobId(j.id, config).then(res => res.data.length).catch(() => 0)
            )
          )
        );
        totalApplications = appResults.reduce((sum, n) => sum + n, 0);
      }
      setCvCount(totalApplications);
      // Lọc recruiter sắp hết hạn gói (trong 7 ngày, status ACTIVE)
      const now = new Date();
      const soon = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
      const expiring = (packageRes.data || []).filter(pkg =>
        pkg.role === 'RECRUITER' &&
        pkg.status === 'ACTIVE' &&
        pkg.expirationDate &&
        new Date(pkg.expirationDate) <= soon &&
        new Date(pkg.expirationDate) >= now
      ).sort((a, b) => new Date(a.expirationDate) - new Date(b.expirationDate));
      setExpiringRecruiters(expiring);
      // Thống kê user mới theo tháng (demo: random, thực tế cần API backend)
      const userStatsMap = {};
      (userRes.data || []).forEach(u => {
        if (u.createdAt) {
          const m = u.createdAt.slice(0, 7); // yyyy-MM
          userStatsMap[m] = (userStatsMap[m] || 0) + 1;
        }
      });
      setUserStats(Object.entries(userStatsMap).map(([month, count]) => ({ month, count }))); // [{month, count}]
      // Thống kê job theo ngành (ưu tiên lấy job.category.name, nếu không có thì map từ job.categoryId sang catRes, nếu vẫn không có thì là 'Khác')
      const categoryMap = {};
      (catRes.data || []).forEach(cat => {
        categoryMap[cat.id] = cat.name;
      });
      const catCountMap = {};
      (jobsRes.data || []).forEach(j => {
        let catName = 'Khác';
        if (j.category && j.category.name) {
          catName = j.category.name;
        } else if (j.categoryId && categoryMap[j.categoryId]) {
          catName = categoryMap[j.categoryId];
        }
        catCountMap[catName] = (catCountMap[catName] || 0) + 1;
      });
      setJobStats(Object.entries(catCountMap).map(([category, count]) => ({ category, count })));
      // Doanh thu theo tháng (lấy đúng status active)
      const revMap = {};
      (packageRes.data || []).forEach(pkg => {
        if (pkg.status && pkg.status.toLowerCase() === 'active' && pkg.purchaseDate && pkg.price) {
          const m = (typeof pkg.purchaseDate === 'string' ? pkg.purchaseDate : (pkg.purchaseDate?.toISOString?.() || '')).slice(0, 7);
          revMap[m] = (revMap[m] || 0) + pkg.price;
        }
      });
      setRevenueStats(Object.entries(revMap).map(([month, revenue]) => ({ month, revenue })));
      // Thống kê số lượng từng loại gói mà recruiter đã mua (tính cả active và expired, không phân biệt hoa thường)
      const pkgMap = {};
      (packageRes.data || []).forEach(pkg => {
        if (pkg.role && pkg.role.toLowerCase() === 'recruiter' && pkg.servicePackageName && pkg.status && ['active','expired'].includes(pkg.status.toLowerCase())) {
          pkgMap[pkg.servicePackageName] = (pkgMap[pkg.servicePackageName] || 0) + 1;
        }
      });
      setPackageStats(Object.entries(pkgMap).map(([name, count]) => ({ name, count })));
      setLoading(false);
    });
  }, []);

  // Chart data động
  const userData = {
    labels: userStats.length > 0 ? userStats.map(u => u.month) : ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Người dùng mới',
        data: userStats.length > 0 ? userStats.map(u => u.count) : [120, 190, 300, 250, 220, 310],
        backgroundColor: 'rgba(59,130,246,0.7)',
      },
    ],
  };
  const jobData = {
    labels: jobStats.length > 0 ? jobStats.map(j => j.category) : ['IT', 'Kế toán', 'Marketing', 'Xây dựng', 'Khác'],
    datasets: [
      {
        label: 'Số lượng job',
        data: jobStats.length > 0 ? jobStats.map(j => j.count) : [50, 30, 20, 10, 15],
        backgroundColor: [
          'rgba(59,130,246,0.7)',
          'rgba(16,185,129,0.7)',
          'rgba(251,191,36,0.7)',
          'rgba(239,68,68,0.7)',
          'rgba(107,114,128,0.7)',
        ],
      },
    ],
  };
  const lineData = {
    labels: revenueStats.length > 0 ? revenueStats.map(r => r.month) : ['T1', 'T2', 'T3', 'T4', 'T5', 'T6'],
    datasets: [
      {
        label: 'Doanh thu (triệu VNĐ)',
        data: revenueStats.length > 0 ? revenueStats.map(r => r.revenue) : [10, 12, 15, 13, 17, 20],
        borderColor: 'rgba(59,130,246,1)',
        backgroundColor: 'rgba(59,130,246,0.2)',
        fill: true,
      },
    ],
  };
  const packagePieData = {
    labels: packageStats.length > 0 ? packageStats.map(p => p.name) : ['Gói A', 'Gói B', 'Gói C'],
    datasets: [
      {
        label: 'Số lượng gói đã mua',
        data: packageStats.length > 0 ? packageStats.map(p => p.count) : [10, 5, 2],
        backgroundColor: [
          'rgba(59,130,246,0.7)',
          'rgba(251,191,36,0.7)',
          'rgba(16,185,129,0.7)',
          'rgba(239,68,68,0.7)',
          'rgba(107,114,128,0.7)',
        ],
      },
    ],
  };

  return (
    <div className="flex flex-col gap-8 my-8">
      {/* Summary cards */}
      <div className="grid grid-cols-1 md:grid-cols-6 gap-6 mb-8">
        <div className="bg-white rounded-xl shadow p-6 flex flex-col items-center justify-center border-b-4 border-blue-500">
          <div className="text-xs text-gray-500 mb-1">Tổng số người dùng</div>
          <div className="text-3xl font-extrabold text-blue-700">{userCount}</div>
        </div>
        <div className="bg-white rounded-xl shadow p-6 flex flex-col items-center justify-center border-b-4 border-green-500">
          <div className="text-xs text-gray-500 mb-1">Tổng số công việc</div>
          <div className="text-3xl font-extrabold text-green-600">{jobCount}</div>
        </div>
        <div className="bg-white rounded-xl shadow p-6 flex flex-col items-center justify-center border-b-4 border-purple-500">
          <div className="text-xs text-gray-500 mb-1">Tổng số recruiter</div>
          <div className="text-3xl font-extrabold text-purple-600">{recruiterCount}</div>
        </div>
        <div className="bg-white rounded-xl shadow p-6 flex flex-col items-center justify-center border-b-4 border-pink-500">
          <div className="text-xs text-gray-500 mb-1">Tổng số ứng viên</div>
          <div className="text-3xl font-extrabold text-pink-600">{candidateCount}</div>
        </div>
        <div className="bg-white rounded-xl shadow p-6 flex flex-col items-center justify-center border-b-4 border-orange-400">
          <div className="text-xs text-gray-500 mb-1">Tổng số CV đã ứng tuyển</div>
          <div className="text-3xl font-extrabold text-orange-600">{cvCount}</div>
        </div>
        <div className="bg-white rounded-xl shadow p-6 flex flex-col items-center justify-center border-b-4 border-indigo-400">
          <div className="text-xs text-gray-500 mb-1">Tổng số gói đã bán</div>
          <div className="text-3xl font-extrabold text-indigo-600">{packageStats.reduce((sum, p) => sum + p.count, 0)}</div>
        </div>
      </div>
      {/* Main charts */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="bg-white rounded-2xl shadow-xl p-6 flex flex-col items-center justify-center">
          <h2 className="font-bold mb-2 text-blue-700 text-base">Doanh thu theo tháng</h2>
          <div className="w-full max-w-2xl mx-auto">
            <Line data={lineData} height={200} options={{ maintainAspectRatio: false }} />
          </div>
        </div>
        <div className="bg-white rounded-2xl shadow-xl p-6 flex flex-col items-center justify-center">
          <h2 className="font-bold mb-2 text-blue-700 text-base">Nhà tuyển dụng sắp hết hạn gói</h2>
          <div className="w-full max-w-xs md:max-w-sm mx-auto">
            {expiringRecruiters.length === 0 ? (
              <div className="text-gray-500 text-sm text-center">Không có recruiter nào sắp hết hạn gói trong 7 ngày tới.</div>
            ) : (
              <table className="min-w-full text-xs text-left border mt-2">
                <thead>
                  <tr className="bg-blue-50">
                    <th className="px-2 py-1 font-semibold">Tên recruiter</th>
                    <th className="px-2 py-1 font-semibold">Gói</th>
                    <th className="px-2 py-1 font-semibold">Hết hạn</th>
                  </tr>
                </thead>
                <tbody>
                  {expiringRecruiters.map((r, idx) => (
                    <tr key={idx} className="border-b">
                      <td className="px-2 py-1">{r.fullName || r.username || r.email}</td>
                      <td className="px-2 py-1">{r.servicePackageName}</td>
                      <td className="px-2 py-1">{r.expirationDate ? new Date(r.expirationDate).toLocaleDateString() : ''}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
      {/* Nếu muốn thêm các chart khác, có thể chuyển sang tab/accordion hoặc ẩn đi */}
    </div>
  );
}
