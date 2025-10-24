package com.example.MerchantDashboardApis.Services;

import com.example.MerchantDashboardApis.Models.PayinDetailedTxnsFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PayinDetailedTxnSummaryService {

    private final JdbcTemplate jdbcTemplate;

    public PayinDetailedTxnSummaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getPayinDetailedTransactionSummary(PayinDetailedTxnsFilter filterDto) {

        int pageNumber = filterDto.getPageNumber() != null ? filterDto.getPageNumber() : 1;
        int pageSize = filterDto.getPageSize() != null ? filterDto.getPageSize() : 10;
        int offset = (pageNumber - 1) * pageSize;

        // Allowed statuses
        List<String> allowedStatuses = Arrays.asList("success", "pending", "failed");

        // Use only allowed statuses if filter provided, otherwise return all
        List<String> statusFilter = (filterDto.getTransactionStatus() != null && !filterDto.getTransactionStatus().isEmpty()) ?
                filterDto.getTransactionStatus().stream()
                        .filter(allowedStatuses::contains)
                        .collect(Collectors.toList())
                : allowedStatuses;

        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Helper method to build query for a table
        buildQueryForTable(sql, params, "live_payment", filterDto, statusFilter);
        sql.append(" UNION ALL ");
        buildQueryForTable(sql, params, "live_payment_bkp", filterDto, statusFilter);

        // Count total
        String countSql = "SELECT COUNT(*) FROM (" + sql.toString() + ") AS count_table";
        int total = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);

        // Add pagination
        sql.append(" ORDER BY created_date DESC LIMIT ? OFFSET ? ");
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
                                    PayinDetailedTxnsFilter filterDto, List<String> statusFilter) {

//        sql.append("(")
//                .append("SELECT lp.transaction_date, lp.created_date, lp.transaction_username, ")
//                .append("lp.transaction_amount, lp.transaction_gid, lp.transaction_status, ")
//                .append("lp.bank_ref_no, lp.udf1, m.name AS merchant_name, vb.bank_name AS vendor_name ")
//                .append("FROM ").append(tableName).append(" lp ")
//                .append("LEFT JOIN merchant m ON lp.created_merchant = m.id ")
//                .append("LEFT JOIN payin_vendor_bank_name vbn ON lp.vendor_id = vbn.vendor_id ")
//                .append("LEFT JOIN vendor_bank vb ON lp.vendor_id = vb.id ")
//                .append("WHERE lp.created_merchant = ? ")
//        ;
//        params.add(filterDto.getMerchantId().get(0)); // single merchant

        sql.append("(")
                .append("SELECT lp.transaction_date, lp.created_date, lp.transaction_username, ")
                .append("lp.transaction_amount, lp.transaction_gid, lp.transaction_status, ")
                .append("lp.bank_ref_no, lp.udf1, m.name AS merchant_name, ")
                .append("vb.bank_name AS vendor_name, vbn.vendor_bank_name AS payin_vendor_bank_name ")
                .append("FROM ").append(tableName).append(" lp ")
                .append("LEFT JOIN merchant m ON lp.created_merchant = m.id ")
                .append("LEFT JOIN vendor_bank vb ON lp.vendor_id = vb.id ")
                .append("LEFT JOIN mb_payin_vendor_bank_name vbn ON lp.vendor_id = vbn.vendor_id ")
                .append("WHERE lp.created_merchant = ? ");
        params.add(filterDto.getMerchantId().get(0)); // single merchant

        sql.append("AND lp.created_date BETWEEN ? AND ? ");
        params.add(filterDto.getFromDate());
        params.add(filterDto.getToDate());

        if (filterDto.getTransactionId() != null && !filterDto.getTransactionId().isEmpty()) {
            sql.append("AND lp.transaction_gid LIKE ? ");
            params.add("%" + filterDto.getTransactionId() + "%");
        }

        if (filterDto.getUtr() != null && !filterDto.getUtr().isEmpty()) {
            sql.append("AND lp.bank_ref_no LIKE ? ");
            params.add("%" + filterDto.getUtr() + "%");
        }

        if (filterDto.getUdf1() != null && !filterDto.getUdf1().isEmpty()) {
            sql.append("AND lp.udf1 LIKE ? ");
            params.add("%" + filterDto.getUdf1() + "%");
        }

        if (!statusFilter.isEmpty()) {
            sql.append("AND lp.transaction_status IN (")
                    .append(statusFilter.stream().map(s -> "?").collect(Collectors.joining(",")))
                    .append(") ");
            params.addAll(statusFilter);
        }

        sql.append(")");
    }
}
