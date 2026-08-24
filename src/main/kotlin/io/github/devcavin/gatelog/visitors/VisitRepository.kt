package io.github.devcavin.gatelog.visitors

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
interface VisitRepository :
    JpaRepository<Visit, UUID>,
    JpaSpecificationExecutor<Visit> {

    /*
     * Visitor history
     */

    fun findTopByVisitorProfileIdOrderByCheckInTimeDesc(
        visitorProfileId: UUID
    ): Visit?

    fun countBySiteIdAndVisitorProfileId(
        siteId: UUID,
        profileId: UUID
    ): Long

    fun findFirstByVisitorProfileIdAndVisitStatusName(
        visitorProfileId: UUID,
        statusName: String
    ): Visit?

    /*
     * Overdue processing
     *
     * The overdue threshold belongs to the scheduled job.
     * The dashboard should only query visits that have already
     * been transitioned to the OVERDUE status.
     */

    @Modifying
    @Query(
        """
    UPDATE Visit v
    SET v.visitStatus = :overdueStatus
    WHERE v.visitStatus.name = 'CHECKED_IN'
      AND v.checkInTime <= :threshold
    """
    )
    fun markOverdue(
        @Param("threshold") threshold: OffsetDateTime,
        @Param("overdueStatus") overdueStatus: VisitStatus
    ): Int

    /*
     * Dashboard queries
     *
     * siteId = null  -> global scope (ADMIN)
     * siteId != null -> site scope (MANAGER / STAFF)
     *
     * Keeping the scope handling here means DashboardService
     * does not need separate global/site repository calls.
     */

    @Query(
        """
        SELECT COUNT(v)
        FROM Visit v
        WHERE (:siteId IS NULL OR v.site.id = :siteId)
          AND (
              v.visitStatus = :checkedInStatus
              OR v.visitStatus = :overdueStatus
          )
        """
    )
    fun countCurrentlyOnPremises(
        @Param("siteId") siteId: UUID?,
        @Param("checkedInStatus") checkedInStatus: VisitStatus,
        @Param("overdueStatus") overdueStatus: VisitStatus
    ): Long

    @Query(
        """
        SELECT COUNT(v)
        FROM Visit v
        WHERE (:siteId IS NULL OR v.site.id = :siteId)
          AND v.checkInTime >= :startOfDay
          AND v.checkInTime < :endOfDay
        """
    )
    fun countCheckedInToday(
        @Param("siteId") siteId: UUID?,
        @Param("startOfDay") startOfDay: OffsetDateTime,
        @Param("endOfDay") endOfDay: OffsetDateTime
    ): Long

    @Query(
        """
        SELECT COUNT(v)
        FROM Visit v
        WHERE (:siteId IS NULL OR v.site.id = :siteId)
          AND v.visitStatus = :checkedOutStatus
          AND v.checkOutTime >= :startOfDay
          AND v.checkOutTime < :endOfDay
        """
    )
    fun countCheckedOutToday(
        @Param("siteId") siteId: UUID?,
        @Param("checkedOutStatus") checkedOutStatus: VisitStatus,
        @Param("startOfDay") startOfDay: OffsetDateTime,
        @Param("endOfDay") endOfDay: OffsetDateTime
    ): Long

    @Query(
        """
        SELECT COUNT(v)
        FROM Visit v
        WHERE (:siteId IS NULL OR v.site.id = :siteId)
          AND v.visitStatus = :overdueStatus
        """
    )
    fun countOverdue(
        @Param("siteId") siteId: UUID?,
        @Param("overdueStatus") overdueStatus: VisitStatus
    ): Long

    @Query(
        """
        SELECT v
        FROM Visit v
        WHERE (:siteId IS NULL OR v.site.id = :siteId)
          AND v.visitStatus = :checkedInStatus
        ORDER BY v.checkInTime DESC
        """
    )
    fun findActiveVisitors(
        @Param("siteId") siteId: UUID?,
        @Param("checkedInStatus") checkedInStatus: VisitStatus,
        pageable: Pageable
    ): Page<Visit>

    @Query(
        """
    SELECT v
    FROM Visit v
    WHERE (:siteId IS NULL OR v.site.id = :siteId)
      AND v.visitStatus = :overdueStatus
    ORDER BY v.checkInTime ASC
    """
    )
    fun findOverdueVisitors(
        @Param("siteId") siteId: UUID?,
        @Param("overdueStatus") overdueStatus: VisitStatus,
        pageable: Pageable
    ): Page<Visit>

    @Query(
        """
        SELECT v
        FROM Visit v
        WHERE (:siteId IS NULL OR v.site.id = :siteId)
          AND v.visitStatus = :checkedOutStatus
        ORDER BY v.checkOutTime DESC
        """
    )
    fun findRecentlyCheckedOut(
        @Param("siteId") siteId: UUID?,
        @Param("checkedOutStatus") checkedOutStatus: VisitStatus,
        pageable: Pageable
    ): Page<Visit>

    @Query(
        """
    SELECT v
    FROM Visit v
    WHERE (:siteId IS NULL OR v.site.id = :siteId)
      AND v.checkOutTime IS NULL
      AND v.checkInTime < :startOfToday
    ORDER BY v.checkInTime ASC
    """
    )
    fun findOvernightVisitors(
        @Param("siteId") siteId: UUID?,
        @Param("startOfToday") startOfToday: OffsetDateTime,
        pageable: Pageable
    ): Page<Visit>

    @Query(
        """
    SELECT COUNT(v)
    FROM Visit v
    WHERE (:siteId IS NULL OR v.site.id = :siteId)
      AND v.checkOutTime IS NULL
      AND v.checkInTime < :startOfToday
    """
    )
    fun countOvernight(
        @Param("siteId") siteId: UUID?,
        @Param("startOfToday") startOfToday: OffsetDateTime
    ): Long
}