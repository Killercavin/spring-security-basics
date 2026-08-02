package io.github.devcavin.gatelog.backend.common.persistence

import jakarta.persistence.MappedSuperclass
import java.util.UUID

@MappedSuperclass
abstract class BaseEntity {

    abstract var id: UUID?

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as BaseEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int =
        id?.hashCode() ?: System.identityHashCode(this)
}