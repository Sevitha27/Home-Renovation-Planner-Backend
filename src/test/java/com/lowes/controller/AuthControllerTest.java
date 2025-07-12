 package com.lowes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.dto.request.auth.AuthLoginDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.dto.request.auth.UpdateUserProfileDTO;
import com.lowes.dto.request.SkillRequestDTO;
import com.lowes.dto.response.auth.GetCustomerProfileDTO;
import com.lowes.dto.response.auth.GetVendorProfileDTO;
import com.lowes.dto.response.auth.UserResponseDTO;
import com.lowes.entity.enums.Role;
import com.lowes.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    // Register Tests
    @Test
    void testRegister_Customer_Success() throws Exception {
        // Given
        AuthRegisterDTO request = createCustomerRegisterDTO();
        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .email("customer@test.com")
            .role("CUSTOMER")
            .accessToken("jwt-token-123")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).register(any(AuthRegisterDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.email").value("customer@test.com"))
            .andExpect(jsonPath("$.role").value("CUSTOMER"))
            .andExpect(jsonPath("$.accessToken").value("jwt-token-123"));
    }

    @Test
    void testRegister_Vendor_Success() throws Exception {
        // Given
        AuthRegisterDTO request = createVendorRegisterDTO();
        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .email("vendor@test.com")
            .role("VENDOR")
            .accessToken("jwt-token-456")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).register(any(AuthRegisterDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.email").value("vendor@test.com"))
            .andExpect(jsonPath("$.role").value("VENDOR"))
            .andExpect(jsonPath("$.accessToken").value("jwt-token-456"));
    }

    @Test
    void testRegister_BadRequest() throws Exception {
        // Given
        AuthRegisterDTO request = createCustomerRegisterDTO();
        request.setEmail(""); // Invalid email

        doReturn(ResponseEntity.badRequest().body("Invalid email format"))
            .when(authService).register(any(AuthRegisterDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        // Given
        AuthRegisterDTO request = createCustomerRegisterDTO();

        doReturn(ResponseEntity.badRequest().body("Email already exists"))
            .when(authService).register(any(AuthRegisterDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    // Login Tests
    @Test
    void testLogin_Success() throws Exception {
        // Given
        AuthLoginDTO request = new AuthLoginDTO();
        request.setEmail("user@test.com");
        request.setPassword("password123");

        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .email("user@test.com")
            .role("CUSTOMER")
            .accessToken("jwt-token-789")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).login(any(AuthLoginDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.email").value("user@test.com"))
            .andExpect(jsonPath("$.role").value("CUSTOMER"))
            .andExpect(jsonPath("$.accessToken").value("jwt-token-789"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        AuthLoginDTO request = new AuthLoginDTO();
        request.setEmail("user@test.com");
        request.setPassword("wrongpassword");

        doReturn(ResponseEntity.badRequest().body("Invalid credentials"))
            .when(authService).login(any(AuthLoginDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Given
        AuthLoginDTO request = new AuthLoginDTO();
        request.setEmail("nonexistent@test.com");
        request.setPassword("password123");

        doReturn(ResponseEntity.notFound().build())
            .when(authService).login(any(AuthLoginDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    // Refresh Token Tests
    @Test
    void testRefreshAccessToken_Success() throws Exception {
        // Given
        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .accessToken("new-jwt-token-123")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).refreshAccessToken(any(), any());

        // When & Then
        mockMvc.perform(post("/auth/refreshAccessToken"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.accessToken").value("new-jwt-token-123"));
    }

    @Test
    void testRefreshAccessToken_InvalidToken() throws Exception {
        // Given
        doReturn(ResponseEntity.badRequest().body("Invalid refresh token"))
            .when(authService).refreshAccessToken(any(), any());

        // When & Then
        mockMvc.perform(post("/auth/refreshAccessToken"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testRefreshAccessToken_ExpiredToken() throws Exception {
        // Given
        doReturn(ResponseEntity.badRequest().body("Refresh token expired"))
            .when(authService).refreshAccessToken(any(), any());

        // When & Then
        mockMvc.perform(post("/auth/refreshAccessToken"))
            .andExpect(status().isBadRequest());
    }

    // Get Profile Tests
    @Test
    void testGetProfile_Customer_Success() throws Exception {
        // Given
        GetCustomerProfileDTO response = createCustomerProfileDTO();

        doReturn(ResponseEntity.ok().body(response)).when(authService).getProfile();

        // When & Then
        mockMvc.perform(get("/auth/getProfile"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("customer@test.com"))
            .andExpect(jsonPath("$.name").value("Test Customer"))
            .andExpect(jsonPath("$.contact").value("1234567890"));
    }

    @Test
    void testGetProfile_Vendor_Success() throws Exception {
        // Given
        GetVendorProfileDTO response = createVendorProfileDTO();

        doReturn(ResponseEntity.ok().body(response)).when(authService).getProfile();

        // When & Then
        mockMvc.perform(get("/auth/getProfile"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.email").value("vendor@test.com"))
            .andExpect(jsonPath("$.name").value("Test Vendor"))
            .andExpect(jsonPath("$.companyName").value("Test Company"))
            .andExpect(jsonPath("$.experience").value("5 years"));
    }

    @Test
    void testGetProfile_Unauthorized() throws Exception {
        // Given
        doReturn(ResponseEntity.status(401).body("Unauthorized"))
            .when(authService).getProfile();

        // When & Then
        mockMvc.perform(get("/auth/getProfile"))
            .andExpect(status().isUnauthorized());
    }

    // Update Profile Tests
    @Test
    void testUpdateProfile_Customer_Success() throws Exception {
        // Given
        UpdateUserProfileDTO request = UpdateUserProfileDTO.builder()
            .name("Updated Name")
            .contact("9876543210")
            .build();

        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).updateProfile(any(UpdateUserProfileDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/updateProfile")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "Updated Name")
                .param("contact", "9876543210"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testUpdateProfile_Vendor_Success() throws Exception {
        // Given
        UpdateUserProfileDTO request = UpdateUserProfileDTO.builder()
            .name("Updated Vendor")
            .companyName("Updated Company")
            .experience("10 years")
            .available(true)
            .build();

        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).updateProfile(any(UpdateUserProfileDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/updateProfile")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "Updated Vendor")
                .param("companyName", "Updated Company")
                .param("experience", "10 years")
                .param("available", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

    @Test
    void testUpdateProfile_WithImage_Success() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
            "profileImage", 
            "test-image.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        UserResponseDTO response = UserResponseDTO.builder()
            .message("SUCCESS")
            .url("https://example.com/image.jpg")
            .build();

        doReturn(ResponseEntity.ok().body(response)).when(authService).updateProfile(any(UpdateUserProfileDTO.class));

        // When & Then
        mockMvc.perform(multipart("/auth/updateProfile")
                .file(imageFile)
                .param("name", "Updated Name"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS"))
            .andExpect(jsonPath("$.url").value("https://example.com/image.jpg"));
    }

    @Test
    void testUpdateProfile_BadRequest() throws Exception {
        // Given
        doReturn(ResponseEntity.badRequest().body("Invalid data"))
            .when(authService).updateProfile(any(UpdateUserProfileDTO.class));

        // When & Then
        mockMvc.perform(post("/auth/updateProfile")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("name", "")) // Invalid name
            .andExpect(status().isBadRequest());
    }

    
    // Helper methods to create test DTOs
    private AuthRegisterDTO createCustomerRegisterDTO() {
        return AuthRegisterDTO.builder()
            .name("Test Customer")
            .email("customer@test.com")
            .password("password123")
            .role("CUSTOMER")
            .contact("1234567890")
            .companyName("") // Empty for customer
            .experience("") // Empty for customer
            .available(false) // Not applicable for customer
            .skills(Arrays.asList()) // Empty for customer
            .build();
    }

    private AuthRegisterDTO createVendorRegisterDTO() {
        SkillRequestDTO skill1 = SkillRequestDTO.builder()
            .skillName("Electrical")
            .basePrice(100.0)
            .build();
        
        SkillRequestDTO skill2 = SkillRequestDTO.builder()
            .skillName("Plumbing")
            .basePrice(150.0)
            .build();

        return AuthRegisterDTO.builder()
            .name("Test Vendor")
            .email("vendor@test.com")
            .password("password123")
            .role("VENDOR")
            .contact("1234567890")
            .companyName("Test Company")
            .experience("5 years")
            .available(true)
            .skills(Arrays.asList(skill1, skill2))
            .build();
    }

    private GetCustomerProfileDTO createCustomerProfileDTO() {
        GetCustomerProfileDTO dto = new GetCustomerProfileDTO();
        dto.setEmail("customer@test.com");
        dto.setName("Test Customer");
        dto.setContact("1234567890");
        return dto;
    }

    private GetVendorProfileDTO createVendorProfileDTO() {
        GetVendorProfileDTO dto = new GetVendorProfileDTO();
        dto.setEmail("vendor@test.com");
        dto.setName("Test Vendor");
        dto.setContact("1234567890");
        dto.setCompanyName("Test Company");
        dto.setExperience("5 years");
        dto.setAvailable(true);
        return dto;
    }
}