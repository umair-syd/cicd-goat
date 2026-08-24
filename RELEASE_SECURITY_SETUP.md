# Release Security Setup

## Overview
The CircleCI release workflow has been secured with token-based authorization to prevent unauthorized releases.

## Security Issue (Fixed)
Previously, any user with CircleCI pipeline creation permissions could trigger the release workflow by providing a non-empty VERSION parameter. This allowed unauthorized users to:
- Push malicious images to official Docker Hub repositories
- Overwrite existing tags including `latest`
- Compromise the supply chain

## Solution
The release workflow now requires a secret token (`RELEASE_TOKEN`) that must match a value stored in CircleCI project environment variables (`RELEASE_TOKEN_SECRET`). Only the authorized GitHub release workflow has access to this token.

## Required Setup

### 1. Generate a Secure Token
Generate a cryptographically secure random token:
```bash
openssl rand -hex 32
```

### 2. Configure CircleCI Environment Variable
1. Go to CircleCI project settings
2. Navigate to "Environment Variables"
3. Add a new environment variable:
   - Name: `RELEASE_TOKEN_SECRET`
   - Value: The token generated in step 1

### 3. Configure GitHub Secret
1. Go to GitHub repository settings
2. Navigate to "Secrets and variables" → "Actions"
3. Add a new repository secret:
   - Name: `RELEASE_TOKEN`
   - Value: The same token from step 1

## How It Works

1. When a GitHub release is published, the GitHub Actions workflow triggers
2. The workflow calls the CircleCI API with both `VERSION` and `RELEASE_TOKEN` parameters
3. The CircleCI release job validates that `RELEASE_TOKEN` matches `RELEASE_TOKEN_SECRET`
4. If validation fails, the job exits immediately with an error
5. If validation succeeds, the release proceeds normally

## Security Benefits

- **Authorization boundary**: Only pipelines with the correct token can run releases
- **Fail-fast**: Unauthorized attempts are rejected before any sensitive operations
- **Audit trail**: Failed authorization attempts are logged in CircleCI
- **Defense in depth**: Even if someone can create pipelines, they cannot release without the token

## Testing

To test that the security is working:

1. **Valid release** (should succeed):
   - Create and publish a GitHub release
   - The GitHub Actions workflow will trigger CircleCI with the correct token
   - The release should complete successfully

2. **Invalid release** (should fail):
   - Try to trigger a CircleCI pipeline directly via API without the token
   - The release job should fail with "Unauthorized release attempt" error
