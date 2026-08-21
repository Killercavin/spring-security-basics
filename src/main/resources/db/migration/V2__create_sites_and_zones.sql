CREATE TABLE sites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NOT NULL
);

CREATE TABLE zones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    site_id UUID NOT NULL REFERENCES sites(id),
    CONSTRAINT uq_zones_site_name UNIQUE(site_id, name)
);