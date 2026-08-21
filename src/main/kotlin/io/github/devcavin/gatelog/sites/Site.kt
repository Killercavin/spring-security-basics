package io.github.devcavin.gatelog.sites

import io.github.devcavin.gatelog.common.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sites")
class Site(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    override var id: UUID? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, length = 255)
    var location: String
) : BaseEntity()