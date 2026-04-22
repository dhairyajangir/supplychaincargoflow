# High-Fidelity UI

This document describes the polished design direction derived from the wireframes.

## Reference
- Low-fidelity source: `docs/ui_wireframes.md`

## Design System
- Typography: consistent text hierarchy across all pages
- Color roles: primary, success, warning, danger, and neutral
- Spacing: 8px grid
- Components: header, sidebar, page container, data table, form modal, status badge, toast, tabs, and chips

## Page Coverage
### Dashboard
- Metric cards with clear hierarchy
- Recent activity shown as a timeline
- Quick actions aligned with common tasks

### Shipment Tracking
- Status chips with counts
- Compact table layout
- Inline or modal status updates

### Cargo Management
- Searchable table layout
- Modal-based create and edit flow
- Validation for required and numeric fields

### Route Management
- Search, filter, and sort controls
- Normalized route form
- Clear status selector

### Vendor Coordination
- Card or table-based vendor directory
- Assignment flow for shipments
- Shared action patterns with the other modules

## Implementation Notes
- Keep page actions in a consistent toolbar location
- Preserve table state when navigating
- Use modal forms for create and edit actions
- Keep destructive actions behind confirmation dialogs

## Next Steps
- Implement shared layout components
- Connect the pages to backend endpoints
- Add tests for form validation and table behavior

---