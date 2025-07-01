package com.lowes.dto.response.admin;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminToastDTO {

    @NonNull
    private String message;

}