package com.example.zkproject.viewmodel;

import com.example.zkproject.model.Document;
import com.example.zkproject.model.Document.TrangThai;
import com.example.zkproject.model.User;
import com.example.zkproject.repository.DocumentRepository;
import com.example.zkproject.repository.UserRepository;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class ListDocumentViewModel {

    private final DocumentRepository documentRepository = new DocumentRepository();
    private final UserRepository userRepository = new UserRepository();

    private List<Document> documents;
    private List<User> userList;
    private Document selectedDocument;
    private boolean showDelegatePopup = false;
    private boolean showFilter = false;

    private String filterSoHieu = "";
    private String filterTieuDe = "";
    private String filterLoaiVB = "";
    private String filterTrangThai = "";
    private String username;
    private Date deadline;
    private Date ngayBanHanhFrom;
    private Date ngayBanHanhTo;
    @Init
    @NotifyChange("documents")
    public void init() {
        documents = documentRepository.findAll();
        User user = (User) Executions.getCurrent().getSession().getAttribute("currentUser");
        if (user != null) {
            this.username = user.getUsername();
        } else {
            Executions.sendRedirect("index.zul");
        }
    }

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        String msg = (String) Executions.getCurrent().getAttribute("successMessage");
        if (msg != null) {
            Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
        }
    }

    @Command
    @NotifyChange("showFilter")
    public void toggleFilter() {
        showFilter = !showFilter;
    }

    @Command
    @NotifyChange({"documents", "filterSoHieu", "filterTieuDe", "filterLoaiVB", "filterTrangThai", "deadline", "ngayBanHanhFrom", "ngayBanHanhTo"})
    public void resetFilters() {
        filterSoHieu = "";
        filterTieuDe = "";
        filterLoaiVB = "";
        filterTrangThai = "";
        deadline = null;
        ngayBanHanhFrom = null;
        ngayBanHanhTo = null;
        documents = documentRepository.findAll();
    }


    @Command
    public void goToCreateDocument() {
        BindUtils.postGlobalCommand(null, null, "goToCreateDocument", null);
    }

    @Command
    public void viewDetail(@BindingParam("doc") Document selectedDoc) {
        Executions.getCurrent().getSession().setAttribute("selectedDocument", selectedDoc);
        BindUtils.postGlobalCommand(null, null, "goToDocumentDetail", null);
    }

    @Command
    @NotifyChange("documents")
    public void approve(@BindingParam("doc") Document doc) {
        doc.setTrangThai(TrangThai.HOAN_THANH);
        boolean success = documentRepository.updateTrangThai(doc.getId(), doc.getTrangThai());

        if (success) {
            Clients.showNotification("Đã phê duyệt văn bản!", Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
            documents = documentRepository.findAll();
        } else {
            Clients.showNotification("Phê duyệt thất bại!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
        }
    }

    @Command
    @NotifyChange("documents")
    public void reject(@BindingParam("doc") Document doc) {
        Clients.showNotification("Đã từ chối văn bản!", Clients.NOTIFICATION_TYPE_WARNING, null, "top_center", 3000);
        documents = documentRepository.findAll();
    }

    @Command
    @NotifyChange({"userList", "showDelegatePopup", "selectedDocument"})
    public void delegate(@BindingParam("doc") Document doc) {
        this.selectedDocument = doc;
        this.userList = userRepository.findAll();
        this.showDelegatePopup = true;
    }

    @Command
    @NotifyChange("showDelegatePopup")
    public void delegateTo(@BindingParam("user") User user) {
        selectedDocument.setTrangThai(TrangThai.DANG_XU_LY);
        documentRepository.updateTrangThai(selectedDocument.getId(), selectedDocument.getTrangThai());

        Clients.showNotification("Đã chuyển văn bản \"" + selectedDocument.getTieuDe() + "\" cho " + user.getUsername(),
                Clients.NOTIFICATION_TYPE_INFO, null, "top_center", 3000);
        this.showDelegatePopup = false;
    }

    @Command
    @NotifyChange("showDelegatePopup")
    public void closeDelegatePopup() {
        this.showDelegatePopup = false;
    }

    @Command
    @NotifyChange("documents")
    public void search() {
        documents = documentRepository.searchWithFilters(
                filterSoHieu,
                filterTieuDe,
                filterLoaiVB,
                filterTrangThai,
                formatDate(deadline),
                ngayBanHanhFrom != null && ngayBanHanhTo != null
                        ? formatDate(ngayBanHanhFrom) + ";" + formatDate(ngayBanHanhTo)
                        : null
        );
    }
    private String formatDate(Date date) {
        if (date == null) return null;
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }
    public String formatDeadline(Date deadline) {
        if (deadline == null) return "";

        LocalDate today = LocalDate.now();
        LocalDate deadlineDate;

        if (deadline instanceof java.sql.Date) {
            deadlineDate = ((java.sql.Date) deadline).toLocalDate();
        } else {
            deadlineDate = deadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        if (deadlineDate.isBefore(today)) {
            long daysLate = ChronoUnit.DAYS.between(deadlineDate, today);
            return  " Đã quá hạn " + daysLate + " ngày";
        } else if (deadlineDate.isEqual(today)) {
            return  "Hạn là hôm nay";
        } else {
            long daysLeft = ChronoUnit.DAYS.between(today, deadlineDate);
            return  "Còn " + daysLeft + " ngày";
        }
    }
    public String getDeadlineStyle(Date deadline) {
        if (deadline == null) return "color: black;";

        LocalDate today = LocalDate.now();
        LocalDate deadlineDate;

        if (deadline instanceof java.sql.Date) {
            deadlineDate = ((java.sql.Date) deadline).toLocalDate();
        } else {
            deadlineDate = deadline.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }

        if (deadlineDate.isBefore(today)) {
            return "color: red; font-weight: bold;";
        } else {
            return "color: black; font-weight: normal;";
        }
    }


    public List<Document> getDocuments() { return documents; }

    public boolean isShowDelegatePopup() { return showDelegatePopup; }

    public List<User> getUserList() { return userList; }

    public Document getSelectedDocument() { return selectedDocument; }

    public boolean isShowFilter() { return showFilter; }

    public String getFilterSoHieu() { return filterSoHieu; }

    public String getFilterTieuDe() { return filterTieuDe; }

    public String getFilterLoaiVB() { return filterLoaiVB; }

    public String getFilterTrangThai() { return filterTrangThai; }

    public String getUsername() { return username; }

    public DocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setSelectedDocument(Document selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public void setShowDelegatePopup(boolean showDelegatePopup) {
        this.showDelegatePopup = showDelegatePopup;
    }

    public void setShowFilter(boolean showFilter) {
        this.showFilter = showFilter;
    }

    public void setFilterSoHieu(String filterSoHieu) {
        this.filterSoHieu = filterSoHieu;
    }

    public void setFilterTieuDe(String filterTieuDe) {
        this.filterTieuDe = filterTieuDe;
    }

    public void setFilterLoaiVB(String filterLoaiVB) {
        this.filterLoaiVB = filterLoaiVB;
    }

    public void setFilterTrangThai(String filterTrangThai) {
        this.filterTrangThai = filterTrangThai;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getNgayBanHanhFrom() {
        return ngayBanHanhFrom;
    }

    public void setNgayBanHanhFrom(Date ngayBanHanhFrom) {
        this.ngayBanHanhFrom = ngayBanHanhFrom;
    }

    public Date getNgayBanHanhTo() {
        return ngayBanHanhTo;
    }

    public void setNgayBanHanhTo(Date ngayBanHanhTo) {
        this.ngayBanHanhTo = ngayBanHanhTo;
    }
}
