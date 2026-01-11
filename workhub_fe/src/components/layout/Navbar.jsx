import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import SearchIcon from '@mui/icons-material/Search';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import LogoutIcon from '@mui/icons-material/Logout';

function parseJwt(token) {
  if (!token) return null;
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch {
    return null;
  }
}

function Navbar() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Lấy user info từ token
  const token = localStorage.getItem('token');
  const user = parseJwt(token);

  const handleLogout = async () => {
    localStorage.removeItem('token');
    queryClient.setQueryData(['profile'], null);
    setIsDropdownOpen(false);
    navigate('/');
  };

  const handleSearch = (e) => {
    if (e.key === 'Enter' && searchQuery.trim() !== '') {
      navigate(`/jobs?title=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  return (
    <nav className="bg-white shadow-nav font-heading">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-20 items-center">
          <div className="flex items-center gap-8">
            <Link to="/" className="flex items-center gap-2 text-3xl font-bold text-primary font-heading">
              <img src="/workhub-logo.png" alt="WorkHub Logo" className="h-10 w-auto" />
              
            </Link>
            <div className="hidden md:flex gap-6 text-base font-medium">
              <Link to="/jobs" className="hover:text-primary transition">Việc làm</Link>
              <Link to="/companies" className="hover:text-primary transition">Công ty</Link>
              <Link to="/pricing" className="hover:text-primary transition">Bảng giá</Link>
              
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className="relative">
              <input
                type="text"
                className="rounded-full border border-border px-4 py-2 focus:outline-none focus:ring-2 focus:ring-primary bg-secondary text-dark w-56"
                placeholder="Tìm việc, công ty..."
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                onKeyDown={handleSearch}
              />
              <button
                className="absolute right-2 top-1/2 -translate-y-1/2 text-primary"
                onClick={handleSearch}
                aria-label="Tìm kiếm"
              >
                <SearchIcon />
              </button>
            </div>
            {user ? (
              <div className="relative">
                <button
                  className="flex items-center gap-2 px-4 py-2 rounded-full bg-primary text-white font-semibold hover:bg-accent transition"
                  onClick={() => setIsDropdownOpen(v => !v)}
                >
                  <AccountCircleIcon />
                  {user.name || 'Tài khoản'}
                </button>
                {isDropdownOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-card z-50 py-2">
                    {user && user.role === 'admin' ? (
                      <Link to="/admin/stats" className="block px-4 py-2 hover:bg-secondary text-dark">Bảng điều khiển</Link>
                    ) : (
                      <>
                        <Link to={user && user.role === 'recruiter' ? `/profile/${user.id}` : '/profile'} className="block px-4 py-2 hover:bg-secondary text-dark">Hồ sơ cá nhân</Link>
                        {(user && user.role === 'recruiter') && (
                          <Link to="/dashboard" className="block px-4 py-2 hover:bg-secondary text-dark">Bảng điều khiển</Link>
                        )}
                      </>
                    )}
                    <button onClick={handleLogout} className="w-full text-left px-4 py-2 hover:bg-secondary text-danger flex items-center gap-2">
                      <LogoutIcon fontSize="small" /> Đăng xuất
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex gap-2">
                <Link to="/login" className="px-4 py-2 rounded-full bg-primary text-white font-semibold hover:bg-accent transition">Đăng nhập</Link>
                <Link to="/register" className="px-4 py-2 rounded-full border border-primary text-primary font-semibold hover:bg-primary hover:text-white transition">Đăng ký</Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;