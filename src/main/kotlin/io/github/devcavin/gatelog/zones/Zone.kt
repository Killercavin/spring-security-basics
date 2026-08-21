package io.github.devcavin.gatelog.zones

import io.github.devcavin.gatelog.common.persistence.BaseEntity
import io.github.devcavin.gatelog.sites.Site
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
import java.util.UUID

@Entity
@Table(
    name = "zones",
    uniqueConstraints = [UniqueConstraint(name = "uq_zones_site_name", columnNames = ["site_id", "name"])]
)
class Zone(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    override var id: UUID? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    var site: Site
) : BaseEntity()