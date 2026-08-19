package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.common.persistence.BaseEntity
import io.github.devcavin.gatelog.backend.sites.Site
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(
    name = "visitor_profiles",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_visitor_profile_site_phone",
            columnNames = ["site_id", "phone_number"]
        )
    ]
)
class VisitorProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    override var id: UUID? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, name = "phone_number", length = 25)
    var phoneNumber: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    var site: Site,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now()
) : BaseEntity()