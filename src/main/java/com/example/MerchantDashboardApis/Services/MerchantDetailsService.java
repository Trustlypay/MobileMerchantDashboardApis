package com.example.MerchantDashboardApis.Services;
import com.example.MerchantDashboardApis.Models.MerchantData;
import com.example.MerchantDashboardApis.Models.MerchantDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MerchantDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public MerchantDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MerchantDetails getMerchantById(Integer merchantId) {
        try {
            String sql = "SELECT merchant_gid, name, email FROM merchant WHERE id = ?";
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new MerchantDetails(
                            rs.getString("merchant_gid"),
                            rs.getString("name"),
                            rs.getString("email")
                    ),
                    merchantId
            );
        } catch (Exception e) {
            return null;
        }
    }
}

