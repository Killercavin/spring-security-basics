package io.github.devcavin.gatelog.visitors

import io.github.devcavin.gatelog.auth.AccessScope
import io.github.devcavin.gatelog.sites.Site
import io.github.devcavin.gatelog.visitors.dto.VisitSearchParams
import io.github.devcavin.gatelog.zones.Zone
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
            predicates += cb.equal(
                root.get<Site>("site").get<UUID>("id"),
                scope.siteId
            )
        }

        val profile = root.get<VisitorProfile>("visitorProfile")

        params.name
            ?.takeIf(String::isNotBlank)
            ?.let { name ->
                predicates += cb.like(
                    cb.lower(profile.get("name")),
                    "%${name.lowercase()}%"
                )
            }

        params.phone
            ?.takeIf(String::isNotBlank)
            ?.let { phone ->
                predicates += cb.like(
                    profile.get("phoneNumber"),
                    "%$phone%"
                )
            }

        params.visitorType
            ?.takeIf(String::isNotBlank)
            ?.let { visitorType ->
                predicates += cb.equal(
                    root.get<String>("visitorType"),
                    visitorType
                )
            }

        params.zoneId?.let { zoneId ->
            predicates += cb.equal(
                root.get<Zone>("zone").get<UUID>("id"),
                zoneId
            )
        }

        params.status
            ?.takeIf(String::isNotBlank)
            ?.let { status ->
                predicates += cb.equal(
                    root.get<VisitStatus>("visitStatus")
                        .get<String>("name"),
                    status
                )
            }

        params.from?.let { from ->
            predicates += cb.greaterThanOrEqualTo(
                root.get("checkInTime"),
                from
            )
        }

        params.to?.let { to ->
            predicates += cb.lessThanOrEqualTo(
                root.get("checkInTime"),
                to
            )
        }

        cb.and(*predicates.toTypedArray())
    }
}
