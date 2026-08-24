# Jenkins Password Configuration

## Security Notice

Jenkins user passwords are no longer hardcoded in the repository. Instead, they are configured via environment variables at container runtime.

## Configuration

### Option 1: Using Environment Variables (Recommended for Production)

Set the following environment variables before starting the containers:

```bash
export ADMIN_PASSWORD="your-secure-admin-password"
export ALICE_PASSWORD="your-secure-alice-password"
export KNAVE_PASSWORD="your-secure-knave-password"
docker-compose up -d
```

### Option 2: Using .env File (Recommended for Development)

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and set secure passwords:
   ```
   ADMIN_PASSWORD=your-secure-admin-password
   ALICE_PASSWORD=your-secure-alice-password
   KNAVE_PASSWORD=your-secure-knave-password
   ```

3. Start the containers:
   ```bash
   docker-compose up -d
   ```

### Default Behavior

If no passwords are provided, the system will use placeholder values (`CHANGE_ME_*`). **These should be changed immediately in production environments.**

## User Accounts

- **admin**: Administrator account with `Overall/Administer` permissions
- **alice**: User account with build and job permissions
- **knave**: User account with agent management permissions

## Security Best Practices

1. Always set strong, unique passwords for each account
2. Never commit the `.env` file to version control (it's already in `.gitignore`)
3. Use Docker secrets or a secrets management system in production
4. Rotate passwords regularly
5. Monitor Jenkins access logs for unauthorized access attempts
