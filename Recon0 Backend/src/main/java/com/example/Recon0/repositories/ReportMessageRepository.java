package com.example.Recon0.repositories;

import com.example.Recon0.models.Report;
import com.example.Recon0.models.ReportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportMessageRepository extends JpaRepository<ReportMessage, UUID> {

    /**
     * Finds all messages for a given report, ordered by their creation time.
     */
    List<ReportMessage> findByReportOrderByCreatedAtAsc(Report report);
}

