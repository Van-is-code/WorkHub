import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { login } from '../apiService';
import { Link } from 'react-router-dom';

const Login = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState(''); // Thêm state để hiển thị lỗi

  const loginMutation = useMutation({
    mutationFn: async (data) => {
      const response = await login(data);
      return response.data; // response.data là { token: ... }
    },
    onSuccess: (result) => {
      setError('');
      try {
        const token = result.token; // lấy đúng chuỗi token
        const payload = JSON.parse(atob(token.split('.')[1]));
        localStorage.setItem('token', token);
        setTimeout(() => {
          queryClient.clear(); // Xóa toàn bộ cache react-query để lấy lại user mới
          navigate('/'); // Tất cả role đều về trang home
        }, 100);
      } catch (e) {
        alert('Đăng nhập thành công nhưng không xác định được role!');
        navigate('/');
      }
    },
    onError: (error) => {
      let msg = 'Đăng nhập thất bại. Vui lòng thử lại sau.';
      if (error.response && error.response.data && error.response.data.message) {
        msg = error.response.data.message;
      }
      setError(msg);
    },
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    loginMutation.mutate(formData);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 to-accent/10 flex flex-col justify-center items-center py-12 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-10 flex flex-col items-center">
        <Link to="/" className="mb-6 flex items-center gap-2 justify-center">
          <img src="/workhub-logo.png" alt="WorkHub Logo" className="h-12 w-auto" />
        </Link>
        <h2 className="text-3xl text-dark mb-2 text-center font-heading">Đăng nhập</h2>
        <p className="text-muted mb-8 text-center">Chào mừng bạn quay lại! Hãy đăng nhập để tiếp tục.</p>
        <form className="w-full space-y-6" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-dark mb-1">Email</label>
            <div className="relative">
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                value={formData.email}
                onChange={handleChange}
                className="block w-full px-4 py-3 rounded-full border border-border shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-base"
                placeholder="Nhập email..."
              />
            </div>
          </div>
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-dark mb-1">Mật khẩu</label>
            <div className="relative">
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                required
                value={formData.password}
                onChange={handleChange}
                className="block w-full px-4 py-3 rounded-full border border-border shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-base"
                placeholder="Nhập mật khẩu..."
              />
            </div>
          </div>
          {error && (
            <div className="text-red-500 text-sm text-center">{error}</div>
          )}
          <button
            type="submit"
            disabled={loginMutation.isPending}
            className="w-full py-3 rounded-full bg-primary text-white font-bold text-lg shadow hover:bg-accent transition disabled:opacity-60"
          >
            {loginMutation.isPending ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>
        <div className="mt-8 w-full text-center text-sm text-muted">
          Chưa có tài khoản?{' '}
          <Link to="/register" className="text-primary font-semibold hover:underline">Đăng ký ngay</Link>
        </div>
      </div>
    </div>
  );
};

export default Login;