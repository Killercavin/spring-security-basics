package io.github.devcavin.gatelog.users

import io.github.devcavin.gatelog.common.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "roles")
class Role(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    override var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 100)
    var name: String
) : BaseEntity()