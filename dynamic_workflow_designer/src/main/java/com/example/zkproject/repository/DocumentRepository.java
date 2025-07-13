package com.example.zkproject.repository;

import com.example.zkproject.model.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentRepository {

    public boolean save(Document doc) {
        String sql = "INSERT INTO documents(" +
                "so_hieu, tieu_de, loai_van_ban, nguoi_soan_thao, ngay_ban_hanh, tep_dinh_kem, trang_thai, deadline) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, doc.getSoHieu());
            ps.setString(2, doc.getTieuDe());
            ps.setString(3, doc.getLoaiVanBan());
            ps.setString(4, doc.getNguoiSoanThao());
            ps.setDate(5, doc.getNgayBanHanh() != null ? new java.sql.Date(doc.getNgayBanHanh().getTime()) : null);
            ps.setString(6, doc.getTepDinhKem());
            ps.setString(7, String.valueOf(doc.getTrangThai()));
            ps.setDate(8, doc.getDeadline() != null ? new java.sql.Date(doc.getDeadline().getTime()) : null);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Document> findAll() {
        String sql = "SELECT * FROM documents ORDER BY deadline DESC";
        List<Document> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Document doc = new Document();
                doc.setId(rs.getLong("id"));
                doc.setSoHieu(rs.getString("so_hieu"));
                doc.setTieuDe(rs.getString("tieu_de"));
                doc.setLoaiVanBan(rs.getString("loai_van_ban"));
                doc.setNguoiSoanThao(rs.getString("nguoi_soan_thao"));
                doc.setNgayBanHanh(rs.getDate("ngay_ban_hanh"));
                doc.setTepDinhKem(rs.getString("tep_dinh_kem"));
                doc.setTrangThai(Document.TrangThai.valueOf(rs.getString("trang_thai")));

                Date deadline = rs.getDate("deadline");
                if (deadline != null) {
                    doc.setDeadline(deadline);
                }

                result.add(doc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<Document> searchWithFilters(String soHieu, String tieuDe, String loaiVB, String trangThai, String deadline, String ngayBanHanh) {
        List<Document> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM documents WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (soHieu != null && !soHieu.trim().isEmpty()) {
            sql.append(" AND LOWER(so_hieu) LIKE ?");
            params.add("%" + soHieu.toLowerCase() + "%");
        }
        if (tieuDe != null && !tieuDe.trim().isEmpty()) {
            sql.append(" AND LOWER(tieu_de) LIKE ?");
            params.add("%" + tieuDe.toLowerCase() + "%");
        }
        if (loaiVB != null && !loaiVB.trim().isEmpty()) {
            sql.append(" AND LOWER(loai_van_ban) LIKE ?");
            params.add("%" + loaiVB.toLowerCase() + "%");
        }
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            sql.append(" AND LOWER(trang_thai) LIKE ?");
            params.add("%" + trangThai.toLowerCase() + "%");
        }
        if (deadline != null && !deadline.trim().isEmpty()) {
            sql.append(" AND DATE(deadline) = ?");
            params.add(Date.valueOf(deadline));
        }
        if (ngayBanHanh != null && ngayBanHanh.contains(";")) {
            String[] parts = ngayBanHanh.split(";");
            if (parts.length == 2) {
                sql.append(" AND DATE(ngay_ban_hanh) BETWEEN ? AND ?");
                params.add(Date.valueOf(parts[0]));
                params.add(Date.valueOf(parts[1]));
            }
        }


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Document doc = new Document();
                doc.setId(rs.getLong("id"));
                doc.setSoHieu(rs.getString("so_hieu"));
                doc.setTieuDe(rs.getString("tieu_de"));
                doc.setLoaiVanBan(rs.getString("loai_van_ban"));
                doc.setNguoiSoanThao(rs.getString("nguoi_soan_thao"));
                doc.setNgayBanHanh(rs.getDate("ngay_ban_hanh"));
                doc.setTepDinhKem(rs.getString("tep_dinh_kem"));
                doc.setTrangThai(Document.TrangThai.valueOf(rs.getString("trang_thai")));
                doc.setDeadline(rs.getDate("deadline"));
                results.add(doc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public boolean updateTrangThai(Long id, Document.TrangThai trangThai) {
        String sql = "UPDATE documents SET trang_thai = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.valueOf(trangThai));
            stmt.setLong(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Map<String, Map<String, Integer>> countByTypeAndTrangThai() {
        String sql = "SELECT loai_van_ban, trang_thai, deadline FROM documents";

        Map<String, Map<String, Integer>> result = new HashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while (rs.next()) {
                String loaiVanBan = rs.getString("loai_van_ban");
                String trangThai = rs.getString("trang_thai");
                Date deadline = rs.getDate("deadline");

                result.computeIfAbsent(loaiVanBan, k -> new HashMap<>())
                        .merge(trangThai, 1, Integer::sum);

                if ((trangThai.equals("CHO_XU_LY") || trangThai.equals("DANG_XU_LY")) &&
                        deadline != null && deadline.before(today)) {

                    result.get(loaiVanBan)
                            .merge("QUA_HAN", 1, Integer::sum);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }


}
