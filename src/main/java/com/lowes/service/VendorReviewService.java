package com.lowes.service;

import com.lowes.dto.request.VendorReviewRequestDTO;
import com.lowes.dto.response.VendorReviewDTO;
import com.lowes.entity.Skill;

import com.lowes.entity.Vendor;
import com.lowes.entity.VendorReview;
import com.lowes.entity.enums.SkillType;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.VendorReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.lowes.entity.User;
import com.lowes.repository.VendorRepository;
import com.lowes.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;



@Service
public class VendorReviewService {

    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorReviewRepository vendorReviewRepository;

    @Transactional
    public List<VendorReviewDTO> getVendorsBySkill(String skillName) {
        SkillType skillEnum = null;
        try {
            skillEnum = SkillType.valueOf(skillName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid skill: '" + skillName + "'. Must be one of: " +
                    Arrays.toString(SkillType.values()));
        }

        SkillType finalSkillEnum = skillEnum;
        Skill skill = skillRepository.findByName(skillEnum)
                .orElseThrow(() -> new RuntimeException("Skill not found: " + finalSkillEnum.name()));

        return skill.getVendors().stream()
                .filter(Vendor::getApproved)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    private VendorReviewDTO mapToDTO(Vendor vendor) {
        List<VendorReview> reviews = vendor.getReviews();

        List<VendorReviewDTO.ReviewDetail> reviewDetails = reviews.stream()
                .map(review -> VendorReviewDTO.ReviewDetail.builder()
                        .reviewerName(review.getReviewer().getName())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt() != null
                                ? review.getCreatedAt().toString()  // or format if needed
                                : "")
                        .build())
                .collect(Collectors.toList());

        double averageRating = reviews.stream()
                .mapToDouble(VendorReview::getRating)
                .average()
                .orElse(0.0);

        Skill skill = vendor.getSkills().stream().findFirst().orElse(null);

        return VendorReviewDTO.builder()
                .id(vendor.getId())
                .name(vendor.getUser().getName())
                .pic(vendor.getUser().getPic())
                .rating(averageRating)
                .reviews(reviewDetails)
                .available(vendor.isAvailable())
                .experience(vendor.getExperience())
                .companyName(vendor.getCompanyName())
                .basePrice(skill != null ? skill.getBasePrice() : null)
                .build();
    }
    public void addReview(VendorReviewRequestDTO dto) {
        Vendor vendor = vendorRepository.findById(dto.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        VendorReview review = VendorReview.builder()
                .vendor(vendor)
                .reviewer(user)
                .comment(dto.getComment())
                .rating(dto.getRating())
                .build();

        vendorReviewRepository.save(review);
    }

    public void updateReview(UUID reviewId, VendorReviewRequestDTO dto) {
        VendorReview review = vendorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        vendorReviewRepository.save(review);
    }

    public void deleteReview(UUID reviewId) {
        vendorReviewRepository.deleteById(reviewId);
    }

}
