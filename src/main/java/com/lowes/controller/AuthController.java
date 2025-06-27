package com.lowes.controller;

import com.lowes.dto.request.AuthLoginDTO;
import com.lowes.dto.request.AuthRegisterDTO;
import com.lowes.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    //register
    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute AuthRegisterDTO authRegisterDTO){

        return authService.register(authRegisterDTO);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginDTO authLoginDTO)
    {
        return null;
    }

    //refresh access token
    @PostMapping("/refreshAccessToken")
    public ResponseEntity<?> refreshAccessToken(){
        return null;
    }

    //change password
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(){
        return null;
    }

    //delete account
    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(){
        return null;
    }

}
