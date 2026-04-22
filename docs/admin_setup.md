# Admin User Setup Instructions

This guide explains how to create and manage the admin user for the cargo management system.

## Roles
- `ADMIN`: can approve or reject pending operators
- `OPERATOR`: can access the application after approval
- `PENDING`: new users waiting for approval

## Create the Admin User

### Option 1: SQL Script
1. Connect to the PostgreSQL database.
2. Run the provided admin creation script.

### Option 2: Direct SQL
```sql
INSERT INTO users (email, password, name, provider, role, is_active, picture, provider_id, created_at, updated_at)
VALUES (
  'your-admin-email@example.com',
  'your-bcrypt-password-hash',
  'System Administrator',
  'local',
  'ADMIN',
  true,
  NULL,
  NULL,
  NOW(),
  NOW()
)
ON CONFLICT (email) DO UPDATE SET
  role = 'ADMIN',
  is_active = true,
  updated_at = NOW();
```

Generate a hash with:

```bash
java PasswordHashGenerator "your-strong-admin-password"
```

## Workflow
### New User Signup
1. User signs up through the login page.
2. The account is created as `PENDING` and inactive.
3. The user waits for approval before logging in.

### Admin Approval
1. Admin logs in with the admin account.
2. Admin is redirected to `/admin`.
3. Admin reviews pending users and approves or rejects them.

### Approved User Login
1. Approved user logs in normally.
2. User is redirected to `/dashboard`.
3. Full application access is granted.

## API Endpoints
### Admin Endpoints
- `GET /api/admin/pending-users`
- `GET /api/admin/all-users`
- `POST /api/admin/approve-user/:userId`
- `DELETE /api/admin/reject-user/:userId`

### Authentication Endpoints
- `POST /api/auth/signup`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/user`

## Troubleshooting
- Confirm the user role is `ADMIN` if `/admin` access fails.
- Confirm the user role is `PENDING` if a new user does not appear in the admin panel.
- Confirm `is_active = true` if an approved user still cannot log in.

## Security Notes
- Use a unique, strong admin password.
- Keep passwords hashed.
- Protect admin endpoints through authentication and role checks.

---
