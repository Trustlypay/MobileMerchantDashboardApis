package com.example.MerchantDashboardApis.Services;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PayinVendorSummaryService {

    private final JdbcTemplate jdbcTemplate;

    public PayinVendorSummaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getVendorSummary(String merchantIdStr, String fromDate, String toDate) {
        // Parse merchantId safely
        Integer merchantId;
        try {
            merchantId = Integer.parseInt(merchantIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid merchantId: " + merchantIdStr);
        }

        // Fetch transactions from live_payment + live_payment_bkp
        String sql = """
                SELECT vendor_id, transaction_status, transaction_amount
                FROM live_payment
                WHERE created_merchant = ? AND created_date BETWEEN ? AND ?
                UNION ALL
                SELECT vendor_id, transaction_status, transaction_amount
                FROM live_payment_bkp
                WHERE created_merchant = ? AND created_date BETWEEN ? AND ?
                """;

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                sql,
                merchantId, fromDate, toDate,
                merchantId, fromDate, toDate
        );

        // Group by vendor_id
        Map<Integer, List<Map<String, Object>>> groupedByVendor = new HashMap<>();
        for (Map<String, Object> row : rows) {
            // vendor_id might be string, so parse safely
            Integer vendorId = null;
            Object vendorIdObj = row.get("vendor_id");
            if (vendorIdObj instanceof Number) {
                vendorId = ((Number) vendorIdObj).intValue();
            } else if (vendorIdObj instanceof String) {
                vendorId = Integer.parseInt((String) vendorIdObj);
            }

            groupedByVendor.computeIfAbsent(vendorId, k -> new ArrayList<>()).add(row);
        }

        List<Map<String, Object>> vendorSummaries = new ArrayList<>();

        for (Map.Entry<Integer, List<Map<String, Object>>> entry : groupedByVendor.entrySet()) {
            Integer vendorId = entry.getKey();
            List<Map<String, Object>> vendorTxns = entry.getValue();

            long successCount = vendorTxns.stream()
                    .filter(t -> "success".equalsIgnoreCase(String.valueOf(t.get("transaction_status"))))
                    .count();
            long failedCount = vendorTxns.stream()
                    .filter(t -> "failed".equalsIgnoreCase(String.valueOf(t.get("transaction_status"))))
                    .count();
            long pendingCount = vendorTxns.stream()
                    .filter(t -> "pending".equalsIgnoreCase(String.valueOf(t.get("transaction_status"))))
                    .count();

            long totalCount = successCount + failedCount + pendingCount;

            double successAmount = vendorTxns.stream()
                    .filter(t -> "success".equalsIgnoreCase(String.valueOf(t.get("transaction_status"))))
                    .mapToDouble(t -> Double.parseDouble(String.valueOf(t.get("transaction_amount"))))
                    .sum();

            double failedAmount = vendorTxns.stream()
                    .filter(t -> "failed".equalsIgnoreCase(String.valueOf(t.get("transaction_status"))))
                    .mapToDouble(t -> Double.parseDouble(String.valueOf(t.get("transaction_amount"))))
                    .sum();

            double pendingAmount = vendorTxns.stream()
                    .filter(t -> "pending".equalsIgnoreCase(String.valueOf(t.get("transaction_status"))))
                    .mapToDouble(t -> Double.parseDouble(String.valueOf(t.get("transaction_amount"))))
                    .sum();

            double totalAmount = successAmount + failedAmount + pendingAmount;

            double successPercentage = roundToTwoDecimals(totalCount > 0 ? (double) successCount / totalCount : 0.0 );
            double failedPercentage = roundToTwoDecimals(totalCount > 0 ? (double) failedCount / totalCount : 0.0);
            double pendingPercentage = roundToTwoDecimals(totalCount > 0 ? (double) pendingCount / totalCount : 0.0);

            // Fetch vendor name and bank name
            String vendorName = null;
            String bankName = null;

            try {
                vendorName = jdbcTemplate.queryForObject(
                        "SELECT bank_name FROM vendor_bank WHERE id = ?",
                        new Object[]{vendorId},
                        String.class
                );
            } catch (EmptyResultDataAccessException ignored) {}

            try {
                bankName = jdbcTemplate.queryForObject(
                        "SELECT vendor_bank_name FROM mb_payin_vendor_bank_name WHERE vendor_id = ?",
                        new Object[]{vendorId},
                        String.class
                );
            } catch (EmptyResultDataAccessException ignored) {}

            Map<String, Object> summary = new LinkedHashMap<>();
            summary.put("vendorId", vendorId);
            summary.put("vendorName", vendorName);
            summary.put("bankName", bankName);
            summary.put("successCount", successCount);
            summary.put("failedCount", failedCount);
            summary.put("pendingCount", pendingCount);
            summary.put("totalCount", totalCount);
            summary.put("successAmount", successAmount);
            summary.put("failedAmount", failedAmount);
            summary.put("pendingAmount", pendingAmount);
            summary.put("totalAmount", totalAmount);
            summary.put("successPercentage", successPercentage);
            summary.put("failedPercentage", failedPercentage);
            summary.put("pendingPercentage", pendingPercentage);

            vendorSummaries.add(summary);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("merchantId", merchantId);
        result.put("vendorSummaries", vendorSummaries);

        return result;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
