package com.lowes.controller;

import com.lowes.dto.request.auth.AuthLoginDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    //register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRegisterDTO authRegisterDTO){
        return authService.register(authRegisterDTO);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginDTO authLoginDTO)
    {
        return authService.login(authLoginDTO);
    }

    //refresh access token
    @PostMapping("/refreshAccessToken")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response){
        return authService.refreshAccessToken(request, response);
    }

    //change password
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VENDOR','ROLE_CUSTOMER')")
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(){
        return null;
    }

    //delete account
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VENDOR','ROLE_CUSTOMER')")
    public ResponseEntity<?> deleteAccount(){
        return null;
    }

    // vendor role authorization testing
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    @GetMapping("/vendor-authz")
    public ResponseEntity<?> testingForVendorAuthorization(){
        return ResponseEntity.ok("Vendor Working");
    }

    // customer role authorization testing
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    @GetMapping("/customer-authz")
    public ResponseEntity<?> testingForCustomerAuthorization(){
        return ResponseEntity.ok("Customer Working");
    }

    // admin role authorization testing
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin-authz")
    public ResponseEntity<?> testingForAdminAuthorization(){
        return ResponseEntity.ok("Admin Working");
    }

}