package io.github.devcavin.gatelog.backend.users

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.devcavin.gatelog.backend.common.persistence.BaseEntity
import io.github.devcavin.gatelog.backend.sites.Site
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import java.util.*

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    override var id: UUID? = null,

    @Column(nullable = false, length = 100)
    var name: String,

    @Email
    @Column(nullable = false, length = 150, unique = true)
    var email: String,

    @JsonIgnore
    @Column(name ="password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    var role: Role,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    var site: Site,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
) : BaseEntity()