package com.example.MerchantDashboardApis.Controllers;

import com.example.MerchantDashboardApis.Models.MerchantData;
import com.example.MerchantDashboardApis.Models.MerchantDetails;
import com.example.MerchantDashboardApis.Services.MerchantDataService;
import com.example.MerchantDashboardApis.Services.MerchantDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant/dashboard/merchantsDetails")
@CrossOrigin(origins = "*")
public class MerchantDetailsController {

    private final MerchantDetailsService merchantDetailsService;

    @Autowired
    public MerchantDetailsController(MerchantDetailsService merchantDetailsService) {
        this.merchantDetailsService = merchantDetailsService;
    }

    @GetMapping("/merchantId")
    @Operation(summary = "Get Merchant Details")
    public ResponseEntity<Map<String, Object>> getMerchantsDetails(
            @Parameter(description = "Merchant ID", example = "1", required = true)
            @RequestParam Integer merchantId) {

        // Fetch merchant info
        MerchantDetails merchantDetails = merchantDetailsService.getMerchantById(merchantId);

        if (merchantDetails == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("merchant_gid", merchantDetails.getMerchantGid());
        response.put("name", merchantDetails.getName());
        response.put("email", merchantDetails.getEmail());

        return ResponseEntity.ok(response);
    }
}
