package io.github.devcavin.gatelog.auth

import io.github.devcavin.gatelog.common.exception.ResourceNotFoundException
import io.github.devcavin.gatelog.common.exception.AccessDeniedException
import io.github.devcavin.gatelog.sites.Site
import io.github.devcavin.gatelog.users.User
import io.github.devcavin.gatelog.visitors.Visit
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
            "MANAGER", "STAFF" -> AccessScope.Site(user.site.id!!)
            else -> throw AccessDeniedException("Unsupported user role")
        }
    }

    /**
     * Asserts the user's scope covers the given siteId.
     * Throws AuthorizationDeniedException if the scope does not cover it.
     */

    fun assertCovers(user: User, siteId: UUID) {
        if (!scopeFor(user).covers(siteId)) {
            throw AccessDeniedException("Authorization denied")
        }
    }

    /**
     * Asserts the user's scope covers the visitor's site.
     * Throws ResourceNotFoundException for site-scoped users seeing
     * resources from another site - avoids leaking resource existence.
     */

    fun assertCanAccessVisitor(user: User, visitor: Visit) {
        if (!scopeFor(user).covers(requireSiteId(visitor.site))) {
            throw ResourceNotFoundException("Visitor", requireNotNull(visitor.id))
        }
    }

    /**
     * Returns a siteId filter appropriate for list/search queries.
     * Global scope returns null - callers omit the filter entirely.
     * Site scope returns the user's siteId - callers apply it.
     */

    fun siteFilterFor(user: User): UUID? = scopeFor(user).siteIdOrNull

    fun canAccessSite(user: User, siteId: UUID): Boolean = scopeFor(user).covers(siteId)

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
                    throw AccessDeniedException("Managers can only create staff accounts")

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
                    throw AccessDeniedException("Managers can only update staff accounts")

                if (newRoleName != "STAFF")
                    throw AccessDeniedException("Managers cannot change role beyond staff")
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
                    throw AccessDeniedException("Managers can only deactivate staff accounts")
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
                    throw ResourceNotFoundException("User", requireNotNull(target.id))

                if (target.role.name != "STAFF")
                    throw AccessDeniedException("Managers can only view staff accounts")
            }
        }
    }

    private fun requireSiteId(user: User) : UUID {
        return user.site.id ?: throw AccessDeniedException("User is not associated with a site")
    }

    private fun requireSiteId(site: Site) : UUID {
        return site.id ?: throw AccessDeniedException("Resource is not associated with a site")
    }
}