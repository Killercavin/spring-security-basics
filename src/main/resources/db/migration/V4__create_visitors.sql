CREATE TABLE visitors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(25) NOT NULL,
    site_id UUID NOT NULL REFERENCES sites(id),
    zone_id UUID REFERENCES zones(id),
    created_by UUID NOT NULL REFERENCES users(id),
    visit_status_id UUID NOT NULL REFERENCES visit_statuses(id),
    visitor_type VARCHAR(50) NOT NULL,
    purpose TEXT NOT NULL DEFAULT 'General Visit',
    check_in_time TIMESTAMPTZ NOT NULL DEFAULT now(),
    check_out_time TIMESTAMPTZ
);

CREATE INDEX idx_visitors_site_id ON visitors(site_id);
CREATE INDEX idx_visitors_phone ON visitors(phone);
CREATE INDEX idx_visitors_status ON visitors(visit_status_id);
CREATE INDEX idx_visitors_check_in_time ON visitors(check_in_time);