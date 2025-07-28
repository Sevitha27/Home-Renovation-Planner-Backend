package com.lowes.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowes.Application;
import com.lowes.config.TestConfig;
import com.lowes.dto.request.auth.AuthLoginDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.entity.User;
import com.lowes.entity.enums.Role;
import com.lowes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
@Import(TestConfig.class)
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testRegisterUser() throws Exception {
        AuthRegisterDTO request = new AuthRegisterDTO();
        request.setEmail("test@example.com");
        request.setName("Test User");
        request.setPassword("password123");
        request.setRole("CUSTOMER");
        request.setContact("1234567890");
        request.setCompanyName("Test Company");
        request.setExperience("5 years");
        request.setAvailable(true);
        request.setSkills(List.of());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // Verify user was saved
        assertThat(userRepository.findByEmail("test@example.com")).isPresent();
    }

    @Test
    void testRegisterUserWithExistingEmail() throws Exception {
        // Create a user first
        User existingUser = createTestUser("test@example.com", "Existing User", Role.CUSTOMER);
        userRepository.save(existingUser);

        AuthRegisterDTO request = new AuthRegisterDTO();
        request.setEmail("test@example.com");
        request.setName("New User");
        request.setPassword("password123");
        request.setRole("CUSTOMER");
        request.setContact("1234567890");
        request.setCompanyName("Test Company");
        request.setExperience("5 years");
        request.setAvailable(true);
        request.setSkills(List.of());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User already exists with this email"));
    }

    @Test
    void testLoginUser() throws Exception {
        // Create a user first
        User user = createTestUser("test@example.com", "Test User", Role.CUSTOMER);
        user.setPassword(passwordEncoder.encode("password123")); // Encrypt the password
        userRepository.save(user);

        AuthLoginDTO request = new AuthLoginDTO();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testLoginUserWithInvalidCredentials() throws Exception {
        AuthLoginDTO request = new AuthLoginDTO();
        request.setEmail("nonexistent@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testLoginUserWithInvalidPassword() throws Exception {
        // Create a user first
        User user = createTestUser("test@example.com", "Test User", Role.CUSTOMER);
        user.setPassword(passwordEncoder.encode("correctpassword")); // Encrypt the password
        userRepository.save(user);

        AuthLoginDTO request = new AuthLoginDTO();
        request.setEmail("test@example.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Helper method to create test data
    private User createTestUser(String email, String name, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(role);
        user.setContact("1234567890");
        return user;
    }
} 