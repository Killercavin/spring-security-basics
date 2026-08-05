package io.github.devcavin.gatelog.backend.visitors

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
interface VisitorRepository : JpaRepository<Visitor, UUID>, JpaSpecificationExecutor<Visitor> {
    // returning visitor lookup by phone within a site
    fun findTopBySiteIdAndPhoneOrderByCheckInTimeDesc(
        siteId: UUID,
        phone: String
    ): Visitor?

    // active visitors on the dashboard
    fun findAllBySiteIdAndVisitStatus(
        siteId: UUID,
        visitStatus: VisitStatus,
        pageable: Pageable
    ): Page<Visitor>

    // visitors checked in today for daily count
    @Query(
        """
        SELECT v FROM Visitor v
        WHERE v.site.id = :siteId
        AND v.checkInTime >= :startOfDay
        AND v.checkInTime < :endOfDay
        """
    )
    fun findAllCheckedInToday(
        siteId: UUID,
        startOfDay: OffsetDateTime,
        endOfDay: OffsetDateTime,
        pageable: Pageable
    ): Page<Visitor>

    // overdue visitors - checked in but no checkout past threshold
    @Query(
        """
        SELECT v FROM Visitor v
        WHERE v.site.id = :siteId
        AND v.visitStatus.name = 'CHECKED_IN'
        AND v.checkInTime <= :threshold
        """
    )
    fun findAllOverdue(
        siteId: UUID,
        threshold: OffsetDateTime
    ): List<Visitor>

    // bulk status update for overdue job
    @Modifying
    @Query(
        """
        UPDATE Visitor v
        SET v.visitStatus = :overdueStatus
        WHERE v.site.id = :siteId
        AND v.visitStatus.name = 'CHECKED_IN'
        AND v.checkInTime <= :threshold
        """
    )
    fun markOverdue(
        siteId: UUID,
        threshold: OffsetDateTime,
        overdueStatus: VisitStatus
    ): Int

    // count by status - dashboard stats
    fun countBySiteIdAndVisitStatus(
        siteId: UUID,
        visitStatus: VisitStatus
    ): Long

    fun countBySiteIdAndVisitorProfileId(siteId: UUID, profileId: UUID): Long

    @Query("""
    SELECT v FROM Visitor v
    WHERE v.visitStatus = :visitStatus
    """)
    fun findAllByVisitStatus(
        visitStatus: VisitStatus,
        pageable: Pageable
    ): Page<Visitor>

    @Query("""
    SELECT v FROM Visitor v
    WHERE v.visitStatus.name = 'CHECKED_IN'
    AND v.checkInTime <= :threshold
    """)
    fun findAllOverdueGlobal(
        threshold: OffsetDateTime
    ): List<Visitor>

    @Query("""
    SELECT COUNT(v) FROM Visitor v
    WHERE v.checkInTime >= :startOfDay
    AND v.checkInTime < :endOfDay
    """)
    fun countCheckedInTodayGlobal(
        startOfDay: OffsetDateTime,
        endOfDay: OffsetDateTime
    ): Long

    fun countByVisitStatus(visitStatus: VisitStatus): Long

    fun countBySiteIdAndVisitStatusAndCheckOutTimeBetween(
        siteId: UUID,
        visitStatus: VisitStatus,
        start: OffsetDateTime,
        end: OffsetDateTime
    ): Long

    fun countByVisitStatusAndCheckOutTimeBetween(
        visitStatus: VisitStatus,
        start: OffsetDateTime,
        end: OffsetDateTime
    ): Long
}