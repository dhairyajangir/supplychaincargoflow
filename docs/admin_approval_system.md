# Admin-Based Operator Approval System

This document summarizes the role-based approval flow used to control operator access to the system.

## Roles
- `ADMIN`: approves or rejects new operator accounts
- `OPERATOR`: gains access after approval
- `PENDING`: new users awaiting approval

## Backend Changes
### Auth Service
- New users are created with `role = PENDING` and `isActive = false`
- Pending users can be queried for admin review
- Approved users are promoted to `OPERATOR`
- Rejected users are removed

### OAuth2 Login Handler
- OAuth users also enter the system as `PENDING`

### User Repository
- Added role-based lookup for pending-user queries

### Admin Controller
- Added endpoints for listing, approving, and rejecting users
- Admin access is restricted to authenticated users with the `ADMIN` role

### Security Configuration
- Authentication endpoints remain public
- Admin endpoints are protected
- General application routes require authentication

### Auth Controller
- Login responses indicate when an account is still pending approval
- Signup responses tell users that approval is required

## Frontend Changes
### Admin Panel
- Added a dedicated page for reviewing pending users
- Supports approve and reject actions

### Router
- Added `/admin`
- Redirects admin users to the admin page
- Redirects approved operators to the dashboard

### Login Flow
- Shows pending-account messaging when required
- Routes admin users to the admin page after login

## Database Notes
- No schema changes are required for the approval flow
- The existing `users` table already supports the required role and activation fields

## Workflow
### New User Signup
1. User signs up.
2. Account is created as pending.
3. User cannot log in until approved.

### Admin Approval
1. Admin logs in.
2. Admin reviews the pending list.
3. Admin approves or rejects the account.

### Approved Login
1. Approved user logs in.
2. User lands on the dashboard.

## Testing Checklist
- Verify pending accounts are created correctly
- Verify pending users are visible to admins
- Verify approval updates the role and activation state
- Verify rejected users are removed
- Verify approved users can log in normally

---