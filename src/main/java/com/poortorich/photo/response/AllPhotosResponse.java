package com.poortorich.photo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllPhotosResponse {

    private Boolean hasNext;
    private String nextDate;
    private Long nextId;
    private Long photoCount;
    private List<PhotoInfoResponse> photos;
}
