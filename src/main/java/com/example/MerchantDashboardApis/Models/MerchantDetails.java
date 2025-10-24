package com.example.MerchantDashboardApis.Models;

public class MerchantDetails {
    private String merchantGid;
    private String name;
    private String email;

    public MerchantDetails(String merchantGid, String name, String email) {
        this.merchantGid = merchantGid;
        this.name = name;
        this.email = email;
    }

    // getters and setters
    public String getMerchantGid() { return merchantGid; }
    public void setMerchantGid(String merchantGid) { this.merchantGid = merchantGid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
