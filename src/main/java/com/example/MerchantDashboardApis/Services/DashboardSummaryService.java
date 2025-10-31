package com.example.MerchantDashboardApis.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardSummaryService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardSummaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Map<String, Object>> getPayinDashboardSummary(String fromDate, String toDate, String merchantId) {

        if (merchantId == null || merchantId.isEmpty()) {
            throw new IllegalArgumentException("merchantId is required");
        }

        String sql = "SELECT transaction_status, SUM(transaction_amount) AS amount, COUNT(*) AS total " +
                "FROM ( " +
                "    SELECT transaction_status, transaction_amount, created_merchant, created_date FROM live_payment " +
                "    UNION ALL " +
                "    SELECT transaction_status, transaction_amount, created_merchant, created_date FROM live_payment_bkp " +
                ") AS combined " +
                "WHERE created_date BETWEEN ? AND ? AND created_merchant = ? " +
                "GROUP BY transaction_status";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, fromDate, toDate, merchantId);

        return calculateSummary(result);
    }


    public List<Map<String, Object>> getDashboardPayoutTransactionSummary(String fromDate, String toDate, String merchantId) {

        if (merchantId == null || merchantId.isEmpty()) {
            throw new IllegalArgumentException("merchantId is required");
        }

        String sql = "SELECT status, SUM(CAST(amount AS DECIMAL(18,2))) AS amount, COUNT(*) AS total " +
                "FROM ( " +
                "    SELECT status, amount, merchant_id, created_at FROM payout_transactions " +
                "    UNION ALL " +
                "    SELECT status, amount, merchant_id, created_at FROM payout_transactions_bkp " +
                ") AS combined " +
                "WHERE created_at BETWEEN ? AND ? AND merchant_id = ? " +
                "GROUP BY status";

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, fromDate, toDate, merchantId);

        return calculateSummary(result);
    }


    private List<Map<String, Object>> calculateSummary(List<Map<String, Object>> queryResult) {

        Map<String, Object> success = new HashMap<>(Map.of("status", "success", "totalAmount", 0.0, "totalCount", 0, "percentage", 0.0));
        Map<String, Object> failed  = new HashMap<>(Map.of("status", "failed", "totalAmount", 0.0, "totalCount", 0, "percentage", 0.0));
        Map<String, Object> pending = new HashMap<>(Map.of("status", "pending", "totalAmount", 0.0, "totalCount", 0, "percentage", 0.0));

        double totalAmount = 0;
        int totalCount = 0;

        for (Map<String, Object> row : queryResult) {
            String status = row.get("transaction_status") != null ? row.get("transaction_status").toString().toLowerCase()
                    : row.get("status") != null ? row.get("status").toString().toLowerCase() : "";

            double amount = row.get("amount") != null ? ((Number) row.get("amount")).doubleValue() : 0;
            int count = row.get("total") != null ? ((Number) row.get("total")).intValue() : 0;

            totalAmount += amount;
            totalCount += count;

            switch (status) {
                case "success" -> {
                    success.put("totalAmount", amount);
                    success.put("totalCount", count);
                }
                case "failed" -> {
                    failed.put("totalAmount", amount);
                    failed.put("totalCount", count);
                }
                case "pending" -> {
                    pending.put("totalAmount", amount);
                    pending.put("totalCount", count);
                }
            }
        }


        if (totalCount > 0) {
            success.put("percentage", roundToTwoDecimals((int) success.get("totalCount") / (double) totalCount) );
            failed.put("percentage", roundToTwoDecimals((int) failed.get("totalCount") / (double) totalCount) );
            pending.put("percentage", roundToTwoDecimals((int) pending.get("totalCount") / (double) totalCount) );
        }



        Map<String, Object> all = new HashMap<>(Map.of("status", "total volume", "totalAmount", totalAmount, "totalCount", totalCount));

        return List.of(failed, success, pending, all);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
