package com.example.MerchantDashboardApis.Services;



import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MerchantServicesServices {

    private final JdbcTemplate jdbcTemplate;

    public MerchantServicesServices(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Get payin/payout status for all merchants along with merchant name, payin vendor, payout vendor
     */
    public List<Map<String, Object>> getAllMerchantsPayinPayoutStatus() {
        String sql = """
                SELECT 
                    ms.merchant_id,
                    m.name AS merchantName,
                    ms.payin,
                    ms.payout,
                    vb.bank_name AS payinVendorName,
                    pvb.bank_name AS payoutVendorName
                FROM trustlypay_db.merchant_services ms
                JOIN trustlypay_db.merchant m ON ms.merchant_id = m.id
                LEFT JOIN trustlypay_db.merchant_vendor_bank mvb ON mvb.merchant_id = ms.merchant_id
                LEFT JOIN trustlypay_db.vendor_bank vb ON mvb.upi = vb.id
                LEFT JOIN trustlypay_db.merchant_payout_vendor mpv ON mpv.merchant_id = ms.merchant_id
                LEFT JOIN trustlypay_db.payout_vendor_bank pvb ON mpv.imps = pvb.id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("merchantId", rs.getInt("merchant_id"));
            map.put("merchantName", rs.getString("merchantName"));
            map.put("payinEnabled", rs.getInt("payin") == 1);
            map.put("payoutEnabled", rs.getInt("payout") == 1);
            map.put("payinVendorName", rs.getString("payinVendorName"));
            map.put("payoutVendorName", rs.getString("payoutVendorName"));
            return map;
        });
    }

    /**
     * Get payin/payout status for a specific merchant by merchantId
     */
    public List<Map<String, Object>> getMerchantPayinPayoutStatusById(int merchantId) {
        String sql = """
                SELECT 
                    ms.merchant_id,
                    m.name AS merchantName,
                    ms.payin,
                    ms.payout,
                    vb.bank_name AS payinVendorName,
                    pvb.bank_name AS payoutVendorName
                FROM trustlypay_db.merchant_services ms
                JOIN trustlypay_db.merchant m ON ms.merchant_id = m.id
                LEFT JOIN trustlypay_db.merchant_vendor_bank mvb ON mvb.merchant_id = ms.merchant_id
                LEFT JOIN trustlypay_db.vendor_bank vb ON mvb.upi = vb.id
                LEFT JOIN trustlypay_db.merchant_payout_vendor mpv ON mpv.merchant_id = ms.merchant_id
                LEFT JOIN trustlypay_db.payout_vendor_bank pvb ON mpv.imps = pvb.id
                WHERE ms.merchant_id = ?
                """;

        return jdbcTemplate.query(sql, new Object[]{merchantId}, (rs, rowNum) -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("merchantId", rs.getInt("merchant_id"));
            map.put("merchantName", rs.getString("merchantName"));
            map.put("payinEnabled", rs.getInt("payin") == 1);
            map.put("payoutEnabled", rs.getInt("payout") == 1);
            map.put("payinVendorName", rs.getString("payinVendorName"));
            map.put("payoutVendorName", rs.getString("payoutVendorName"));
            return map;
        });
    }
}
