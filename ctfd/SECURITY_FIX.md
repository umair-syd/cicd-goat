# CTFd Secret Key Security Fix

## Issue
The repository previously contained a tracked `.ctfd_secret_key` file that was copied into Docker images. This created a critical security vulnerability where all deployments shared the same signing key, enabling cross-deployment attacks.

## Fix Applied
1. **Dockerfile updated**: Added `RUN rm -f /opt/CTFd/.ctfd_secret_key` to remove any tracked secret key from the image
2. **Docker ignore added**: Created `.dockerignore` to prevent `.ctfd_secret_key` files from being copied during builds
3. **Gitignore updated**: Added `.ctfd_secret_key` patterns to `.gitignore` to prevent future commits
4. **Repository cleanup required**: The tracked file `ctfd/data/CTFd/.ctfd_secret_key` must be manually removed from the repository and git history

## Manual Steps Required

### Remove the tracked secret key file
```bash
# Remove the file from the repository
rm ctfd/data/CTFd/.ctfd_secret_key

# Commit the removal
git add ctfd/data/CTFd/.ctfd_secret_key
git commit -m "Remove tracked CTFd secret key file"

# Optional but recommended: Remove from git history
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch ctfd/data/CTFd/.ctfd_secret_key' \
  --prune-empty --tag-name-filter cat -- --all
```

## For Operators
After deploying images built with this fix:

1. **Set SECRET_KEY environment variable**: Provide a unique secret key via the `SECRET_KEY` environment variable
   ```bash
   docker run -e SECRET_KEY="$(openssl rand -hex 32)" ...
   ```

2. **Or let CTFd generate one**: If no `SECRET_KEY` is provided, CTFd will generate a new random key at runtime and store it in `.ctfd_secret_key` within the container

3. **For multi-worker deployments**: You MUST set the `SECRET_KEY` environment variable or mount a shared `.ctfd_secret_key` file

## Security Impact
- **Before fix**: All deployments shared the same secret key, allowing attackers to forge session tokens and password reset tokens across any deployment
- **After fix**: Each deployment uses a unique secret key, isolating security boundaries between instances

## Verification
To verify the fix is applied:
```bash
# Build the image
docker build -t ctfd-test ./ctfd

# Check that .ctfd_secret_key is not present in the image
docker run --rm ctfd-test ls -la /opt/CTFd/.ctfd_secret_key
# Should output: "No such file or directory"
```
