package com.billit.credit.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DocumentType {
    INCOME("소득증명원"),
    EMPLOYMENT("재직증명서");

    private final String description;
}
