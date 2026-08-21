package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.auth.AccessScope
import io.github.devcavin.gatelog.visitors.dto.VisitSearchParams
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.util.UUID

object VisitSpecification {

    fun search(
        scope: AccessScope,
        params: VisitSearchParams
    ): Specification<Visit> = Specification { root, _, cb ->

        val predicates = mutableListOf<Predicate>()

        if (scope is AccessScope.Site) {
            predicates.add(
                cb.equal(
                    root.get<Any>("site").get<UUID>("id"),
                    scope.siteId
                )
            )
        }

        val profile = root.get<VisitorProfile>("visitorProfile")

        params.name
            ?.takeIf { it.isNotBlank() }
            ?.let {
                predicates.add(
                    cb.like(
                        cb.lower(profile.get("name")),
                        "%${it.lowercase()}%"
                    )
                )
            }

        params.phone
            ?.takeIf { it.isNotBlank() }
            ?.let {
                predicates.add(
                    cb.like(
                        profile.get("phoneNumber"),
                        "%$it%"
                    )
                )
            }

        params.visitorType
            ?.takeIf { it.isNotBlank() }
            ?.let {
                predicates.add(
                    cb.equal(
                        root.get<String>("visitorType"),
                        it
                    )
                )
            }

        params.zoneId?.let {
            predicates.add(
                cb.equal(
                    root.get<Any>("zone").get<UUID>("id"),
                    it
                )
            )
        }

        params.status
            ?.takeIf { it.isNotBlank() }
            ?.let {
                predicates.add(
                    cb.equal(
                        root.get<Any>("visitStatus")
                            .get<String>("name"),
                        it
                    )
                )
            }

        params.from?.let {
            predicates.add(
                cb.greaterThanOrEqualTo(
                    root.get("checkInTime"),
                    it
                )
            )
        }

        params.to?.let {
            predicates.add(
                cb.lessThanOrEqualTo(
                    root.get("checkInTime"),
                    it
                )
            )
        }

        cb.and(*predicates.toTypedArray())
    }
}