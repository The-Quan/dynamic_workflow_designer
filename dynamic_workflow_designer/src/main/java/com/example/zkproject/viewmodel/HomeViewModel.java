package com.example.zkproject.viewmodel;

import com.example.zkproject.model.User;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;

public class HomeViewModel {

    private String username;
    private User.Role role;
    private String pageToInclude;

    @Init
    @NotifyChange("pageToInclude")
    public void init() {
        User user = (User) Executions.getCurrent().getSession().getAttribute("currentUser");

        if (user != null) {
            this.username = user.getUsername();
            this.role = user.getRole();

            this.pageToInclude = "/page/dashboard.zul";
        } else {
            Executions.sendRedirect("index.zul");
        }

    }

    public String getUsername() {
        return username;
    }

    public User.Role getRole() {
        return role;
    }

    public String getPageToInclude() {
        return pageToInclude;
    }

    @Command
    @NotifyChange("pageToInclude")
    public void goToListUser() {
        this.pageToInclude = "/page/list_user.zul";
    }

    @Command
    @GlobalCommand
    @NotifyChange("pageToInclude")
    public void goToListDocument() {
        this.pageToInclude = "/page/document/list_document.zul";
    }

    @Command
    @GlobalCommand
    @NotifyChange("pageToInclude")
    public void goToListWorkflow() {
        this.pageToInclude = "/page/workflow/workflow_config.zul";
    }


    @GlobalCommand
    @NotifyChange("pageToInclude")
    public void goToCreateDocument() {
        pageToInclude = "/page/document/action/create_document.zul";
    }

    @GlobalCommand
    @NotifyChange("pageToInclude")
    public void goToDocumentDetail() {
        pageToInclude = "/page/document/action/document_detail.zul";
    }


    @Command
    public void logout() {
        Executions.getCurrent().getSession().invalidate();
        Executions.sendRedirect("index.zul");
    }

    @GlobalCommand
    @NotifyChange("pageToInclude")
    public void closeUserTab() {
        this.pageToInclude = "";
    }

    @Command
    @NotifyChange("pageToInclude")
    public void goToDashboard() {
        pageToInclude = "/page/dashboard.zul";
    }

}
