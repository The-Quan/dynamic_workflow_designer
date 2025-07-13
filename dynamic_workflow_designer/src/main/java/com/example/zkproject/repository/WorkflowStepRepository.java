package com.example.zkproject.repository;

import com.example.zkproject.model.WorkflowStep;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkflowStepRepository {

    public List<WorkflowStep> findAllByWorkflowNameOrderByStepOrder(String workflowName) {
        List<WorkflowStep> steps = new ArrayList<>();
        String sql = "SELECT * FROM workflow_step WHERE workflow_name = ? ORDER BY step_order ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, workflowName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                WorkflowStep step = new WorkflowStep();
                step.setId(rs.getInt("id"));
                step.setStepOrder(rs.getInt("step_order"));
                step.setStepName(rs.getString("step_name"));
                step.setRole(rs.getString("role"));
                step.setWorkflowName(rs.getString("workflow_name"));
                steps.add(step);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return steps;
    }

    public boolean deleteByWorkflowName(String workflowName) {
        String sql = "DELETE FROM workflow_step WHERE workflow_name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, workflowName);
            return ps.executeUpdate() >= 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean save(WorkflowStep workflowStep) {
        String sql = "INSERT INTO workflow_step(step_order, step_name, role, workflow_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workflowStep.getStepOrder());
            ps.setString(2, workflowStep.getStepName());
            ps.setString(3, workflowStep.getRole());
            ps.setString(4, workflowStep.getWorkflowName());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<WorkflowStep> findAll() {
        List<WorkflowStep> steps = new ArrayList<>();
        String sql = "SELECT * FROM workflow_step ORDER BY workflow_name ASC, step_order ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                WorkflowStep step = new WorkflowStep();
                step.setId(rs.getInt("id"));
                step.setStepOrder(rs.getInt("step_order"));
                step.setStepName(rs.getString("step_name"));
                step.setRole(rs.getString("role"));
                step.setWorkflowName(rs.getString("workflow_name"));
                steps.add(step);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return steps;
    }
    public boolean saveAll(List<WorkflowStep> steps) {
        if (steps == null || steps.isEmpty()) return false;

        String workflowName = steps.get(0).getWorkflowName();

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement psDelete = conn.prepareStatement(
                    "DELETE FROM workflow_step WHERE workflow_name = ?")) {
                psDelete.setString(1, workflowName);
                psDelete.executeUpdate();
            }

            try (PreparedStatement psInsert = conn.prepareStatement(
                    "INSERT INTO workflow_step(step_order, step_name, role, workflow_name) VALUES (?, ?, ?, ?)")) {

                for (WorkflowStep step : steps) {
                    psInsert.setInt(1, step.getStepOrder());
                    psInsert.setString(2, step.getStepName());
                    psInsert.setString(3, step.getRole());
                    psInsert.setString(4, step.getWorkflowName());
                    psInsert.addBatch();
                }

                psInsert.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
