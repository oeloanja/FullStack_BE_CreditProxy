package com.billit.credit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUrlRequest {
    private String fileUrl;
    private String phoneNumber;
}