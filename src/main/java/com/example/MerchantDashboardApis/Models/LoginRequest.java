package com.example.MerchantDashboardApis.Models;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {


    private String userName;
    private String password;


    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
