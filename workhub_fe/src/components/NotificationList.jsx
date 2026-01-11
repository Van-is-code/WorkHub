import React, { useEffect, useState } from 'react';
import { getNotifications } from '../apiService';

const NotificationList = ({ userId }) => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!userId) return;
    getNotifications(userId)
      .then(res => {
        setNotifications(res.data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [userId]);

  if (loading) return <div>Đang tải thông báo...</div>;
  if (!notifications.length) return <div>Không có thông báo nào.</div>;

  return (
    <div className="bg-white rounded shadow p-4 mt-4">
      <h2 className="text-lg font-bold mb-2">Thông báo</h2>
      <ul>
        {notifications.map((n, idx) => (
          <li key={idx} className="mb-2 border-b pb-2">
            <div>{n.message}</div>
            <div className="text-xs text-gray-500">{new Date(n.createdAt).toLocaleString()}</div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default NotificationList;
