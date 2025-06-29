package gr3.workhub.controller;

import gr3.workhub.dto.UserDTO;
import gr3.workhub.entity.User;
import gr3.workhub.service.UserService;
import gr3.workhub.service.AdminService;
import gr3.workhub.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@Tag(name = "✅ Authentication", description = "API xác thực cho ứng viên, nhà tuyển dụng và admin")
@RestController
@CrossOrigin
@RequestMapping("/workhub/api/v1")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private TokenService tokenService;

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Đăng ký nhà tuyển dụng", description = "Tạo tài khoản recruiter mới")
    @PostMapping("/recruiter/register")
    public String registerRecruiter(
            @RequestBody UserDTO userDTO,
            @Parameter(description = "Mật khẩu") @RequestParam String password) {
        return userService.registerRecruiter(userDTO, password);
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên> Đăng ký ứng viên", description = "Tạo tài khoản candidate mới")
    @PostMapping("/candidate/register")
    public String registerCandidate(
            @RequestBody UserDTO userDTO,
            @Parameter(description = "Mật khẩu") @RequestParam String password) {
        return userService.registerCandidate(userDTO, password);
    }

    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng> Đăng nhập recruiter", description = "Đăng nhập hệ thống với tài khoản recruiter")
    @PostMapping("/recruiter/login")
    public String loginRecruiter(
            @Parameter(description = "Email") @RequestParam String email,
            @Parameter(description = "Mật khẩu") @RequestParam String password) {
        return userService.login(email, password, User.Role.recruiter);
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên> Đăng nhập candidate", description = "Đăng nhập hệ thống với tài khoản candidate")
    @PostMapping("/candidate/login")
    public String loginCandidate(
            @Parameter(description = "Email") @RequestParam String email,
            @Parameter(description = "Mật khẩu") @RequestParam String password) {
        return userService.login(email, password, User.Role.candidate);
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên> Đăng nhập admin", description = "Đăng nhập hệ thống với tài khoản admin")
    @PostMapping("/admin/login")
    public String loginAdmin(
            @Parameter(description = "Email admin") @RequestParam String email,
            @Parameter(description = "Mật khẩu") @RequestParam String password) {
        return adminService.login(email, password);
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên> Kích hoạt tài khoản", description = "Kích hoạt tài khoản qua email xác thực")
    @GetMapping("/activate")
    public String activateUser(@Parameter(description = "Mã token kích hoạt") @RequestParam String token) {
        userService.activateUser(token);
        return "Account activated";
    }
    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng và ứng viên> Cập nhật hồ sơ người dùng", description = "Cập nhật thông tin cá nhân người dùng")
    @PutMapping("/users/profile")
    public String updateProfile(
            @RequestBody UserDTO userDTO,
            HttpServletRequest request) {
        userService.updateProfile(request, userDTO);
        return "Profile updated";
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên> Quên mật khẩu", description = "Gửi email khôi phục mật khẩu")
    @PostMapping("/forgot-password")
    public String forgotPassword(@Parameter(description = "Email đã đăng ký") @RequestParam String email) {
        userService.forgotPassword(email);
        return "Password reset email sent";
    }

    @Operation(summary = "<EndPoint cho trang của ứng viên> Đặt lại mật khẩu", description = "Đặt lại mật khẩu mới bằng mã token")
    @PostMapping("/reset-password")
    public String resetPassword(
            @Parameter(description = "Token khôi phục") @RequestParam String token,
            @Parameter(description = "Mật khẩu mới") @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return "Password has been reset";
    }
    @Operation(summary = "<EndPoint cho trang của nhà tuyển dụng và ứng viên> Đổi mật khẩu", description = "Đổi mật khẩu người dùng đang đăng nhập")
    @PutMapping("/users/password")
    public String changePassword(
            @Parameter(description = "Mật khẩu cũ") @RequestParam String oldPassword,
            @Parameter(description = "Mật khẩu mới") @RequestParam String newPassword,
            HttpServletRequest request) {
        userService.changePassword(request, oldPassword, newPassword);
        return "Password changed";
    }

    @Operation(summary = "Đăng nhập tự động phân role", description = "Đăng nhập bằng email và password, backend tự xác định role")
    @PostMapping("/login")
    public Map<String, String> loginAutoRole(
            @Parameter(description = "Email") @RequestParam String email,
            @Parameter(description = "Mật khẩu") @RequestParam String password) {
        String token = userService.loginAutoRole(email, password);
        return java.util.Map.of("token", token);
    }
}