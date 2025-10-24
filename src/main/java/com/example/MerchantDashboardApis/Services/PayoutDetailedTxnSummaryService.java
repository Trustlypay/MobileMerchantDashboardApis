package com.example.MerchantDashboardApis.Services;

import com.example.MerchantDashboardApis.Models.PayoutDetailedTxnsFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PayoutDetailedTxnSummaryService {

    private final JdbcTemplate jdbcTemplate;

    public PayoutDetailedTxnSummaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getPayoutDetailedTransactionSummary(PayoutDetailedTxnsFilter filterDto) {

        int pageNumber = (filterDto.getPageNumber() != null) ? filterDto.getPageNumber() : 1;
        int pageSize = (filterDto.getPageSize() != null) ? filterDto.getPageSize() : 10;
        int offset = (pageNumber - 1) * pageSize;

        // Allowed statuses
        List<String> allowedStatuses = Arrays.asList("SUCCESS", "PENDING", "FAILED");

        // Use provided statuses or default to all
        List<String> statusFilter = (filterDto.getTransactionStatus() != null && !filterDto.getTransactionStatus().isEmpty())
                ? filterDto.getTransactionStatus().stream()
                .filter(allowedStatuses::contains)
                .collect(Collectors.toList())
                : allowedStatuses;

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Build query for main table
        buildQueryForTable(sql, params, "payout_transactions", filterDto, statusFilter);

        // Union with backup table
        sql.append(" UNION ALL ");
        buildQueryForTable(sql, params, "payout_transactions_bkp", filterDto, statusFilter);

        // Count total rows
        String countSql = "SELECT COUNT(*) FROM (" + sql.toString() + ") AS count_table";
        int total = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);

        // Add pagination
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add(offset);

        List<Map<String, Object>> items = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        result.put("pageNumber", pageNumber);
        result.put("pageSize", pageSize);
        result.put("items", items);

        return result;
    }

    private void buildQueryForTable(StringBuilder sql, List<Object> params, String tableName,
                                    PayoutDetailedTxnsFilter filterDto, List<String> statusFilter) {

        sql.append("(")
                .append("SELECT pt.created_at, pt.ben_name, pt.amount, pt.transfer_id, ")
                .append("pt.status, pt.udf1, m.name AS merchant_name,")
                .append("vb.bank_name AS vendor_name, vbn.vendor_bank_name AS payout_vendor_bank_name ")
                .append("FROM ").append(tableName).append(" pt ")
                .append("LEFT JOIN merchant m ON pt.merchant_id = m.id ")
                .append("LEFT JOIN payout_vendor_bank vb ON pt.vendor = vb.id ")
                .append("LEFT JOIN mb_payout_vendor_bank_name vbn ON pt.vendor = vbn.vendor_id ")
                .append("WHERE pt.merchant_id = ? ");
        params.add(filterDto.getMerchantId()); // single merchant

        sql.append("AND pt.created_at BETWEEN ? AND ? ");
        params.add(filterDto.getFromDate());
        params.add(filterDto.getToDate());

        if (filterDto.getTransactionId() != null && !filterDto.getTransactionId().isEmpty()) {
            sql.append("AND pt.transfer_id LIKE ? ");
            params.add("%" + filterDto.getTransactionId() + "%");
        }

        if (filterDto.getUtr() != null && !filterDto.getUtr().isEmpty()) {
            sql.append("AND pt.utr LIKE ? ");
            params.add("%" + filterDto.getUtr() + "%");
        }

        if (filterDto.getUdf1() != null && !filterDto.getUdf1().isEmpty()) {
            sql.append("AND pt.udf1 LIKE ? ");
            params.add("%" + filterDto.getUdf1() + "%");
        }

        if (!statusFilter.isEmpty()) {
            sql.append("AND pt.status IN (")
                    .append(statusFilter.stream().map(s -> "?").collect(Collectors.joining(",")))
                    .append(") ");
            params.addAll(statusFilter);
        }

        sql.append(")");
    }
}
