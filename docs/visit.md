# Visitor Map

## Key terminologies
- **VisitorProfile**: A site's representation of a visitor identity
- **Visit**: One occurrence of a visitor entering/leaving a site

## Model representation

```                      
                    Site
                     │
                     │
              ┌──────▼──────┐
              │VisitorProfile│
              │              │
              │ name         │
              │ phone        │
              └──────┬───────┘
                     │
                     │ 1:N
                     ▼
                  ┌─────┐
                  │Visit│
                  ├─────┤
                  │zone │
                  │type │
                  │purpose
                  │status
                  │creator
                  │check-in
                  │check-out
                  └─────┘
```
> [!NOTE]
> - a profile belongs to exactly one site
> - naturally phone numbers change, or get recycled

## Rules: the core invariant
- A VisitorProfile may have many historical visits at a site, but may have at most one active visit at that site.

## Visit registration flow

```
Register Visit
      │
      ▼
Find VisitorProfile by
(site, phone)
      │
      ├── not found ──→ create profile
      │
      ▼
Check active Visit (VisitStatus == "CHECKED_IN/OVERDUE") # visitor must be checked out on the site to create a new visit
      │
      ├── exists ─────→ 409 Conflict
      │
      ▼
Create Visit

```

