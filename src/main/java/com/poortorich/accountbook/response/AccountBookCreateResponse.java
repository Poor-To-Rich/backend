package com.poortorich.accountbook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBookCreateResponse {

    private Long id;
    private Long categoryId;
}
