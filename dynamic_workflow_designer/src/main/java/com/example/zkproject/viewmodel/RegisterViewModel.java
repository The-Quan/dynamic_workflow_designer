package com.example.zkproject.viewmodel;

import com.example.zkproject.model.User;
import com.example.zkproject.model.User.Role;
import com.example.zkproject.repository.UserRepository;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.util.Clients;

import java.util.Arrays;
import java.util.List;

public class RegisterViewModel {

    private String username;
    private String password;
    private String confirmPassword;
    private Role selectedRole;
    private List<Role> roles;

    @Init
    public void init() {
        roles = Arrays.asList(Role.values());
        selectedRole = Role.NHAN_VIEN;
    }

    @Command
    public void register() {
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            Messagebox.show("Vui lòng nhập đầy đủ thông tin!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (!password.equals(confirmPassword)) {
            Messagebox.show("Mật khẩu xác nhận không khớp!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        UserRepository repo = new UserRepository();

        if (repo.findByUsername(username) != null) {
            Messagebox.show("Tên đăng nhập đã tồn tại!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(selectedRole);

        boolean success = repo.save(user);
        if (success) {
            Messagebox.show("Đăng ký thành công!", "Thông báo", Messagebox.OK, Messagebox.INFORMATION);
            Clients.evalJavaScript("window.location.href='index.zul';");
        } else {
            Messagebox.show("Đăng ký thất bại!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public Role getSelectedRole() { return selectedRole; }
    public void setSelectedRole(Role selectedRole) { this.selectedRole = selectedRole; }

    public List<Role> getRoles() { return roles; }
}
