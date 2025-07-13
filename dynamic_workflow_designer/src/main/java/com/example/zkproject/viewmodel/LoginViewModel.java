package com.example.zkproject.viewmodel;

import com.example.zkproject.model.User;
import com.example.zkproject.repository.UserRepository;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Messagebox;

public class LoginViewModel {

    private String username;
    private String password;

    @Command
    public void login() {
        UserRepository repo = new UserRepository();
        User user = repo.findByUsername(username);

        if (user == null) {
            Messagebox.show("Tài khoản không tồn tại!", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        if (!user.getPassword().equals(password)) {
            Messagebox.show("Sai username hoặc passwork", "Lỗi", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        Executions.getCurrent().getSession().setAttribute("currentUser", user);

        Executions.sendRedirect("home.zul");
    }

    @Command
    public void goToRegister() {
        Executions.sendRedirect("register.zul");
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
