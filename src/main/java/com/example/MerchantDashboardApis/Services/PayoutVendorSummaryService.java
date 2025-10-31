//package com.example.MerchantDashboardApis.Services;
//
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class PayoutVendorSummaryService {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public PayoutVendorSummaryService(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public Map<String, Object> getVendorSummary(String merchantIdStr, String fromDate, String toDate) {
//
//        // Validate merchantId input
//        if (merchantIdStr == null || merchantIdStr.isBlank()) {
//            throw new IllegalArgumentException("merchantId cannot be null or empty");
//        }
//
//        Integer merchantId;
//        try {
//            merchantId = Integer.parseInt(merchantIdStr);
//        } catch (NumberFormatException e) {
//            throw new IllegalArgumentException("Invalid merchantId: " + merchantIdStr);
//        }
//
//        // Fetch transactions from both tables
//        String sql = """
//                SELECT vendor, status, amount
//                FROM payout_transactions
//                WHERE merchant_id = ? AND created_at BETWEEN ? AND ?
//                UNION ALL
//                SELECT vendor, status, amount
//                FROM payout_transactions_bkp
//                WHERE merchant_id = ? AND created_at BETWEEN ? AND ?
//                """;
//
//        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
//                sql,
//                merchantId, fromDate, toDate,
//                merchantId, fromDate, toDate
//        );
//
//        // Group by vendor (safe parsing)
//        Map<Integer, List<Map<String, Object>>> groupedByVendor = new HashMap<>();
//        for (Map<String, Object> row : rows) {
//            Object vendorObj = row.get("vendor");
//            if (vendorObj == null) continue; // skip null
//            String vendorStr = vendorObj.toString().trim();
//            if (vendorStr.isEmpty()) continue; // skip empty string
//
//            Integer vendorId;
//            try {
//                vendorId = Integer.parseInt(vendorStr);
//            } catch (NumberFormatException e) {
//                continue; // skip invalid vendor IDs
//            }
//
//            groupedByVendor.computeIfAbsent(vendorId, k -> new ArrayList<>()).add(row);
//        }
//
//        List<Map<String, Object>> vendorSummaries = new ArrayList<>();
//
//        for (Map.Entry<Integer, List<Map<String, Object>>> entry : groupedByVendor.entrySet()) {
//            Integer vendorId = entry.getKey();
//            List<Map<String, Object>> vendorTxns = entry.getValue();
//
//            long successCount = vendorTxns.stream()
//                    .filter(t -> "success".equalsIgnoreCase(String.valueOf(t.get("status"))))
//                    .count();
//            long failedCount = vendorTxns.stream()
//                    .filter(t -> "failed".equalsIgnoreCase(String.valueOf(t.get("status"))))
//                    .count();
//            long pendingCount = vendorTxns.stream()
//                    .filter(t -> "pending".equalsIgnoreCase(String.valueOf(t.get("status"))))
//                    .count();
//
//            long totalCount = successCount + failedCount + pendingCount;
//
//            double successAmount = vendorTxns.stream()
//                    .filter(t -> "success".equalsIgnoreCase(String.valueOf(t.get("status"))))
//                    .mapToDouble(t -> Double.parseDouble(String.valueOf(t.get("amount"))))
//                    .sum();
//
//            double failedAmount = vendorTxns.stream()
//                    .filter(t -> "failed".equalsIgnoreCase(String.valueOf(t.get("status"))))
//                    .mapToDouble(t -> Double.parseDouble(String.valueOf(t.get("amount"))))
//                    .sum();
//
//            double pendingAmount = vendorTxns.stream()
//                    .filter(t -> "pending".equalsIgnoreCase(String.valueOf(t.get("status"))))
//                    .mapToDouble(t -> Double.parseDouble(String.valueOf(t.get("amount"))))
//                    .sum();
//
//            double totalAmount = successAmount + failedAmount + pendingAmount;
//
//            double successPercentage =roundToTwoDecimals( totalCount > 0 ? (double) successCount / totalCount : 0.0 );
//            double failedPercentage = roundToTwoDecimals(totalCount > 0 ? (double) failedCount / totalCount : 0.0);
//            double pendingPercentage = roundToTwoDecimals(totalCount > 0 ? (double) pendingCount / totalCount : 0.0);
//
//            // Vendor details
//            String vendorName = null;
//            try {
//                vendorName = jdbcTemplate.queryForObject(
//                        "SELECT bank_name FROM payout_vendor_bank WHERE id = ?",
//                        new Object[]{vendorId},
//                        String.class
//                );
//            } catch (EmptyResultDataAccessException ignored) {}
//
//            String bankName = null;
//            try {
//                bankName = jdbcTemplate.queryForObject(
//                        "SELECT vendor_bank_name FROM mb_payout_vendor_bank_name WHERE vendor_id = ?",
//                        new Object[]{vendorId},
//                        String.class
//                );
//            } catch (EmptyResultDataAccessException ignored) {}
//
//            Map<String, Object> summary = new LinkedHashMap<>();
//            summary.put("vendorId", vendorId);
//            summary.put("vendorName", vendorName);
//            summary.put("bankName", bankName);
//            summary.put("successCount", successCount);
//            summary.put("failedCount", failedCount);
//            summary.put("pendingCount", pendingCount);
//            summary.put("totalCount", totalCount);
//            summary.put("successAmount", successAmount);
//            summary.put("failedAmount", failedAmount);
//            summary.put("pendingAmount", pendingAmount);
//            summary.put("totalAmount", totalAmount);
//            summary.put("successPercentage", successPercentage);
//            summary.put("failedPercentage", failedPercentage);
//            summary.put("pendingPercentage", pendingPercentage);
//
//            vendorSummaries.add(summary);
//        }
//
//        Map<String, Object> result = new LinkedHashMap<>();
//        result.put("merchantId", merchantId);
//        result.put("vendorSummaries", vendorSummaries);
//
//        return result;
//    }
//
//
//    private double roundToTwoDecimals(double value) {
//        return Math.round(value * 100.0) / 100.0;
//    }
//}
