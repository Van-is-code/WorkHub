import React, { useEffect, useState } from 'react';
import { getServicePackages, buyServicePackage, getUserPackages } from '../apiService';
import { useNavigate } from 'react-router-dom';

export default function ServicePackageList({ user }) {
  const [packages, setPackages] = useState([]);
  const [userPackages, setUserPackages] = useState([]);
  const [userPackageDetails, setUserPackageDetails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [showUpgrade, setShowUpgrade] = useState(false);
  const [selectedPkg, setSelectedPkg] = useState(null);
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const config = token ? { headers: { Authorization: `Bearer ${token}` } } : {};

  useEffect(() => {
    const fetchPackages = async () => {
      setLoading(true);
      try {
        const res = await getServicePackages(config);
        setPackages(res.data);
        if (user?.id) {
          const upRes = await getUserPackages(user.id, config);
          setUserPackages(upRes.data.map(up => up.servicePackage?.id));
          setUserPackageDetails(upRes.data);
          // Log dữ liệu userPackageDetails để debug (log cả user, upRes)
          console.log('user:', user);
          console.log('userPackageDetails:', upRes.data);
        } else {
          console.log('user is undefined or missing id:', user);
        }
      } catch (err) {
        setPackages([]);
        setUserPackages([]);
        console.log('Error fetching packages:', err);
      }
      setLoading(false);
    };
    fetchPackages();
  }, [user]);

  const handleBuy = async (pkg) => {
    if (!token) {
      navigate('/login');
      return;
    }
    // Kiểm tra nếu user có gói thấp hơn còn hạn
    const now = new Date();
    const lowerActive = userPackageDetails.find(up => {
      const exp = up.expirationDate ? new Date(up.expirationDate) : null;
      return up.servicePackage && up.servicePackage.price < pkg.price && exp && exp > now;
    });
    // Kiểm tra nếu đã sở hữu gói này và còn hạn
    const up = userPackageDetails.find(up => up.servicePackage && up.servicePackage.id === pkg.id);
    const exp = up && up.expirationDate ? new Date(up.expirationDate) : null;
    const isActive = exp && exp > now;
    if (isActive) {
      setMessage('Bạn đã sở hữu gói này, không thể mua lại.');
      setShowUpgrade(false);
      return;
    }
    if (lowerActive) {
      setSelectedPkg(pkg);
      setShowUpgrade(true);
      setMessage('Nếu bạn nâng cấp lên gói này, gói cũ (' + lowerActive.servicePackage.name + ') sẽ bị xóa khỏi tài khoản của bạn.');
      return;
    }
    try {
      navigate('/payment', { state: { pkg } });
    } catch (err) {
      if (err?.response?.data?.message?.includes('không thể mua lại')) {
        setMessage('Bạn đã sở hữu gói này, không thể mua lại.');
      } else {
        setMessage('Có lỗi xảy ra, vui lòng thử lại.');
      }
    }
  };

  // Tạo map: { [packageId]: expirationDate } chỉ với gói còn hạn
  const userPackageMap = {};
  const now = new Date();
  userPackageDetails.forEach(up => {
    if (up.servicePackage && up.expirationDate) {
      const exp = new Date(up.expirationDate);
      if (exp > now) {
        userPackageMap[up.servicePackage.id] = up.expirationDate;
      }
    }
  });

  return (
    <div className="my-12">
      <h2 className="text-3xl font-heading font-bold mb-10 text-center text-primary tracking-tight">Các gói dịch vụ nổi bật</h2>
      {loading ? (
        <div className="text-center text-primary text-lg">Đang tải...</div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {packages.map(pkg => {
            const expDateStr = userPackageMap[pkg.id];
            let isActive = false;
            let expDate = null;
            if (expDateStr) {
              expDate = new Date(expDateStr);
              isActive = expDate > now;
            }
            return (
              <div key={pkg.id} className="relative bg-white rounded-2xl shadow-card border border-border p-8 flex flex-col items-center transition-transform hover:-translate-y-2 hover:shadow-xl">
                <div className="absolute -top-4 left-1/2 -translate-x-1/2 w-16 h-16 bg-accent rounded-full flex items-center justify-center shadow-lg text-white text-2xl font-black border-4 border-white">
                  <span>★</span>
                </div>
                <div className="mt-8 mb-2 text-xl font-bold text-primary text-center">{pkg.name}</div>
                <div className="text-gray-500 mb-4 text-center min-h-[48px]">{pkg.description}</div>
                <div className="text-3xl font-extrabold text-green-600 mb-2">{pkg.price.toLocaleString()} <span className="text-base font-medium">VNĐ</span></div>
                <div className="text-gray-400 mb-2">Thời hạn: <span className="font-semibold text-dark">{pkg.duration} ngày</span></div>
                {isActive && expDate && (
                  <div className="mb-2 text-sm font-semibold text-green-600">Hạn sử dụng đến: {expDate.toLocaleDateString('vi-VN')}</div>
                )}
                <button
                  className="w-full mt-auto px-6 py-3 rounded-full bg-primary text-white font-bold text-lg shadow hover:bg-accent transition disabled:opacity-60"
                  onClick={() => handleBuy(pkg)}
                  disabled={isActive}
                >
                  {isActive
                    ? 'Bạn đã sở hữu gói này'
                    : user && user.role === 'candidate'
                      ? 'Mua & trở thành nhà tuyển dụng'
                      : 'Mua gói này'}
                </button>
              </div>
            );
          })}
        </div>
      )}
      {message && <div className="mt-6 text-center text-green-600 font-bold text-lg">{message}</div>}
      {showUpgrade && selectedPkg && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-2xl p-10 w-full max-w-md">
            <h3 className="text-2xl font-bold mb-4 text-primary text-center">{userPackages.includes(selectedPkg.id) ? 'Bạn đã sở hữu gói này' : 'Nâng cấp gói dịch vụ'}</h3>
            <div className="mb-6 text-center text-gray-700">
              {userPackages.includes(selectedPkg.id)
                ? <>Bạn đã mua gói <span className="font-bold text-green-700">{selectedPkg.name}</span>.<br/>Bạn muốn nâng cấp lên gói cao hơn?</>
                : message || <span>Bạn muốn nâng cấp lên gói này?<br/><span className="text-red-600 font-semibold">Lưu ý: Gói cũ sẽ bị xóa khỏi tài khoản khi nâng cấp!</span></span>}
            </div>
            <div className="flex gap-3 justify-center">
              <button className="px-5 py-2 bg-gray-200 rounded-full font-semibold" onClick={() => { setShowUpgrade(false); setMessage(''); }}>Đóng</button>
              {!userPackages.includes(selectedPkg.id) && (
                <button className="px-5 py-2 bg-primary text-white rounded-full font-semibold hover:bg-accent transition" onClick={() => { setShowUpgrade(false); setMessage(''); navigate('/payment', { state: { pkg: selectedPkg, isUpgrade: true } }); }}>Đồng ý nâng cấp</button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
