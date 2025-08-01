package com.lowes.controller;

import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import com.lowes.service.MaterialService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MaterialControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MaterialService materialService;

    @TestConfiguration
    static class DisableJWTFilterForTest {
        @Bean
        @Primary
        public com.lowes.config.JWTAuthenticationFilter jwtAuthenticationFilter() {
            return new com.lowes.config.JWTAuthenticationFilter(null, null) {
                @Override
                protected void doFilterInternal(
                        jakarta.servlet.http.HttpServletRequest request,
                        jakarta.servlet.http.HttpServletResponse response,
                        jakarta.servlet.FilterChain filterChain
                ) throws java.io.IOException, jakarta.servlet.ServletException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    @TestConfiguration
    static class MockMailSenderConfig {
        @Bean
        @Primary
        public JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }
    }


    private MaterialUserResponse getMaterialUserResponse(){

        MaterialUserResponse materialUserResponse = MaterialUserResponse.builder()
                .exposedId(UUID.fromString("55a0da0f-96fb-4df9-bda6-05db8ebda5f3"))
                .name("Cement")
                .pricePerQuantity(100)
                .phaseType(PhaseType.CIVIL)
                .unit(Unit.KG)
                .build();

        return materialUserResponse;

    }


    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void getExistingMaterialsByPhaseType() throws Exception {

        MaterialUserResponse materialUserResponse = getMaterialUserResponse();

        Mockito.when(materialService.getExistingMaterialsByPhaseType(PhaseType.CIVIL)).thenReturn(List.of(materialUserResponse));

        mockMvc.perform(get("/api/user/materials")
                        .param("phaseType","CIVIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cement"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"CUSTOMER"})
    public void getExistingMaterialsByPhaseType_InternalServerError_Returns500() throws Exception {

        Mockito.when(materialService.getExistingMaterialsByPhaseType(PhaseType.CIVIL))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(get("/api/user/materials")
                        .param("phaseType", "CIVIL"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("Internal Server Error : Unexpected failure"));
    }


}
