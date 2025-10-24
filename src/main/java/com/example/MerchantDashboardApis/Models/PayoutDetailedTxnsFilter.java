package com.example.MerchantDashboardApis.Models;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class PayoutDetailedTxnsFilter {

    @Schema(description = "Start date (YYYY-MM-DD HH:MM:SS)", example = "2025-07-01 00:00:00", required = true)
    private String fromDate;

    @Schema(description = "End date (YYYY-MM-DD HH:MM:SS)", example = "2025-07-31 23:59:59", required = true)
    private String toDate;

    @Schema(description = "Page number", example = "1", required = true)
    private Integer pageNumber;

    @Schema(description = "Page size", example = "10", required = true)
    private Integer pageSize;

    @Schema(description = "Transaction ID (Alphanumeric)", example = "TXN12345")
    private String transactionId;

    @Schema(description = "UTR number", example = "1234567890")
    private String utr;

    @Schema(description = "UDF1 value (Alphanumeric)", example = "Order_001")
    private String udf1;

    @Schema(description = "Transaction Status. Allowed: success, pending, failed")
    private List<String> transactionStatus;

    @Schema(description = "Merchant IDs (single or multiple)")
    private Integer merchantId;

    // Getters and Setters
    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }

    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUtr() { return utr; }
    public void setUtr(String utr) { this.utr = utr; }

    public String getUdf1() { return udf1; }
    public void setUdf1(String udf1) { this.udf1 = udf1; }

    public List<String> getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(List<String> transactionStatus) { this.transactionStatus = transactionStatus; }

    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
}