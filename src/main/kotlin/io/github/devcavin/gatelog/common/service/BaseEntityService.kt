package io.github.devcavin.gatelog.common.service

import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

abstract class BaseEntityService<T>(
    private val repository: JpaRepository<T, UUID>,
    private val resourceName: String
) {

    protected fun findEntityById(id: UUID): T =
        repository.findById(id)
            .orElseThrow {
                ResourceNotFoundException(resourceName, id)
            }
}