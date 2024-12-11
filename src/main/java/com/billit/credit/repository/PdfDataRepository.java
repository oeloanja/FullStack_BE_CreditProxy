package com.billit.credit.repository;

import com.billit.credit.entity.PdfData;
import com.billit.credit.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PdfDataRepository extends JpaRepository<PdfData, Long> {
    Optional<PdfData> findByUserBorrowIdAndDocumentType(Long userBorrowId, DocumentType documentType);

    @Query("SELECT p FROM PdfData p WHERE p.userBorrowId = :userBorrowId " +
            "AND p.documentType = :documentType " +
            "ORDER BY p.createdAt DESC LIMIT 1")
    Optional<PdfData> findLatestByUserIdAndType(
            @Param("userBorrowId") UUID userBorrowId,
            @Param("documentType") DocumentType documentType
    );
}
