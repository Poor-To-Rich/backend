package com.poortorich.accountbook.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBookInfoResponse {

    private Long id;
    private String color;
    private String categoryName;
    private String title;
    private String date;
    private Boolean isIteration;
    private String type;
    private Long cost;
}
