package com.example.zkproject.viewmodel;

import com.example.zkproject.model.Document;
import com.example.zkproject.model.User;
import com.example.zkproject.repository.DocumentRepository;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;

import java.util.Date;

public class CreateDocumentViewModel {

    private Document document = new Document();
    private String uploadedFileName;
    private String username;

    private final DocumentRepository documentRepository = new DocumentRepository();

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public DocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }

    @Command
    @NotifyChange("document")
    public void submit() {
        boolean saved = documentRepository.save(document);

        if (saved) {
            Executions.getCurrent().getDesktop().getExecution()
                    .getAttributes().put("successMessage", "Lưu văn bản thành công!");
            goBack();
        } else {
            Clients.showNotification("Lưu văn bản thất bại!", Clients.NOTIFICATION_TYPE_ERROR, null, "top_center", 3000);
        }
    }

    @Command
    public void goBack() {
        BindUtils.postGlobalCommand(null, null, "goToListDocument", null);
    }

    @Command
    @NotifyChange("uploadedFileName")
    public void uploadFile(@ContextParam(ContextType.TRIGGER_EVENT) UploadEvent event) {
        Media media = event.getMedia();
        if (media != null) {
            uploadedFileName = media.getName();
            document.setTepDinhKem("/uploads/" + uploadedFileName);
        }
    }

    @Init
    public void init() {
        User user = (User) Executions.getCurrent().getSession().getAttribute("currentUser");

        if (user != null) {
            this.username = user.getUsername();
            document = new Document();
            document.setLoaiVanBan("Báo cáo");
            document.setNguoiSoanThao(username);
            document.setTrangThai(Document.TrangThai.CHO_XU_LY);
            document.setDeadline(new Date());
        } else {
            Executions.sendRedirect("index.zul");
        }
    }
}
