package com.example.zkproject.viewmodel;

import com.example.zkproject.model.User;
import com.example.zkproject.repository.UserRepository;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import java.util.List;

public class UserListViewModel {

    private List<User> userList;

    @Init
    public void init() {
        UserRepository repo = new UserRepository();
        userList = repo.findAll();
    }

    public List<User> getUserList() {
        return userList;
    }
    @Command
    public void goBack() {
        Executions.sendRedirect("/home.zul");
    }
    @Command
    public void closeTab() {
        BindUtils.postGlobalCommand(null, null, "closeUserTab", null);
    }

}

