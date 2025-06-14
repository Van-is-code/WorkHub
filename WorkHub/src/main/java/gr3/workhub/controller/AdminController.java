package gr3.workhub.controller;

import gr3.workhub.entity.Admin;
import gr3.workhub.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workhub/api/v1/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/create-super-admin")
    public Admin createSuperAdmin(@RequestParam String fullname,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam(required = false) String phone) {
        return adminService.createAdmin(fullname, email, password, phone, Admin.Role.super_admin);
    }

    @PostMapping("/create-moderator")
    public Admin createModerator(@RequestParam String fullname,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam(required = false) String phone) {
        return adminService.createAdmin(fullname, email, password, phone, Admin.Role.moderator);
    }

    @PostMapping("/create-support")
    public Admin createSupport(@RequestParam String fullname,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String phone) {
        return adminService.createAdmin(fullname, email, password, phone, Admin.Role.support);
    }


    @GetMapping("/{id}")
    public Admin getAdminById(@PathVariable Integer id) {
        return adminService.getAdminById(id);
    }

    @GetMapping("/search")
    public Admin getAdminByEmail(@RequestParam String email) {
        return adminService.getAdminByEmail(email);
    }

    @GetMapping("/role")
    public List<Admin> getAdminsByRole(@RequestParam Admin.Role role) {
        return adminService.getAdminsByRole(role);
    }

    @PutMapping("/{id}")
    public Admin updateAdmin(@PathVariable Integer id,
                             @RequestParam String fullname,
                             @RequestParam(required = false) String phone) {
        return adminService.updateAdmin(id, fullname, phone);
    }

    @DeleteMapping("/{id}")
    public void deleteAdmin(@PathVariable Integer id) {
        adminService.deleteAdmin(id);
    }
}