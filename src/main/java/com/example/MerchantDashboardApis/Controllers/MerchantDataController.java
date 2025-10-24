package com.example.MerchantDashboardApis.Controllers;

import com.example.MerchantDashboardApis.Models.MerchantData;
import com.example.MerchantDashboardApis.Services.MerchantDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant/dashboard/merchantsData")
@CrossOrigin(origins = "*")
public class MerchantDataController {

    private final MerchantDataService merchantDataService;

    @Autowired
    public MerchantDataController(MerchantDataService merchantDataService) {
        this.merchantDataService = merchantDataService;
    }

    // ---------------- Payin Data ----------------
    @GetMapping("/payin")
    @Operation(summary = "Get Payin Merchant Data")
    public ResponseEntity<Map<String, Object>> getPayinMerchantsData(
            @Parameter(description = "Start date (YYYY-MM-DD HH:MM:SS)", example = "2025-07-01 00:00:00", required = true)
            @RequestParam String fromDate,

            @Parameter(description = "End date (YYYY-MM-DD HH:MM:SS)", example = "2025-07-31 23:59:59", required = true)
            @RequestParam String toDate,

            @Parameter(description = "Merchant ID", example = "1", required = true)
            @RequestParam Integer merchantId) {

        List<MerchantData> summaries = merchantDataService.getPayinMerchantsData(fromDate, toDate, merchantId);

        Map<String, Object> response = new HashMap<>();
        response.put("payin", summaries);

        return ResponseEntity.ok(response);
    }

    // ---------------- Payout Data ----------------
    @GetMapping("/payout")
    @Operation(summary = "Get Payout Merchant Data")
    public ResponseEntity<Map<String, Object>> getPayoutMerchantsData(
            @Parameter(description = "Start date (YYYY-MM-DD HH:MM:SS)", example = "2025-07-01 00:00:00", required = true)
            @RequestParam String fromDate,

            @Parameter(description = "End date (YYYY-MM-DD HH:MM:SS)", example = "2025-07-31 23:59:59", required = true)
            @RequestParam String toDate,

            @Parameter(description = "Merchant ID", example = "1", required = true)
            @RequestParam Integer merchantId) {

        List<MerchantData> summaries = merchantDataService.getPayoutMerchantsData(fromDate, toDate, merchantId);

        Map<String, Object> response = new HashMap<>();
        response.put("payout", summaries);

        return ResponseEntity.ok(response);
    }
}
