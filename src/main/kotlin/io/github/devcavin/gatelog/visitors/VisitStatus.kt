package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.common.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "visit_statuses")
class VisitStatus(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    override var id: UUID? = null,

    @Column(nullable = false, length = 100, unique = true)
    var name: String
) : BaseEntity() {

    override fun toString(): String {
        return "VisitStatus(id=$id, name='$name')"
    }
}