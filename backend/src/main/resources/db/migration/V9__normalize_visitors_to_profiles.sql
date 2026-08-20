-- 1. Create one profile for each distinct phone/site combination
--    that does not already have a profile.
--
--    Prefer the most recently checked-in visitor's name when
--    multiple historical visitor rows have the same phone/site.

INSERT INTO visitor_profiles (
    id,
    name,
    phone_number,
    site_id,
    created_at,
    updated_at
)
SELECT
    gen_random_uuid(),
    source.name,
    source.phone,
    source.site_id,
    now(),
    now()
FROM (
         SELECT DISTINCT ON (v.site_id, v.phone)
             v.site_id,
             v.phone,
             v.name
         FROM visitors v
             LEFT JOIN visitor_profiles p
         ON p.site_id = v.site_id
             AND p.phone_number = v.phone
         WHERE p.id IS NULL
         ORDER BY
             v.site_id,
             v.phone,
             v.check_in_time DESC,
             v.id DESC
     ) source;


-- 2. Attach every visitor to its site-scoped profile.

UPDATE visitors v
SET visitor_profile_id = p.id
    FROM visitor_profiles p
WHERE p.site_id = v.site_id
  AND p.phone_number = v.phone
  AND v.visitor_profile_id IS NULL;


-- 3. Fail the migration if any visitor still has no profile.
--
--    This is intentionally defensive. We should never silently
--    introduce a nullable relationship after this migration.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM visitors
        WHERE visitor_profile_id IS NULL
    ) THEN
        RAISE EXCEPTION
            'Cannot normalize visitors: one or more visitors have no visitor profile';
END IF;
END $$;


-- 4. The relationship is now mandatory.

ALTER TABLE visitors
    ALTER COLUMN visitor_profile_id SET NOT NULL;


-- 5. Visitor identity is now owned exclusively by visitor_profiles.

ALTER TABLE visitors
DROP COLUMN name;

ALTER TABLE visitors
DROP COLUMN phone;


-- 6. The old visitor-phone index is no longer relevant.

DROP INDEX IF EXISTS idx_visitors_phone;