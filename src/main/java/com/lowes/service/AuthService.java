package com.lowes.service;

import com.lowes.dto.request.auth.AuthLoginDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.dto.request.SkillRequestDTO;
import com.lowes.dto.request.auth.UpdateUserProfileDTO;
import com.lowes.dto.response.auth.UserResponseDTO;
import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.SkillType;
import com.lowes.mapper.UserConverter;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final UserConverter userConverter;
    private final SkillRepository skillRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CloudinaryServiceImpl cloudinaryServiceImpl;


    @Transactional
    public ResponseEntity<?> register(AuthRegisterDTO request) {
        try {
            User user = userConverter.authRegisterDTOtoUser(request);
            userRepository.save(user);
            System.out.println("After user save");
            if (user.getRole() == Role.VENDOR) {
                List<Skill> skills = new ArrayList<>();
                for (SkillRequestDTO skillDTO : request.getSkills()) {
                    SkillType skillType = SkillType.valueOf(skillDTO.getSkillName().toUpperCase());
                    Skill skill = skillRepository.findByNameAndBasePrice(skillType, skillDTO.getBasePrice())
                            .orElseGet(() -> skillRepository.save(
                                    Skill.builder()
                                            .name(skillType)
                                            .basePrice(skillDTO.getBasePrice())
                                            .build()
                            ));
                    skills.add(skill);
                }

                Vendor vendor = userConverter.authRegisterDTOtoVendor(request, user, skills);
                vendorRepository.save(vendor);
            }

            Optional<User> userFromDBOpt = userRepository.findByEmail(request.getEmail());

            if (userFromDBOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponseDTO.builder().message("ERROR").email(request.getEmail()).build());

            User userFromDB = userFromDBOpt.get();

            final String accessToken = jwtService.generateAccessToken(userFromDB);
            final String refreshToken = jwtService.generateRefreshToken(userFromDB);

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)  // secure true only if we have https domain
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .sameSite("strict")
                    .build();

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(UserResponseDTO.builder().message("SUCCESS").accessToken(accessToken).email(userFromDB.getEmail()).role(userFromDB.getRole().name()).build());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponseDTO.builder().message("ERROR").email(request.getEmail()).build());
        }
    }

    public ResponseEntity<?> login(AuthLoginDTO authLoginDTO) {
        try{
            Optional<User> userOpt= userRepository.findByEmail(authLoginDTO.getEmail());
            if(userOpt.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not registered");
            }
            User user=userOpt.get();
            if(user.getEmail().equals(authLoginDTO.getEmail()) && passwordEncoder.matches(authLoginDTO.getPassword(),user.getPassword()))
            {
                final String accessToken = jwtService.generateAccessToken(user);
                final String refreshToken = jwtService.generateRefreshToken(user);

                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false)  // secure true only if we have https domain
                        .path("/")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("strict")
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                        .body(UserResponseDTO.builder().message("SUCCESS").accessToken(accessToken).email(user.getEmail()).role(user.getRole().name()).build());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserResponseDTO.builder().message("ERROR").email(user.getEmail()).build());
        }
        catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponseDTO.builder().message("ERROR").email(authLoginDTO.getEmail()).build());
        }
    }

    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response){
        try{
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;

            if(cookies != null){
                for(Cookie cookie : cookies){
                    if("refreshToken".equals(cookie.getName()))
                        refreshToken = cookie.getValue();
                }
            }

            if(refreshToken == null || !jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            String email = jwtService.extractEmail(refreshToken);
            Optional<User> userOpt = userRepository.findByEmail(email);

            if(userOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

            User user = userOpt.get();
            String newAccessToken = jwtService.generateAccessToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(UserResponseDTO.builder().message("SUCCESS").accessToken(newAccessToken).email(email).role(user.getRole().name()).build());

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error!");
        }
    }

//    public ResponseEntity<?> updateProfile(UpdateUserProfileDTO dto) {
//        try{
//            User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//            String imageUrl = null;
//            if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
//                imageUrl = cloudinaryServiceImpl.uploadFile(dto.getProfileImage(), "RenoBase");
//                if (imageUrl == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserResponseDTO.builder().message("ERROR").build());
//            }
//
//            userConverter.updateUserProfileDTOToUser(dto, user, imageUrl);
//            return ResponseEntity.status(HttpStatus.OK).body(UserResponseDTO.builder().message("SUCCESS").build());
//
//        }catch (Exception e)
//        {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UserResponseDTO.builder().message("ERROR").build());
//        }
//    }

    public ResponseEntity<?> updateProfile(UpdateUserProfileDTO dto) {
        try {
            // Get the currently authenticated user
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String imageUrl = null;

            // Upload image only if it's provided
            if (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) {
                imageUrl = cloudinaryServiceImpl.uploadFile(dto.getProfileImage(), "RenoBase");
                if (imageUrl == null) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(UserResponseDTO.builder().message("ERROR: Image upload failed").build());
                }
            }

            // Convert and update user using non-null fields
            userConverter.updateUserProfileDTOToUser(dto, user, imageUrl);

            return ResponseEntity.ok(UserResponseDTO.builder().message("SUCCESS").build());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(UserResponseDTO.builder().message("ERROR").build());
        }
    }



    public ResponseEntity<?> getProfile() {
        try{
            User user= (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user.getRole() == Role.CUSTOMER){
                return ResponseEntity.status(HttpStatus.OK).body(userConverter.userToGetCustomerProfileDTO(user));
            }else if(user.getRole() == Role.VENDOR){
                return ResponseEntity.status(HttpStatus.OK).body(userConverter.userToGetVendorProfileDTO(user));
            }else
            {
                return null;
            }
        }catch(Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get Profile");
        }

    }
}