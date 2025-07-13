package com.example.zkproject.viewmodel;

import com.example.zkproject.model.Document;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;

import java.util.ArrayList;
import java.util.List;

public class DocumentDetailViewModel {

    private Document document;


    @Init
    @NotifyChange({"document", "historyList"})
    public void init() {
        document = (Document) Executions.getCurrent().getSession().getAttribute("selectedDocument");

    }

    public Document getDocument() {
        return document;
    }


    @Command
    public void goBack() {
        BindUtils.postGlobalCommand(null, null, "goToListDocument", null);
    }
}
