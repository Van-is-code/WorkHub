import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-dark text-white font-heading">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-10">
          <div>
            <h3 className="text-2xl font-extrabold mb-4 text-accent">WorkHub</h3>
            <p className="text-muted text-base mb-4">Nền tảng tìm kiếm việc làm hàng đầu Việt Nam</p>
            <div className="flex gap-3 mt-4">
              <a href="#" className="w-8 h-8 flex items-center justify-center rounded-full bg-secondary hover:bg-accent transition"><i className="fab fa-facebook-f text-primary"></i></a>
              <a href="#" className="w-8 h-8 flex items-center justify-center rounded-full bg-secondary hover:bg-accent transition"><i className="fab fa-linkedin-in text-primary"></i></a>
              <a href="#" className="w-8 h-8 flex items-center justify-center rounded-full bg-secondary hover:bg-accent transition"><i className="fab fa-twitter text-primary"></i></a>
            </div>
          </div>
          <div>
            <h4 className="text-lg font-bold mb-4">Ứng viên</h4>
            <ul className="space-y-2">
              <li><Link to="/jobs" className="hover:text-accent transition">Tìm việc làm</Link></li>
              <li><Link to="/profile" className="hover:text-accent transition">Tạo CV</Link></li>
              <li><Link to="/resources" className="hover:text-accent transition">Tài nguyên</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="text-lg font-bold mb-4">Nhà tuyển dụng</h4>
            <ul className="space-y-2">
              <li><Link to="/post-job" className="hover:text-accent transition">Đăng tin tuyển dụng</Link></li>
              <li><Link to="/pricing" className="hover:text-accent transition">Bảng giá</Link></li>
              <li><Link to="/employer-resources" className="hover:text-accent transition">Tài nguyên</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="text-lg font-bold mb-4">Liên hệ</h4>
            <ul className="space-y-2 text-muted">
              <li>Email: support@workhub.vn</li>
              <li>Hotline: 1900 1234</li>
              <li>Địa chỉ: 123 Đường ABC, Quận 1, TP.HCM</li>
            </ul>
          </div>
        </div>
        <div className="mt-12 border-t border-border pt-6 text-center text-muted text-sm">
          © {new Date().getFullYear()} WorkHub. All rights reserved.
        </div>
      </div>
    </footer>
  );
};

export default Footer;