package com.example.MerchantDashboardApis.Controllers;


import com.example.MerchantDashboardApis.Models.LoginRequest;
import com.example.MerchantDashboardApis.Services.MerchantLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Merchant Dashboard Login", description = "API for merchant dashboard login")
@RestController
@RequestMapping("/merchant/dashboard")
@CrossOrigin(origins = "*")
public class MerchantLoginController {

    private final MerchantLoginService merchantLoginService;

    public MerchantLoginController(MerchantLoginService merchantLoginService) {
        this.merchantLoginService = merchantLoginService;
    }

    @PostMapping("/login")
    @Operation(summary = "Merchant login", description = "Authenticate merchant and return token + merchantId")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Map<String, String> authResponse = merchantLoginService.authenticate(
                loginRequest.getUserName(),
                loginRequest.getPassword()
        );

        if (authResponse != null) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Login successful",
                    "token", authResponse.get("token"),
                    "merchantId", authResponse.get("merchantId")
            ));
        } else {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "success", false,
                            "message", "Invalid email or password"
                    ));
        }
    }
}