package com.example.zkproject.model;

public class WorkflowStep {
    private int id;
    private int stepOrder;
    private String stepName;
    private String role;
    private String workflowName;

    public WorkflowStep() {}

    public WorkflowStep(int stepOrder, String stepName, String role, String workflowName) {
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.role = role;
        this.workflowName = workflowName;
    }

    // Getters và Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
}
