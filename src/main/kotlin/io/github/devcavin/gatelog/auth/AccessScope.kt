package io.github.devcavin.gatelog.auth

import java.util.UUID

/**
 * Represents the data visibility scope for an authenticated user.
 *
 * GLOBAL - user can access resources across all sites (SUPER_ADMIN)
 *
 * SITE - user can only access resources belonging to their own site (MANAGER, STAFF)
 *
 * This separates the concept of "what a user can do" (role)
 * from "which resources they can see" (scope), making authorization
 * decisions explicit and centralized rather than inferred from site FK.
 */

sealed class AccessScope {
    /** No site boundary - SUPER_ADMIN sees everything */
    data object Global : AccessScope()

    /** Restricted to a single site - MANAGER and STAFF */
    data class Site(val siteId: UUID) : AccessScope()

    /** True when this scope covers the given siteId */
    fun covers(siteId: UUID): Boolean = when (this) {
        is Global -> true
        is Site -> this.siteId == siteId
    }

    /** Returns the siteId if site-scoped, null if global */
    val siteIdOrNull: UUID? get() = when (this) {
        is Global -> null
        is Site -> this.siteId
    }
}