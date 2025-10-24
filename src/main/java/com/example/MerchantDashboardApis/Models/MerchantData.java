package com.example.MerchantDashboardApis.Models;

import java.math.BigDecimal;

public class MerchantData {

    private String merchantName;
    private String merchantGid;
    private String vendorBank;

    private long successCount;
    private long failedCount;
    private long pendingCount;
    private long totalCount;

    private BigDecimal successAmount;
    private BigDecimal failedAmount;
    private BigDecimal pendingAmount;
    private BigDecimal totalAmount;

    private double successPercentage;
    private double failedPercentage;
    private double pendingPercentage;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantGid() {
        return merchantGid;
    }

    public void setMerchantGid(String merchantGid) {
        this.merchantGid = merchantGid;
    }

    public String getVendorBank() {
        return vendorBank;
    }

    public void setVendorBank(String vendorBank) {
        this.vendorBank = vendorBank;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public BigDecimal getSuccessAmount() {
        return successAmount;
    }

    public void setSuccessAmount(BigDecimal successAmount) {
        this.successAmount = successAmount;
    }

    public BigDecimal getFailedAmount() {
        return failedAmount;
    }

    public void setFailedAmount(BigDecimal failedAmount) {
        this.failedAmount = failedAmount;
    }

    public BigDecimal getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(BigDecimal pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getSuccessPercentage() {
        return successPercentage;
    }

    public void setSuccessPercentage(double successPercentage) {
        this.successPercentage = successPercentage;
    }

    public double getFailedPercentage() {
        return failedPercentage;
    }

    public void setFailedPercentage(double failedPercentage) {
        this.failedPercentage = failedPercentage;
    }

    public double getPendingPercentage() {
        return pendingPercentage;
    }

    public void setPendingPercentage(double pendingPercentage) {
        this.pendingPercentage = pendingPercentage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public MerchantData(String merchantName, String merchantGid, String vendorBank, long successCount, long failedCount, long pendingCount, long totalCount, BigDecimal successAmount, BigDecimal failedAmount, BigDecimal pendingAmount, BigDecimal totalAmount, double successPercentage, double failedPercentage, double pendingPercentage) {
        this.merchantName = merchantName;
        this.merchantGid = merchantGid;
        this.vendorBank = vendorBank;
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.pendingCount = pendingCount;
        this.totalCount = totalCount;
        this.successAmount = successAmount;
        this.failedAmount = failedAmount;
        this.pendingAmount = pendingAmount;
        this.totalAmount = totalAmount;
        this.successPercentage = successPercentage;
        this.failedPercentage = failedPercentage;
        this.pendingPercentage = pendingPercentage;
    }

    public MerchantData() {
    }
}
