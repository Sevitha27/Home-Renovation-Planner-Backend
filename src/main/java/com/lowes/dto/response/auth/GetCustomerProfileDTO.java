package com.lowes.dto.response.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCustomerProfileDTO {
    private String name;
    private String contact;
    private String email;
    private String url;
}
