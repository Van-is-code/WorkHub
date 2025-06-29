package gr3.workhub.controller;

import gr3.workhub.entity.Admin;
import gr3.workhub.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("/create-super-admin")
    public Admin createSuperAdmin(@RequestParam String fullname,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String phone) {
        return adminService.createAdmin(fullname, email, password, phone, Admin.Role.super_admin);
    }
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("/create-moderator")
    public Admin createModerator(@RequestParam String fullname,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam(required = false) String phone) {
        return adminService.createAdmin(fullname, email, password, phone, Admin.Role.moderator);
    }
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PostMapping("/create-support")
    public Admin createSupport(@RequestParam String fullname,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String phone) {
        return adminService.createAdmin(fullname, email, password, phone, Admin.Role.support);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @GetMapping("/{id}")
    public Admin getAdminById(@PathVariable Integer id) {
        return adminService.getAdminById(id);
    }
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @GetMapping("/search")
    public Admin getAdminByEmail(@RequestParam String email) {
        return adminService.getAdminByEmail(email);
    }
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN, ROLE_MODERATOR, ROLE_SUPPORT')")
    @GetMapping("/role")
    public List<Admin> getAdminsByRole(@RequestParam Admin.Role role) {
        return adminService.getAdminsByRole(role);
    }

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @PutMapping("/{id}")
    public Admin updateAdmin(@PathVariable Integer id,
                             @RequestParam String fullname,
                             @RequestParam(required = false) String phone) {
        return adminService.updateAdmin(id, fullname, phone);
    }
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
    }
}