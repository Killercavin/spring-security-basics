package io.github.devcavin.gatelog.auth

import java.util.UUID

/**
 * Represents the data visibility scope for an authenticated user.
 *
 * GLOBAL - user can access resources across all sites (ADMIN)
 *
 * SITE - user can only access resources belonging to their own site (MANAGER, STAFF)
 *
 * This separates the concept of "what a user can do" (role)
 * from "which resources they can see" (scope), making authorization
 * decisions explicit and centralized rather than inferred from site FK.
 */

sealed class AccessScope {
    /** No site boundary - ADMIN sees everything */
    data object Global : AccessScope()

    /** Restricted to a single site - MANAGER and STAFF */
    data class Site(val siteId: UUID) : AccessScope()

    /** True when this scope covers the given siteId */
    fun covers(siteId: UUID): Boolean = when (this) {
        is Global -> true
        is Site -> this.siteId == siteId
    }

    /**
     * Returns the site boundary for this scope.
     *
     * Site scope returns its site ID.
     * Global scope returns null because no site filter is required.
     */
    val siteIdOrNull: UUID?
        get() = when (this) {
            is Global -> null
            is Site -> siteId
        }
}