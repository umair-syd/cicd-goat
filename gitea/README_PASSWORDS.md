# Gitea Password Security - Quick Reference

## What Changed?

Plaintext passwords have been removed from the repository and Docker images for security.

## How to Use

### For Development/Testing (Easiest)
Just run as normal. The system will:
- Detect placeholder passwords (e.g., `${JENKINS_PASSWORD}`)
- Generate secure random passwords automatically
- Force users to change passwords on first login

### For Production (Recommended)
Set environment variables before deployment:

```bash
export JENKINS_PASSWORD="YourSecurePassword123!"
export THEALICE_PASSWORD="YourSecurePassword456!"
export JENKINS_HATTER_PASSWORD="YourSecurePassword789!"
export MOCK_TURTLE_CI_PASSWORD="YourSecurePassword012!"
export JENKINS_CATERPILLAR_PASSWORD="YourSecurePassword345!"
```

Then the system will:
- Use your provided passwords
- Still force users to change passwords on first login

## Security Features

✅ No plaintext passwords in git repository  
✅ No passwords embedded in Docker images  
✅ Cryptographically secure random password generation  
✅ Mandatory password change on first login  
✅ Environment variable support for production  

## Files Modified

- `gitea.yaml` - Passwords replaced with placeholders
- `.dockerignore` - Excludes gitea.yaml from Docker images
- `giteacasc/__init__.py` - Added random password generation
- `giteacasc/gitea.py` - Changed default to require password change
- `Dockerfile` - Added comment about exclusion

## Files Added

- `gitea.yaml.template` - Template for reference
- `SECURITY_IMPROVEMENTS.md` - Detailed documentation
- `README_PASSWORDS.md` - This file
