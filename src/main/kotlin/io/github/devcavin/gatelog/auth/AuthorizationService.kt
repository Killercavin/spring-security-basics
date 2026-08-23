package io.github.devcavin.gatelog.auth

import io.github.devcavin.gatelog.common.exception.AccessDeniedException
import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.sites.Site
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.Visit
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthorizationService {

    /**
     * Derives the access scope for a user based on their role.
     * This is the single source of truth for scope decisions.
     */
    fun scopeFor(user: User): AccessScope {
        return when (user.role.name) {
            "ADMIN" -> AccessScope.Global

            "MANAGER", "STAFF" ->
                AccessScope.Site(requireSiteId(user))

            else ->
                throw AccessDeniedException("Unsupported user role")
        }
    }

    /**
     * Asserts that the user's scope covers the given site.
     */
    fun assertCovers(
        user: User,
        siteId: UUID
    ) {
        if (!scopeFor(user).covers(siteId)) {
            throw AccessDeniedException("Authorization denied")
        }
    }

    /**
     * Asserts that the user's scope covers the visit's site.
     *
     * Site-scoped users receive a not-found response when attempting
     * to access a visit belonging to another site, preventing resource
     * existence from being leaked.
     */
    fun assertCanAccessVisit(
        user: User,
        visit: Visit
    ) {
        if (!scopeFor(user).covers(requireSiteId(visit.site))) {
            throw ResourceNotFoundException(
                "Visit",
                requireNotNull(visit.id)
            )
        }
    }

    /**
     * Enforces who can create a user with the given role at the given site.
     *
     * ADMIN    - unrestricted.
     * MANAGER  - Staff only, at their own site.
     * STAFF    - cannot create users.
     */
    fun assertCanCreateUser(
        requestedBy: User,
        targetRoleName: String,
        targetSiteId: UUID
    ) {
        when (val scope = scopeFor(requestedBy)) {
            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (requestedBy.role.name != "MANAGER") {
                    throw AccessDeniedException(
                        "Insufficient privileges to create users"
                    )
                }

                if (targetRoleName != "STAFF") {
                    throw AccessDeniedException(
                        "Managers can only create staff accounts"
                    )
                }

                if (targetSiteId != scope.siteId) {
                    throw AccessDeniedException(
                        "Managers can only create users at their own site"
                    )
                }
            }
        }
    }

    /**
     * Enforces who can update a user's details and which role they can assign.
     *
     * ADMIN    - unrestricted.
     * MANAGER  - Staff at their own site, cannot elevate beyond Staff.
     */
    fun assertCanUpdateUser(
        requestedBy: User,
        target: User,
        newRoleName: String
    ) {
        when (val scope = scopeFor(requestedBy)) {
            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (target.site.id != scope.siteId) {
                    throw AccessDeniedException(
                        "User does not belong to your site"
                    )
                }

                if (target.role.name != "STAFF") {
                    throw AccessDeniedException(
                        "Managers can only update staff accounts"
                    )
                }

                if (newRoleName != "STAFF") {
                    throw AccessDeniedException(
                        "Managers cannot change role beyond staff"
                    )
                }
            }
        }
    }

    /**
     * Enforces who can deactivate a user.
     *
     * ADMIN    - unrestricted.
     * MANAGER  - Staff at their own site only.
     */
    fun assertCanDeactivateUser(
        requestedBy: User,
        target: User
    ) {
        when (val scope = scopeFor(requestedBy)) {
            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (target.site.id != scope.siteId) {
                    throw AccessDeniedException(
                        "User does not belong to your site"
                    )
                }

                if (target.role.name != "STAFF") {
                    throw AccessDeniedException(
                        "Managers can only deactivate staff accounts"
                    )
                }
            }
        }
    }

    /**
     * Enforces visibility of a user record.
     *
     * ADMIN    - can see any user.
     * MANAGER  - Staff at their own site only.
     */
    fun assertCanViewUser(
        requestedBy: User,
        target: User
    ) {
        when (val scope = scopeFor(requestedBy)) {
            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (target.site.id != scope.siteId) {
                    throw ResourceNotFoundException(
                        "User",
                        requireNotNull(target.id)
                    )
                }

                if (target.role.name != "STAFF") {
                    throw AccessDeniedException(
                        "Managers can only view staff accounts"
                    )
                }
            }
        }
    }

    private fun requireSiteId(user: User): UUID =
        user.site.id
            ?: throw AccessDeniedException(
                "User is not associated with a site"
            )

    private fun requireSiteId(site: Site): UUID =
        site.id
            ?: throw AccessDeniedException(
                "Resource is not associated with a site"
            )
}