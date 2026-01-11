import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { buyServicePackage } from '../apiService';

export default function PaymentConfirm() {
  const [params] = useSearchParams();
  const [confirmed, setConfirmed] = useState(false);
  const [error, setError] = useState('');
  const [waiting, setWaiting] = useState(false);
  const navigate = useNavigate();
  const pkgId = params.get('pkgId');

  useEffect(() => {
    async function confirmFake() {
      if (pkgId) {
        setWaiting(true);
        try {
          await buyServicePackage(
            JSON.parse(atob(localStorage.getItem('token').split('.')[1])).id,
            pkgId,
            { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }
          );
          setConfirmed(true);
          setTimeout(() => navigate('/', { replace: true }), 2000);
        } catch (e) {
          setError('Có lỗi khi kích hoạt gói! ' + (e?.message || ''));
        } finally {
          setWaiting(false);
        }
      }
    }
    confirmFake();
  }, [pkgId, navigate]);

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50">
      <div className="bg-white rounded-xl shadow p-8 w-full max-w-md text-center">
        <h2 className="text-2xl font-bold mb-4 text-blue-700">Xác nhận thanh toán</h2>
        {error ? (
          <div className="text-red-600 font-bold">{error}</div>
        ) : confirmed ? (
          <div className="text-green-700 font-bold">Thanh toán thành công! Bạn có thể quay lại trang web trên máy tính.</div>
        ) : waiting ? (
          <div className="text-gray-600">Đang xác nhận thanh toán...</div>
        ) : (
          <div className="text-gray-600">Đang xử lý...</div>
        )}
      </div>
    </div>
  );
}
