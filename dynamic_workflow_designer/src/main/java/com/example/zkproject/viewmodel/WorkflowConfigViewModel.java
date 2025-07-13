package com.example.zkproject.viewmodel;

import com.example.zkproject.model.WorkflowStep;
import com.example.zkproject.repository.WorkflowStepRepository;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import java.util.Arrays;
import java.util.List;

public class WorkflowConfigViewModel {

    private ListModelList<WorkflowStep> steps = new ListModelList<>();
    private WorkflowStep selectedStep;
    private String workflowName = "Quy trình xử lý văn bản";
    private List<String> roles = Arrays.asList("NV", "TP", "GD", "None");
    private WorkflowStepRepository repository = new WorkflowStepRepository();

    @Init
    public void init() {
        List<WorkflowStep> loadedSteps = repository.findAllByWorkflowNameOrderByStepOrder(workflowName);
        if (loadedSteps.isEmpty()) {
            steps.add(new WorkflowStep(1, "Người khởi tạo", "NV", workflowName));
            steps.add(new WorkflowStep(2, "Trưởng phòng phê duyệt", "TP", workflowName));
            steps.add(new WorkflowStep(3, "Giám đốc phê duyệt", "GD", workflowName));
            steps.add(new WorkflowStep(4, "Hoàn thành", "None", workflowName));
        } else {
            steps.addAll(loadedSteps);
        }
    }

    @Command
    @NotifyChange("steps")
    public void addStep() {
        int newOrder = steps.size() + 1;
        steps.add(new WorkflowStep(newOrder, "Bước mới", "", workflowName));
        Clients.evalJavaScript("drawWorkflowLines()");
    }

    @Command
    @NotifyChange("steps")
    public void deleteStep(@BindingParam("step") WorkflowStep step) {
        steps.remove(step);
        updateStepOrder();
        Clients.evalJavaScript("drawWorkflowLines()");
    }

    @Command
    @NotifyChange("steps")
    public void reorder(@BindingParam("dragged") Listitem dragged, @BindingParam("dropped") Listitem dropped) {
        WorkflowStep draggedStep = dragged.getValue();
        WorkflowStep droppedStep = dropped.getValue();
        int fromIndex = steps.indexOf(draggedStep);
        int toIndex = steps.indexOf(droppedStep);
        if (fromIndex >= 0 && toIndex >= 0) {
            steps.remove(fromIndex);
            steps.add(toIndex, draggedStep);
            updateStepOrder();
        }
        Clients.evalJavaScript("drawWorkflowLines()");
    }

    private void updateStepOrder() {
        for (int i = 0; i < steps.size(); i++) {
            steps.get(i).setStepOrder(i + 1);
        }
    }

    @Command
    public void saveWorkflow() {
        boolean success = repository.saveAll(steps);
        if (success) {
            Messagebox.show("Lưu quy trình thành công!");
        } else {
            Messagebox.show("Lưu thất bại. Vui lòng kiểm tra lại dữ liệu.");
        }
    }

    public ListModelList<WorkflowStep> getSteps() { return steps; }
    public WorkflowStep getSelectedStep() { return selectedStep; }
    public void setSelectedStep(WorkflowStep selectedStep) { this.selectedStep = selectedStep; }
    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    public List<String> getRoles() { return roles; }
}
