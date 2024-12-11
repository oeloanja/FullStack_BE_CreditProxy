package com.billit.credit.entity;

import com.billit.credit.enums.DocumentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pdf_data")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID userBorrowId;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String fileUrl;

    @Column(columnDefinition = "JSON")
    private String extractedData;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
