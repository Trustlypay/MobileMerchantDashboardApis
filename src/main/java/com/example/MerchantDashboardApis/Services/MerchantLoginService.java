package com.example.MerchantDashboardApis.Services;

import com.example.MerchantDashboardApis.Utils.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MerchantLoginService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Map<String, MerchantInfo> merchants = new HashMap<>();

    public MerchantLoginService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

        // --- Hardcoded merchant credentials ---
        merchants.put("alex@trustlypay.com",
                new MerchantInfo("1", passwordEncoder.encode("123456")));
        merchants.put("ghighpvtoffice@gmail.com",
                new MerchantInfo("2", passwordEncoder.encode("123456")));
        merchants.put("ipayserveu@gmail.com",
                new MerchantInfo("3", passwordEncoder.encode("123456")));
        merchants.put("candidplay@outlook.com",
                new MerchantInfo("4", passwordEncoder.encode("123456")));
        merchants.put("alisha@sainetworks.co.in",
                new MerchantInfo("5", passwordEncoder.encode("123456")));
        merchants.put("h8585982106@gmail.com",
                new MerchantInfo("6", passwordEncoder.encode("123456")));
        merchants.put("dpcenterprises@gmail.com",
                new MerchantInfo("7", passwordEncoder.encode("123456")));
        merchants.put("chuhai1002@outlook.com",
                new MerchantInfo("9", passwordEncoder.encode("123456")));
        merchants.put("simbaa.money@gmail.com",
                new MerchantInfo("10", passwordEncoder.encode("123456")));
        merchants.put("jpenterprise418@gmail.com",
                new MerchantInfo("11", passwordEncoder.encode("123456")));
        merchants.put("nanditha@trustlypay.com",
                new MerchantInfo("15", passwordEncoder.encode("123456")));
        merchants.put("kiran@trustlypay.com",
                new MerchantInfo("16", passwordEncoder.encode("123456")));
        merchants.put("Rushabh7799@gmail.com",
                new MerchantInfo("17", passwordEncoder.encode("123456")));
        merchants.put("hnzyz19@gmail.com",
                new MerchantInfo("19", passwordEncoder.encode("123456")));
        merchants.put("rahulkumar8527578@gmail.com",
                new MerchantInfo("22", passwordEncoder.encode("123456")));
        merchants.put("LOTINUMA8264532545@gmail.com",
                new MerchantInfo("23", passwordEncoder.encode("123456")));
        merchants.put("yanfenfeng3@gmail.com",
                new MerchantInfo("24", passwordEncoder.encode("123456")));
        merchants.put("hazesoft75635@gmail.com",
                new MerchantInfo("28", passwordEncoder.encode("123456")));
    }

    public Map<String, String> authenticate(String email, String rawPassword) {
        MerchantInfo merchant = merchants.get(email);
        if (merchant == null) {
            return null;
        }


        if (!passwordEncoder.matches(rawPassword, merchant.getPasswordHash())) {
            return null;
        }


        String token = jwtUtil.generateToken(email);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("merchantId", merchant.getMerchantId());
        return response;
    }


    private static class MerchantInfo {
        private final String merchantId;
        private final String passwordHash;

        public MerchantInfo(String merchantId, String passwordHash) {
            this.merchantId = merchantId;
            this.passwordHash = passwordHash;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public String getPasswordHash() {
            return passwordHash;
        }
    }
}
