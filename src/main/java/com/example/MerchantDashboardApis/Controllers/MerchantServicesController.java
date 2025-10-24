package com.example.MerchantDashboardApis.Controllers;


import com.example.MerchantDashboardApis.Services.MerchantServicesServices;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class MerchantServicesController {

    private final MerchantServicesServices merchantServicesServices;

    public MerchantServicesController(MerchantServicesServices merchantServicesServices) {
        this.merchantServicesServices = merchantServicesServices;
    }

    @GetMapping("/merchants/status")
    @Operation(summary = "Get Payin/Payout status for merchants",
            description = "Returns merchantName, payinEnabled, and payoutEnabled flags.")
    public ResponseEntity<List<Map<String, Object>>> getMerchantsStatus(
            @RequestParam(required = true) Integer merchantId
    ) {
        List<Map<String, Object>> result;

        if (merchantId != null) {
            // Filter by merchantId
            result = merchantServicesServices.getMerchantPayinPayoutStatusById(merchantId);
        } else {
            // Return all merchants
            result = merchantServicesServices.getAllMerchantsPayinPayoutStatus();
        }

        return ResponseEntity.ok(result);
    }
}

