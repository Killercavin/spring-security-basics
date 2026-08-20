package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.common.persistence.BaseEntity
import io.github.devcavin.gatelog.backend.sites.Site
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.zones.Zone
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "visitors")
class Visitor(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    override var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visitor_profile_id", nullable = false)
    var visitorProfile: VisitorProfile,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    var site: Site,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    var zone: Zone? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_status_id", nullable = false)
    var visitStatus: VisitStatus,

    @Column(name = "visitor_type", nullable = false, length = 50)
    var visitorType: String,

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT 'General Visit'")
    var purpose: String = "General Visit",

    @Column(name = "check_in_time", nullable = false, updatable = false)
    var checkInTime: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "check_out_time")
    var checkOutTime: OffsetDateTime? = null
) : BaseEntity()