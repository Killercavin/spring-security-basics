CREATE TABLE visitor_profiles(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(25) NOT NULL,
    site_id UUID NOT NULL REFERENCES sites(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_visitor_profiles_phone_site UNIQUE(phone_number, site_id)
);

CREATE INDEX idx_visitor_profiles_phone ON visitor_profiles(phone_number);
CREATE INDEX idx_visitor_profiles_site_id ON visitor_profiles(site_id);

ALTER TABLE visitors ADD column visitor_profile_id UUID REFERENCES visitor_profiles(id);

CREATE INDEX idx_visitors_profile_id ON visitors(visitor_profile_id);