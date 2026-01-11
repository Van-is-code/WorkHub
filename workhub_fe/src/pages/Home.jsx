import { Link, useNavigate } from 'react-router-dom';
import { Search, Work, Business, School } from '@mui/icons-material';
import { useQuery } from '@tanstack/react-query';
import { getJobs } from '../apiService';
import { 
  BriefcaseIcon, 
  BuildingOfficeIcon, 
  MapPinIcon, 
  CurrencyDollarIcon,
  ClockIcon,
  CalendarIcon,
  TagIcon
} from '@heroicons/react/24/outline';
import { useState } from 'react';
import ServicePackageList from '../components/ServicePackageList';
import FeaturedJobList from '../components/FeaturedJobList';

const Home = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  const { data: allJobs, isLoading, isError } = useQuery({
    queryKey: ['allJobs'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getJobs('', {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      // Shuffle and take the first 5 jobs
      return response.data.sort(() => 0.5 - Math.random()).slice(0, 3);
    },
  });

  const featuredJobs = allJobs; // Rename for clarity in JSX

  const handleSearch = (e) => {
    if (e.key === 'Enter' || e.type === 'click') {
      if (searchQuery.trim() !== '') {
        navigate(`/jobs?title=${encodeURIComponent(searchQuery.trim())}`);
      }
    }
  };

  // Lấy user từ token
  let user = null;
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      user = { id: payload.id, role: payload.role };
    } catch {}
  }

  if (isError) {
    return <div className="text-center text-red-500">Lỗi khi tải dữ liệu việc làm.</div>;
  }

  return (
    <div className="min-h-screen bg-white font-sans">
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-primary to-accent text-white py-24">
        <div className="max-w-5xl mx-auto px-4 flex flex-col items-center text-center">
          <h1 className="text-4xl md:text-6xl  font-heading leading-tight tracking-tight drop-shadow-lg">
            <span className="block">Tìm việc làm mơ ước của bạn</span>
            <span className="block mt-2">với <span className="text-accent font-black">WorkHub</span></span>
          </h1>
          <p className="text-lg md:text-2xl text-white/90 mb-10 font-medium drop-shadow">
            Nền tảng tuyển dụng hiện đại, kết nối ứng viên và nhà tuyển dụng hàng đầu Việt Nam.
          </p>
          <div className="flex w-full max-w-xl mx-auto bg-white rounded-full shadow-card p-2 gap-2">
            <input
              type="text"
              className="flex-1 px-6 py-3 rounded-full focus:outline-none text-dark text-lg font-sans"
              placeholder="Nhập vị trí, công ty..."
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              onKeyDown={handleSearch}
            />
            <button
              className="bg-accent text-white px-8 py-3 rounded-full font-bold text-lg hover:bg-primary transition"
              onClick={handleSearch}
            >Tìm kiếm</button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16  bg-white-50 ">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
            <div className="bg-white p-6 rounded-lg shadow-md">
              <Work className="text-primary text-4xl mb-4" />
              <h3 className="text-xl font-semibold mb-2">Tìm việc làm phù hợp</h3>
              <p className="text-gray-600">
                Khám phá hàng nghìn cơ hội việc làm từ các công ty hàng đầu
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-md">
              <Business className="text-primary text-4xl mb-4" />
              <h3 className="text-xl font-semibold mb-2">Kết nối với nhà tuyển dụng</h3>
              <p className="text-gray-600">
                Tương tác trực tiếp với các nhà tuyển dụng uy tín
              </p>
            </div>
            <div className="bg-white p-6 rounded-lg shadow-md">
              <School className="text-primary text-4xl mb-4" />
              <h3 className="text-xl font-semibold mb-2">Phát triển sự nghiệp</h3>
              <p className="text-gray-600">
                Cập nhật kỹ năng và kiến thức mới nhất từ các chuyên gia
              </p>
            </div>
          </div>
        </div>
      </section>
      <section className="py-16 bg-white border-t border-border">
        <div className="max-w-6xl mx-auto px-4">
          
          <FeaturedJobList jobs={featuredJobs} isLoading={isLoading} />
        </div>
      </section>
      

      {/* Service Packages Section */}
      <section className="py-16 bg-white border-t border-border">
        <div className="max-w-6xl mx-auto px-4">
          
          <ServicePackageList user={user} />
        </div>
      </section>
    </div>
  );
};

export default Home;