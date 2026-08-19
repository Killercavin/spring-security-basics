package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AuthorizationService
import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class VisitorService(
    private val visitorRepository: VisitorRepository,
    private val authorizationService: AuthorizationService
) {

    @Transactional(readOnly = true)
    fun getById(
        requestedBy: User,
        visitorId: UUID
    ): VisitorResponse {

        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow {
                ResourceNotFoundException("Visitor", visitorId)
            }

        authorizationService.assertCanAccessVisitor(
            requestedBy,
            visitor
        )

        return visitor.toResponse()
    }

    @Transactional(readOnly = true)
    fun search(
        requestedBy: User,
        params: VisitorSearchParams,
        pageable: Pageable
    ): Page<VisitorResponse> {

        val scope = authorizationService.scopeFor(requestedBy)

        return visitorRepository
            .findAll(
                VisitorSpecification.search(scope, params),
                pageable
            )
            .map(Visitor::toResponse)
    }

    @Transactional
    fun checkOut(
        requestedBy: User,
        visitorId: UUID
    ): VisitorResponse {

        val visitor = visitorRepository.findById(visitorId)
            .orElseThrow {
                ResourceNotFoundException("Visitor", visitorId)
            }

        authorizationService.assertCanAccessVisitor(
            requestedBy,
            visitor
        )

        visitor.checkOut()

        return visitorRepository
            .save(visitor)
            .toResponse()
    }
}