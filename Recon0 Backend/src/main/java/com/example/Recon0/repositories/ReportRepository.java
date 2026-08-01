package com.example.Recon0.repositories;

import com.example.Recon0.models.Program;
import com.example.Recon0.models.Report;

import com.example.Recon0.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    long countByReporter(User reporter);
    long countByReporterAndStatus(User reporterId, String status);


    List<Report> findByReporter(User reporter);
    List<Report> findByProgramInOrderByCreatedAtDesc(List<Program> programs);

    /**
     * Calculates the sum of minimum bounties for all accepted reports by a specific user.
     * This is an approximation of total earnings. A real-world scenario might have a
     * dedicated 'bountyAwarded' field on the Report entity.
     * @param reporterId The ID of the user (reporter).
     * @return The sum of bounties, or null if there are no accepted reports.
     */
    @Query("SELECT SUM(p.min_bounty) FROM Report r JOIN r.program p WHERE r.reporter.id = :reporterId AND r.status = 'Accepted'")
    Integer sumBountiesForAcceptedReports(@Param("reporterId") UUID reporterId);

    /**
     * ADDED: Counts the total number of reports for a specific program.
     */
    long countByProgram(Program program);

    /**
     * ADDED: Counts the number of reports for a program with a specific status.
     */
    long countByProgramAndStatus(Program program, String status);

    /**
     * ADDED: A native query to group reports by severity and count them.
     * This is more efficient than fetching all reports and processing in Java.
     */
    @Query("SELECT new map(r.severity as severity, count(r) as count) FROM Report r WHERE r.program = :program GROUP BY r.severity")
    List<Map<String, Object>> countReportsBySeverity(@Param("program") Program program);

    /**
     * ADDED: Counts all reports for a list of programs.
     */
    long countByProgramIn(List<Program> programs);

    /**
     * ADDED: Counts reports with a specific status for a list of programs.
     */
    long countByProgramInAndStatus(List<Program> programs, String status);

    /**
     * ADDED: Finds the top 5 most recent reports for a list of programs.
     */
    List<Report> findTop5ByProgramInOrderByCreatedAtDesc(List<Program> programs);

    /**
     * ADDED: Counts the number of reports submitted by a user with a specific status.
     * Used to check for the "First Find" achievement.
     */
    long countByReporterAndStatusIgnoreCase(User reporter, String status);

    /**
     * ADDED: Counts reports by a user for a specific program with a specific status.
     * Used to check for the "Specialist" achievement.
     */
    long countByReporterAndProgramAndStatusIgnoreCase(User reporter, Program program, String status);

    long countByReporterAndStatusIgnoreCaseAndIdNot(User reporter, String status, UUID reportId);


}
