package com.example.MerchantDashboardApis.Controllers;





import com.example.MerchantDashboardApis.Models.PayoutDetailedTxnsFilter;
import com.example.MerchantDashboardApis.Services.PayoutDetailedTxnSummaryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant")
@CrossOrigin(origins = "*")
public class PayoutDetailedTxnSummaryController {

    private final PayoutDetailedTxnSummaryService payoutDetailedTxnSummaryService;

    public PayoutDetailedTxnSummaryController(PayoutDetailedTxnSummaryService payoutDetailedTxnSummaryService) {
        this.payoutDetailedTxnSummaryService = payoutDetailedTxnSummaryService;
    }

    @GetMapping("/payout-detailed-summary")
    public ResponseEntity<Map<String, Object>> getPayoutDetailedSummary(
            @Parameter(description = "Start date (YYYY-MM-DD HH:mm:ss)", example = "2025-07-01 00:00:00", required = true)
            @RequestParam String fromDate,

            @Parameter(description = "End date (YYYY-MM-DD HH:mm:ss)", example = "2025-07-31 23:59:59", required = true)
            @RequestParam String toDate,

            @Parameter(description = "Page number", example = "1", required = true)
            @RequestParam Integer pageNumber,

            @Parameter(description = "Page size", example = "10", required = true)
            @RequestParam Integer pageSize,

            @Parameter(description = "Transaction ID (optional)", example = "TXN12345")
            @RequestParam(required = false) String transactionId,

            @Parameter(description = "UTR number (optional)", example = "1234567890")
            @RequestParam(required = false) String utr,

            @Parameter(description = "UDF1 value (optional)", example = "Order_001")
            @RequestParam(required = false) String udf1,

            @Parameter(schema = @Schema(type = "array", allowableValues = {"SUCCESS", "PENDING", "FAILED"}))
            @RequestParam(required = false) List<String> transactionStatus,
            @Parameter(description = "Merchant ID (required)", example = "1", required = true)
            @RequestParam Integer merchantId
    ) {

        PayoutDetailedTxnsFilter filter = new PayoutDetailedTxnsFilter();
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setPageNumber(pageNumber);
        filter.setPageSize(pageSize);
        filter.setTransactionId(transactionId);
        filter.setUtr(utr);
        filter.setUdf1(udf1);
        filter.setTransactionStatus(transactionStatus);
        filter.setMerchantId(merchantId); // single merchant

        return ResponseEntity.ok(payoutDetailedTxnSummaryService.getPayoutDetailedTransactionSummary(filter));
    }
}

