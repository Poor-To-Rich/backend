package com.poortorich.chatnotice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreviewNoticesResponse {

    private List<PreviewNoticeResponse> notices;
}
