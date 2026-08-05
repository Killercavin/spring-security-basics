package io.github.devcavin.gatelog.backend.auth

import io.github.devcavin.gatelog.backend.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.backend.common.exception.AccessDeniedException
import io.github.devcavin.gatelog.backend.users.User
import io.github.devcavin.gatelog.backend.visitors.Visitor
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthorizationService {
    /**
     * Derives the access scope for a user based on their role.
     * This is the single source of truth for scope decisions.
     */

    fun scopeFor(user: User): AccessScope {
        return when (user.role.name) {
            "SUPER_ADMIN" -> AccessScope.Global
            else -> AccessScope.Site(user.site.id!!)
        }
    }

    /**
     * Asserts the user's scope covers the given siteId.
     * Throws AuthorizationDeniedException if the scope does not cover it.
     */

    fun assertCovers(user: User, siteId: UUID) {
        val scope = scopeFor(user)

        if (!scope.covers(siteId)) {
            throw AuthorizationDeniedException(
                "Authorization denied for user"
            )
        }
    }

    /**
     * Asserts the user's scope covers the visitor's site.
     * Throws ResourceNotFoundException for site-scoped users seeing
     * resources from another site - avoids leaking resource existence.
     */

    fun assertCanAccessVisitor(user: User, visitor: Visitor) {
        val scope = scopeFor(user)

        if (!scope.covers(visitor.site.id!!)) throw ResourceNotFoundException(
            "Visitor",
            visitor.id!!
        )
    }

    /**
     * Returns a siteId filter appropriate for list/search queries.
     * Global scope returns null - callers omit the filter entirely.
     * Site scope returns the user's siteId - callers apply it.
     */

    fun siteFilterFor(user: User): UUID? = scopeFor(user).siteIdOrNull

    /**
     * Enforces who can create a user with the given role at the given site.
     * SUPER_ADMIN - unrestricted.
     * MANAGER - Staff only, at their own site.
     * STAFF - cannot create users.
     */

    fun assertCanCreateUser(
        requestedBy: User,
        targetRoleName: String,
        targetSiteId: UUID
    ) {
        when (val scope = scopeFor(requestedBy)) {
            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (requestedBy.role.name != "MANAGER")
                    throw AccessDeniedException("Insufficient privileges to create users")

                if (targetRoleName != "STAFF")
                    throw AccessDeniedException("Managers can only create Staff accounts")

                if (targetSiteId != scope.siteId)
                    throw AccessDeniedException("Managers can only create users at their own site")
            }
        }
    }

    /**
     * Enforces who can update a user's details and which role they can assign.
     * SUPER_ADMIN - unrestricted.
     * MANAGER - Staff at their own site, cannot elevate beyond Staff.
     */

    fun assertCanUpdateUser(
        requestedBy: User,
        target: User,
        newRoleName: String
    ) {
        when (val scope = scopeFor(requestedBy)) {
            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (target.site.id != scope.siteId)
                    throw AccessDeniedException("User does not belong to your site")

                if (target.role.name != "STAFF")
                    throw AccessDeniedException("Managers can only update Staff accounts")

                if (newRoleName != "STAFF")
                    throw AccessDeniedException("Managers cannot change role beyond Staff")
            }
        }
    }

    /**
     * Enforces who can deactivate a user.
     * SUPER_ADMIN - unrestricted.
     * MANAGER - Staff at their own site only.
     */

    fun assertCanDeactivateUser(requestedBy: User, target: User) {
        when (val scope = scopeFor(requestedBy)) {

            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (target.site.id != scope.siteId)
                    throw AccessDeniedException("User does not belong to your site")
                if (target.role.name != "STAFF")
                    throw AccessDeniedException("Managers can only deactivate Staff accounts")
            }
        }
    }

    /**
     * Enforces visibility - who can see a given user record.
     * SUPER_ADMIN - can see any user.
     * MANAGER - Staff at their own site only.
     */
    fun assertCanViewUser(requestedBy: User, target: User) {
        when (val scope = scopeFor(requestedBy)) {

            is AccessScope.Global -> Unit

            is AccessScope.Site -> {
                if (target.site.id != scope.siteId)
                    throw ResourceNotFoundException("User", target.id!!)
                if (target.role.name != "STAFF")
                    throw AccessDeniedException("Managers can only view Staff accounts")
            }
        }
    }

}