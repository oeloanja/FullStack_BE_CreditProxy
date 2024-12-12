package com.billit.credit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RawExtractResponse {
    private BigDecimal income;  // 소득증명원용
    private Integer enp_length; // 재직증명서용
}