package com.lowes.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminToastDTO {

    @NonNull
    private String message;

}