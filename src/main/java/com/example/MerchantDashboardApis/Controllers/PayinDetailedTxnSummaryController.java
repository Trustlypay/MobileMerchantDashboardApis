package com.example.MerchantDashboardApis.Controllers;

import com.example.MerchantDashboardApis.Models.PayinDetailedTxnsFilter;
import com.example.MerchantDashboardApis.Services.PayinDetailedTxnSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant")
@CrossOrigin(origins = "*")
public class PayinDetailedTxnSummaryController {

    private final PayinDetailedTxnSummaryService payinDetailedTxnSummaryService;

    public PayinDetailedTxnSummaryController(PayinDetailedTxnSummaryService payinDetailedTxnSummaryService) {
        this.payinDetailedTxnSummaryService = payinDetailedTxnSummaryService;
    }

    @GetMapping("/payin-detailed-summary")
    @Operation(summary = "Get detailed payin transactions for a specific merchant",
            description = "Returns detailed payin transactions filtered by date range, transaction ID, UTR, UDF1, transaction status, and merchant ID with pagination.")
    public ResponseEntity<Map<String, Object>> getPayinDetailedSummary(
            @Parameter(description = "Start date (YYYY-MM-DD HH:mm:ss)", example = "2025-07-01 00:00:00", required = true)
            @RequestParam String fromDate,

            @Parameter(description = "End date (YYYY-MM-DD HH:mm:ss)", example = "2025-07-31 23:59:59", required = true)
            @RequestParam String toDate,

            @Parameter(description = "Page number for pagination", example = "1", required = true)
            @RequestParam Integer pageNumber,

            @Parameter(description = "Page size for pagination", example = "10", required = true)
            @RequestParam Integer pageSize,

            @Parameter(description = "Transaction ID (Alphanumeric)", example = "TXN12345", required = false)
            @RequestParam(required = false) String transactionId,

            @Parameter(description = "UTR number", example = "1234567890", required = false)
            @RequestParam(required = false) String utr,

            @Parameter(description = "UDF1 value (Alphanumeric)", example = "Order_001", required = false)
            @RequestParam(required = false) String udf1,

            @Parameter(
                    description = "Transaction Status (single or multiple: success, pending, failed)",
                    example = "success,pending,failed",
                    schema = @Schema(type = "array", allowableValues = {"success","pending","failed"})
            )
            @RequestParam(required = false) List<String> transactionStatus,

            @Parameter(description = "Merchant ID (required)", example = "1", required = true)
            @RequestParam Long merchantId
    ) {

        PayinDetailedTxnsFilter filter = new PayinDetailedTxnsFilter();
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setPageNumber(pageNumber);
        filter.setPageSize(pageSize);
        filter.setTransactionId(transactionId);
        filter.setUtr(utr);
        filter.setUdf1(udf1);
        filter.setTransactionStatus(transactionStatus);
        filter.setMerchantId(List.of(merchantId)); // wrap single merchantId in a list

        return ResponseEntity.ok(payinDetailedTxnSummaryService.getPayinDetailedTransactionSummary(filter));
    }
}
