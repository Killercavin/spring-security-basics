package io.github.devcavin.gatelog.backend.visitors

import io.github.devcavin.gatelog.backend.auth.AccessScope
import io.github.devcavin.gatelog.backend.visitors.dto.VisitorSearchParams
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

object VisitorSpecification {

    fun search(
        scope: AccessScope,
        params: VisitorSearchParams
    ): Specification<Visitor> = Specification { root, _, cb ->

        val predicates = mutableListOf<Predicate>()

        /**
         * site filter - only applied for site-scoped access
         * SUPER_ADMIN with Global scope skips this entirely
        */
        if (scope is AccessScope.Site) {
            predicates.add(
                cb.equal(root.get<Any>("site").get<UUID>("id"), scope.siteId)
            )
        }

        params.name?.takeIf { it.isNotBlank() }?.let {
            predicates.add(cb.like(cb.lower(root.get("name")), "%${it.lowercase()}%"))
        }
        params.phone?.takeIf { it.isNotBlank() }?.let {
            predicates.add(cb.like(root.get("phone"), "%$it%"))
        }
        params.visitorType?.takeIf { it.isNotBlank() }?.let {
            predicates.add(cb.equal(root.get<String>("visitorType"), it))
        }
        params.zoneId?.let {
            predicates.add(cb.equal(root.get<Any>("zone").get<UUID>("id"), it))
        }
        params.status?.takeIf { it.isNotBlank() }?.let {
            predicates.add(cb.equal(root.get<Any>("visitStatus").get<String>("name"), it))
        }
        params.from?.let {
            predicates.add(cb.greaterThanOrEqualTo(root.get("checkInTime"), it))
        }
        params.to?.let {
            predicates.add(cb.lessThanOrEqualTo(root.get("checkInTime"), it))
        }

        cb.and(*predicates.toTypedArray())
    }
}