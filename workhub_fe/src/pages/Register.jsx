import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerCandidate } from '../apiService';

const Register = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (form.password !== form.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp!');
      return;
    }
    setLoading(true);
    try {
      await registerCandidate({
        fullname: form.fullName,
        email: form.email
      }, {
        params: { password: form.password }
      });
      alert('Đăng ký thành công! Vui lòng đăng nhập.');
      navigate('/login');
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng ký thất bại!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary/10 to-accent/10 flex flex-col justify-center items-center py-12 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-10 flex flex-col items-center">
        <Link to="/" className="mb-6 flex items-center gap-2 justify-center">
          <img src="/workhub-logo.png" alt="WorkHub Logo" className="h-12 w-auto" />
        </Link>
        <h2 className="text-3xl text-dark mb-2 text-center font-heading">Đăng ký tài khoản</h2>
        <p className="text-muted mb-8 text-center">Tạo tài khoản để bắt đầu hành trình sự nghiệp cùng WorkHub.</p>
        <form className="w-full space-y-6" onSubmit={handleSubmit}>
          <div>
            <label className="block text-sm font-medium text-dark mb-1">Họ và tên</label>
            <div className="relative">
              <input
                name="fullName"
                type="text"
                required
                value={form.fullName}
                onChange={handleChange}
                className="block w-full px-4 py-3 rounded-full border border-border shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-base"
                placeholder="Nhập họ và tên..."
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-dark mb-1">Email</label>
            <div className="relative">
              <input
                name="email"
                type="email"
                required
                value={form.email}
                onChange={handleChange}
                className="block w-full px-4 py-3 rounded-full border border-border shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-base"
                placeholder="Nhập email..."
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-dark mb-1">Mật khẩu</label>
            <div className="relative">
              <input
                name="password"
                type="password"
                required
                value={form.password}
                onChange={handleChange}
                className="block w-full px-4 py-3 rounded-full border border-border shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-base"
                placeholder="Nhập mật khẩu..."
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-dark mb-1">Xác nhận mật khẩu</label>
            <div className="relative">
              <input
                name="confirmPassword"
                type="password"
                required
                value={form.confirmPassword}
                onChange={handleChange}
                className="block w-full px-4 py-3 rounded-full border border-border shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-base"
                placeholder="Nhập lại mật khẩu..."
              />
            </div>
          </div>
          {error && (
            <div className="text-red-500 text-sm text-center">{error}</div>
          )}
          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-full bg-primary text-white font-bold text-lg shadow hover:bg-accent transition disabled:opacity-60"
          >
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>
        </form>
        <div className="mt-8 w-full text-center text-sm text-muted">
          Đã có tài khoản?{' '}
          <Link to="/login" className="text-primary font-semibold hover:underline">Đăng nhập</Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
