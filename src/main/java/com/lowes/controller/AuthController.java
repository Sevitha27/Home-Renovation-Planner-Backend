package com.lowes.controller;

import com.lowes.dto.request.auth.AuthLoginDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.dto.request.auth.UpdateUserProfileDTO;
import com.lowes.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    //get Profile
    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfile(){
        return authService.getProfile();
    }

    //update profile
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_VENDOR','ROLE_CUSTOMER')")
    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@ModelAttribute UpdateUserProfileDTO dto) {
        return authService.updateProfile(dto);
    }
}