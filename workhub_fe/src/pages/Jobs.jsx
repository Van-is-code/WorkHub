import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, useLocation } from 'react-router-dom';
import { 
  BriefcaseIcon, 
  BuildingOfficeIcon, 
  MapPinIcon, 
  CurrencyDollarIcon,
  ClockIcon,
  CalendarIcon,
  TagIcon
} from '@heroicons/react/24/outline';
import { getJobs, getJobCategories, getJobTypes, getJobPositions, getSkills } from '../apiService';

function Jobs() {
  const location = useLocation();
  const [filters, setFilters] = useState({
    title: '',
    typeId: '',
    categoryId: '',
    location: '',
    minSalary: '',
    maxSalary: '',
    positionId: '',
    skillId: ''
  });

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const title = params.get('title') || '';
    setFilters(prev => ({
      ...prev,
      title: title
    }));
  }, [location.search]);

  const { data: jobs, isLoading, error } = useQuery({
    queryKey: ['jobs', filters],
    queryFn: async () => {
      const params = new URLSearchParams();
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
      });
      const token = localStorage.getItem('token');
      const response = await getJobs(params.toString(), {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    },
    enabled: true,
  });

  const { data: categories, isLoading: isLoadingCategories, isError: isErrorCategories } = useQuery({
    queryKey: ['jobCategories'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getJobCategories({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    }
  });

  const { data: types, isLoading: isLoadingTypes, isError: isErrorTypes } = useQuery({
    queryKey: ['jobTypes'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getJobTypes({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    }
  });

  const { data: positions, isLoading: isLoadingPositions, isError: isErrorPositions } = useQuery({
    queryKey: ['jobPositions'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getJobPositions({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    }
  });

  const { data: skills, isLoading: isLoadingSkills, isError: isErrorSkills } = useQuery({
    queryKey: ['skills'],
    queryFn: async () => {
      const token = localStorage.getItem('token');
      const response = await getSkills({
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      return response.data;
    }
  });

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="animate-pulse space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="bg-white p-6 rounded-lg shadow-sm">
              <div className="h-4 bg-gray-200 rounded w-3/4"></div>
              <div className="space-y-3 mt-4">
                <div className="h-4 bg-gray-200 rounded"></div>
                <div className="h-4 bg-gray-200 rounded w-5/6"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h3 className="text-lg font-medium text-gray-900">Có lỗi xảy ra</h3>
          <p className="mt-1 text-sm text-gray-500">Vui lòng thử lại sau</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      {/* Filters */}
      <div className="bg-white p-6 rounded-lg shadow-sm mb-8">
        <h2 className="text-lg font-medium text-gray-900 mb-4">Bộ lọc tìm kiếm</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label htmlFor="categoryId" className="block text-sm font-medium text-gray-700">Ngành nghề</label>
            <select
              name="categoryId"
              id="categoryId"
              value={filters.categoryId}
              onChange={handleFilterChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
              disabled={isLoadingCategories || isErrorCategories}
            >
              <option value="">Tất cả ngành nghề</option>
              {categories?.map(category => (
                <option key={category.id} value={category.id}>{category.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="typeId" className="block text-sm font-medium text-gray-700">Loại công việc</label>
            <select
              name="typeId"
              id="typeId"
              value={filters.typeId}
              onChange={handleFilterChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
              disabled={isLoadingTypes || isErrorTypes}
            >
              <option value="">Tất cả loại công việc</option>
              {types?.map(type => (
                <option key={type.id} value={type.id}>{type.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="positionId" className="block text-sm font-medium text-gray-700">Vị trí công việc</label>
            <select
              name="positionId"
              id="positionId"
              value={filters.positionId}
              onChange={handleFilterChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
              disabled={isLoadingPositions || isErrorPositions}
            >
              <option value="">Tất cả vị trí</option>
              {positions?.map(position => (
                <option key={position.id} value={position.id}>{position.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="skillId" className="block text-sm font-medium text-gray-700">Kỹ năng</label>
            <select
              name="skillId"
              id="skillId"
              value={filters.skillId}
              onChange={handleFilterChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-primary focus:ring-primary sm:text-sm"
              disabled={isLoadingSkills || isErrorSkills}
            >
              <option value="">Tất cả kỹ năng</option>
              {skills?.map(skill => (
                <option key={skill.id} value={skill.id}>{skill.name}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Jobs List */}
      <div className="space-y-6">
        {jobs?.map((job) => (
          <div key={job.id} className="bg-white p-6 rounded-lg shadow-sm hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between">
              <div>
                <h3 className="text-lg font-medium text-gray-900">
                  <Link to={`/jobs/${job.id}`} className="hover:text-primary">
                    {job.title}
                  </Link>
                </h3>
              </div>
              <div className="flex items-center">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  job.postAt === 'urgent' ? 'bg-red-100 text-red-800' :
                  job.postAt === 'proposal' ? 'bg-blue-100 text-blue-800' :
                  'bg-green-100 text-green-800'
                }`}>
                  {job.postAt === 'urgent' ? 'Khẩn cấp' :
                   job.postAt === 'proposal' ? 'Đề xuất' :
                   'Tiêu chuẩn'}
                </span>
              </div>
            </div>

            <div className="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <div className="flex items-center text-sm text-gray-500">
                {job.location}
              </div>
              <div className="flex items-center text-sm text-gray-500">
                {job.salaryRange}
              </div>
              <div className="flex items-center text-sm text-gray-500">
                {job.type?.name}
              </div>
            </div>

            <div className="mt-4">
              <div className="flex items-center text-sm text-gray-500 mb-2">
                <span className="font-medium">Ngành nghề:</span>
                <span className="ml-2">{job.category?.name}</span>
              </div>
            </div>

            <div className="mt-4 flex items-center justify-between">
              <div className="flex items-center text-sm text-gray-500">
                <div>
                  <div>Đăng: {new Date(job.createdAt).toLocaleDateString('vi-VN')}</div>
                  {job.deadline && (
                    <div>Hạn nộp: {new Date(job.deadline).toLocaleDateString('vi-VN')}</div>
                  )}
                </div>
              </div>
              <Link
                to={`/jobs/${job.id}`}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
              >
                Xem chi tiết
              </Link>
            </div>
          </div>
        ))}

        {jobs?.length === 0 && (
          <div className="text-center py-12">
            <h3 className="text-lg font-medium text-gray-900">Không tìm thấy việc làm phù hợp</h3>
            <p className="mt-1 text-sm text-gray-500">Vui lòng thử lại với bộ lọc khác</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default Jobs;