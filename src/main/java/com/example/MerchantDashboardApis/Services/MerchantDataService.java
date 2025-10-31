package com.example.MerchantDashboardApis.Services;

import com.example.MerchantDashboardApis.Models.MerchantData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class MerchantDataService {

    private final JdbcTemplate jdbcTemplate;

    public MerchantDataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ---------------- Payin Data ----------------
    public List<MerchantData> getPayinMerchantsData(String fromDate, String toDate, int merchantId) {
        String sql = """
            SELECT
                m.name AS merchant_name,
                m.merchant_gid,
                vb.bank_name AS vendor_bank,
                pvb.vendor_bank_name,
                SUM(CASE WHEN t.transaction_status = 'success' THEN 1 ELSE 0 END) AS success_count,
                SUM(CASE WHEN t.transaction_status = 'failed' THEN 1 ELSE 0 END) AS failed_count,
                SUM(CASE WHEN t.transaction_status = 'pending' THEN 1 ELSE 0 END) AS pending_count,
                COALESCE(SUM(CASE WHEN t.transaction_status = 'success' THEN t.transaction_amount ELSE 0 END),0) AS success_amount,
                COALESCE(SUM(CASE WHEN t.transaction_status = 'failed' THEN t.transaction_amount ELSE 0 END),0) AS failed_amount,
                COALESCE(SUM(CASE WHEN t.transaction_status = 'pending' THEN t.transaction_amount ELSE 0 END),0) AS pending_amount
            FROM merchant m
            LEFT JOIN (
                SELECT lp.created_merchant, lp.transaction_status, lp.transaction_amount, lp.vendor_id
                FROM live_payment lp
                WHERE lp.created_date BETWEEN ? AND ?
                  AND lp.created_merchant = ?
                  AND lp.vendor_id IS NOT NULL AND lp.vendor_id != 0
                UNION ALL
                SELECT lpb.created_merchant, lpb.transaction_status, lpb.transaction_amount, lpb.vendor_id
                FROM live_payment_bkp lpb
                WHERE lpb.created_date BETWEEN ? AND ?
                  AND lpb.created_merchant = ?
                  AND lpb.vendor_id IS NOT NULL AND lpb.vendor_id != 0
            ) t ON m.id = t.created_merchant
            LEFT JOIN vendor_bank vb ON t.vendor_id = vb.id
            LEFT JOIN mb_payin_vendor_bank_name pvb ON t.vendor_id = pvb.vendor_id
            WHERE m.id = ?
            GROUP BY m.id, m.name, m.merchant_gid, vb.bank_name
            ORDER BY m.name ASC
        """;

        Object[] params = new Object[]{fromDate, toDate, merchantId, fromDate, toDate, merchantId, merchantId};

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapRowToMerchantData(rs));
    }

    // ---------------- Payout Data ----------------
    public List<MerchantData> getPayoutMerchantsData(String fromDate, String toDate, int merchantId) {
        String sql = """
            SELECT
                m.name AS merchant_name,
                m.merchant_gid,
                vb.bank_name AS vendor_bank,
                pvb.vendor_bank_name,
                SUM(CASE WHEN pt.status = 'success' THEN 1 ELSE 0 END) AS success_count,
                SUM(CASE WHEN pt.status = 'failed' THEN 1 ELSE 0 END) AS failed_count,
                SUM(CASE WHEN pt.status = 'pending' THEN 1 ELSE 0 END) AS pending_count,
                COALESCE(SUM(CASE WHEN pt.status = 'success' THEN pt.amount ELSE 0 END),0) AS success_amount,
                COALESCE(SUM(CASE WHEN pt.status = 'failed' THEN pt.amount ELSE 0 END),0) AS failed_amount,
                COALESCE(SUM(CASE WHEN pt.status = 'pending' THEN pt.amount ELSE 0 END),0) AS pending_amount
            FROM merchant m
            LEFT JOIN (
                SELECT * 
                FROM payout_transactions
                WHERE created_at BETWEEN ? AND ? 
                  AND merchant_id = ?
                  AND vendor IS NOT NULL AND vendor != 0
                UNION ALL
                SELECT *
                FROM payout_transactions_bkp
                WHERE created_at BETWEEN ? AND ? 
                  AND merchant_id = ?
                  AND vendor IS NOT NULL AND vendor != 0
            ) pt ON m.id = pt.merchant_id
            LEFT JOIN payout_vendor_bank vb ON pt.vendor = vb.id
            LEFT JOIN mb_payout_vendor_bank_name pvb ON pt.vendor = pvb.vendor_id
            WHERE m.id = ?
            GROUP BY m.id, m.name, m.merchant_gid, vb.bank_name, pvb.vendor_bank_name
            ORDER BY m.name ASC
        """;

        Object[] params = new Object[]{fromDate, toDate, merchantId, fromDate, toDate, merchantId, merchantId};

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> mapRowToMerchantData(rs));
    }

    // ---------------- Row Mapper ----------------
    private MerchantData mapRowToMerchantData(java.sql.ResultSet rs) throws java.sql.SQLException {
        long successCount = rs.getLong("success_count");
        long failedCount = rs.getLong("failed_count");
        long pendingCount = rs.getLong("pending_count");

        BigDecimal successAmount = rs.getBigDecimal("success_amount");
        BigDecimal failedAmount = rs.getBigDecimal("failed_amount");
        BigDecimal pendingAmount = rs.getBigDecimal("pending_amount");

        long totalCount = successCount + failedCount + pendingCount;
        BigDecimal totalAmount = successAmount.add(failedAmount).add(pendingAmount);

        double successPct = roundToTwoDecimals(totalCount > 0 ? (double) successCount / totalCount : 0 );
        double failedPct = roundToTwoDecimals(totalCount > 0 ? (double) failedCount / totalCount : 0 );
        double pendingPct = roundToTwoDecimals(totalCount > 0 ? (double) pendingCount / totalCount : 0 );

        MerchantData summary = new MerchantData();
        summary.setMerchantName(rs.getString("merchant_name"));
        summary.setMerchantGid(rs.getString("merchant_gid"));
        summary.setVendorBank(rs.getString("vendor_bank"));
        summary.setBankName(rs.getString("vendor_bank_name"));
        summary.setSuccessCount(successCount);
        summary.setFailedCount(failedCount);
        summary.setPendingCount(pendingCount);
        summary.setTotalCount(totalCount);
        summary.setSuccessAmount(successAmount);
        summary.setFailedAmount(failedAmount);
        summary.setPendingAmount(pendingAmount);
        summary.setTotalAmount(totalAmount);
        summary.setSuccessPercentage(successPct);
        summary.setFailedPercentage(failedPct);
        summary.setPendingPercentage(pendingPct);

        return summary;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
