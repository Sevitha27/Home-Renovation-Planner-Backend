package com.lowes.service;

import com.lowes.dto.request.SkillRequestDTO;
import com.lowes.dto.request.auth.AuthLoginDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.dto.request.auth.UpdateUserProfileDTO;
import com.lowes.dto.response.auth.UserResponseDTO;
import com.lowes.dto.response.auth.GetCustomerProfileDTO;
import com.lowes.dto.response.auth.GetVendorProfileDTO;
import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.SkillType;
import com.lowes.mapper.UserConverter;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import com.lowes.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.TransactionStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private VendorRepository vendorRepository;
    @Mock
    private UserConverter userConverter;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private CloudinaryServiceImpl cloudinaryServiceImpl;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_userNotFound() {
        AuthLoginDTO dto = new AuthLoginDTO();
        dto.setEmail("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<?> response = authService.login(dto);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testLogin_success() {
        AuthLoginDTO dto = new AuthLoginDTO();
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded");
        user.setRole(Role.CUSTOMER);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        ResponseEntity<?> response = authService.login(dto);
        assertEquals(200, response.getStatusCodeValue());
    }

    // register
    @Test
    void testRegister_success_customer() {
        AuthRegisterDTO dto = new AuthRegisterDTO();
        dto.setEmail("test@example.com");
        User user = new User();
        user.setRole(Role.CUSTOMER);
        when(userConverter.authRegisterDTOtoUser(any(AuthRegisterDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        ResponseEntity<?> response = authService.register(dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testRegister_success_vendor() {
        AuthRegisterDTO dto = new AuthRegisterDTO();
        dto.setEmail("vendor@example.com");
        dto.setSkills(List.of(new SkillRequestDTO("PLUMBING", 100.0)));
        User user = new User();
        user.setRole(Role.VENDOR);
        when(userConverter.authRegisterDTOtoUser(any(AuthRegisterDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(skillRepository.findByNameAndBasePrice(any(SkillType.class), anyDouble())).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenReturn(new Skill());
        when(userConverter.authRegisterDTOtoVendor(any(AuthRegisterDTO.class), any(User.class), anyList())).thenReturn(new Vendor());
        when(vendorRepository.save(any(Vendor.class))).thenReturn(new Vendor());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh");
        ResponseEntity<?> response = authService.register(dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    void testRegister_userNotFoundAfterSave() {
        AuthRegisterDTO dto = new AuthRegisterDTO();
        dto.setEmail("test@example.com");
        User user = new User();
        user.setRole(Role.CUSTOMER);
        when(userConverter.authRegisterDTOtoUser(any(AuthRegisterDTO.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<?> response = authService.register(dto);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    @Test
    void testRegister_exception() {
        AuthRegisterDTO dto = new AuthRegisterDTO();
        dto.setEmail("test@example.com");
        try (MockedStatic<TransactionAspectSupport> tasMock = mockStatic(TransactionAspectSupport.class)) {
            TransactionStatus mockStatus = mock(TransactionStatus.class);
            tasMock.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(mockStatus);
            when(userConverter.authRegisterDTOtoUser(any(AuthRegisterDTO.class))).thenThrow(new RuntimeException());
            ResponseEntity<?> response = authService.register(dto);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }

    // refreshAccessToken
    @Test
    void testRefreshAccessToken_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie("refreshToken", "validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.isRefreshToken(anyString())).thenReturn(true);
        when(jwtService.extractEmail(anyString())).thenReturn("test@example.com");
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.CUSTOMER);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access");
        ResponseEntity<?> resp = authService.refreshAccessToken(request, response);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
    @Test
    void testRefreshAccessToken_invalidToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie("refreshToken", "invalidToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.validateToken(anyString())).thenReturn(false);
        when(jwtService.isRefreshToken(anyString())).thenReturn(false);
        ResponseEntity<?> resp = authService.refreshAccessToken(request, response);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    }
    @Test
    void testRefreshAccessToken_userNotFound() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        Cookie cookie = new Cookie("refreshToken", "validToken");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.validateToken(anyString())).thenReturn(true);
        when(jwtService.isRefreshToken(anyString())).thenReturn(true);
        when(jwtService.extractEmail(anyString())).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        ResponseEntity<?> resp = authService.refreshAccessToken(request, response);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }
    @Test
    void testRefreshAccessToken_exception() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getCookies()).thenThrow(new RuntimeException());
        ResponseEntity<?> resp = authService.refreshAccessToken(request, response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // updateProfile
    @Test
    void testUpdateProfile_success_noImage() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        User user = new User();
        mockSecurityContext(user);
        doNothing().when(userConverter).updateUserProfileDTOToUser(any(UpdateUserProfileDTO.class), any(User.class), isNull());
        ResponseEntity<?> resp = authService.updateProfile(dto);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
    @Test
    void testUpdateProfile_success_withImage() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        dto.setProfileImage(mockFile);
        User user = new User();
        mockSecurityContext(user);
        when(cloudinaryServiceImpl.uploadFile(any(MultipartFile.class), anyString())).thenReturn("url");
        doNothing().when(userConverter).updateUserProfileDTOToUser(any(UpdateUserProfileDTO.class), any(User.class), anyString());
        ResponseEntity<?> resp = authService.updateProfile(dto);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
    @Test
    void testUpdateProfile_imageUploadFails() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        dto.setProfileImage(mockFile);
        User user = new User();
        mockSecurityContext(user);
        when(cloudinaryServiceImpl.uploadFile(any(MultipartFile.class), anyString())).thenReturn(null);
        ResponseEntity<?> resp = authService.updateProfile(dto);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
    @Test
    void testUpdateProfile_exception() {
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        User user = new User();
        mockSecurityContext(user);
        doThrow(new RuntimeException()).when(userConverter).updateUserProfileDTOToUser(any(UpdateUserProfileDTO.class), any(User.class), isNull());
        ResponseEntity<?> resp = authService.updateProfile(dto);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // getProfile
    @Test
    void testGetProfile_customer() {
        User user = new User();
        user.setRole(Role.CUSTOMER);
        mockSecurityContext(user);
        when(userConverter.userToGetCustomerProfileDTO(any(User.class))).thenReturn(new GetCustomerProfileDTO());
        ResponseEntity<?> resp = authService.getProfile();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
    @Test
    void testGetProfile_vendor() {
        User user = new User();
        user.setRole(Role.VENDOR);
        mockSecurityContext(user);
        when(userConverter.userToGetVendorProfileDTO(any(User.class))).thenReturn(new GetVendorProfileDTO());
        ResponseEntity<?> resp = authService.getProfile();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
    @Test
    void testGetProfile_otherRole() {
        User user = new User();
        user.setRole(null);
        mockSecurityContext(user);
        ResponseEntity<?> resp = authService.getProfile();
        assertNull(resp);
    }
    @Test
    void testGetProfile_exception() {
        mockSecurityContext(null);
        ResponseEntity<?> resp = authService.getProfile();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }

    // Helper to mock SecurityContext
    private void mockSecurityContext(User user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
