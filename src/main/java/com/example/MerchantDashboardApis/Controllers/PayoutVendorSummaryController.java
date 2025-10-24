package com.example.MerchantDashboardApis.Controllers;

import com.example.MerchantDashboardApis.Services.PayoutVendorSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant")
@CrossOrigin(origins = "*")
public class PayoutVendorSummaryController {

    private final PayoutVendorSummaryService payoutVendorSummaryService;

    public PayoutVendorSummaryController(PayoutVendorSummaryService payoutVendorSummaryService) {
        this.payoutVendorSummaryService = payoutVendorSummaryService;
    }

    @GetMapping("/payout-vendor-summary")
    @Operation(
            summary = "Get Payout Vendor Summary",
            description = "Returns success, failed, and pending transaction counts and amounts for each vendor under a given merchant within a date range."
    )
    public ResponseEntity<Map<String, Object>> getVendorSummary(
            @Parameter(description = "Merchant ID (required)", example = "1", required = true)
            @RequestParam String merchantId,

            @Parameter(description = "Start date (YYYY-MM-DD HH:mm:ss)", example = "2025-07-01 00:00:00", required = true)
            @RequestParam String fromDate,

            @Parameter(description = "End date (YYYY-MM-DD HH:mm:ss)", example = "2025-07-31 23:59:59", required = true)
            @RequestParam String toDate
    ) {
        return ResponseEntity.ok(payoutVendorSummaryService.getVendorSummary(merchantId, fromDate, toDate));
    }
}
