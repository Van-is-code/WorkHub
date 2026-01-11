import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  BriefcaseIcon,
  BuildingOfficeIcon,
  MapPinIcon,
  CurrencyDollarIcon,
  ClockIcon,
  TagIcon,
  CalendarIcon
} from '@heroicons/react/24/outline';

export default function FeaturedJobList({ jobs = [], isLoading }) {
  const navigate = useNavigate();
  return (
    <div className="my-12">
      <h2 className="text-3xl font-heading font-bold mb-10 text-center text-primary tracking-tight">Việc làm nổi bật</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {isLoading ? (
          <div className="col-span-3 text-center text-primary text-lg">Đang tải...</div>
        ) : jobs && jobs.length ? (
          jobs.map(job => (
            <div key={job.id} className="relative bg-white rounded-2xl shadow-card border border-border p-8 flex flex-col items-center transition-transform hover:-translate-y-2 hover:shadow-xl">
              <div className="absolute -top-4 left-1/2 -translate-x-1/2 w-16 h-16 bg-primary rounded-full flex items-center justify-center shadow-lg text-white text-2xl font-black border-4 border-white">
                <BriefcaseIcon className="w-8 h-8" />
              </div>
              <div className="mt-8 mb-2 text-xl font-bold text-primary text-center">{job.title}</div>
              <div className="flex flex-col gap-2 w-full mb-4">
                <div className="flex flex-wrap justify-center gap-3 text-muted text-sm">
                  <span className="flex items-center gap-1"><BuildingOfficeIcon className="w-5 h-5" />{job.companyName}</span>
                  <span className="flex items-center gap-1"><MapPinIcon className="w-5 h-5" />{job.location}</span>
                </div>
                <div className="flex flex-wrap justify-center gap-3 text-muted text-sm">
                  <span className="flex items-center gap-1"><CurrencyDollarIcon className="w-5 h-5 text-green-600" />{job.salary || 'Thoả thuận'}</span>
                  <span className="flex items-center gap-1"><ClockIcon className="w-5 h-5" />{job.type}</span>
                  {job.experience && (
                    <span className="flex items-center gap-1"><TagIcon className="w-5 h-5" />{job.experience}</span>
                  )}
                  {job.deadline && (
                    <span className="flex items-center gap-1"><CalendarIcon className="w-5 h-5" />Hạn: {job.deadline}</span>
                  )}
                </div>
              </div>
              {job.description && (
                <div className="text-gray-500 text-sm mb-4 line-clamp-2 text-center">{job.description}</div>
              )}
              <button
                className="w-full mt-auto px-6 py-3 rounded-full bg-primary text-white font-bold text-lg shadow hover:bg-accent transition"
                onClick={() => navigate(`/jobs/${job.id}`)}
              >Xem chi tiết</button>
            </div>
          ))
        ) : (
          <div className="col-span-3 text-center text-danger">Không có việc làm nổi bật.</div>
        )}
      </div>
    </div>
  );
}
