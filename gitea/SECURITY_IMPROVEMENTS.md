# Security Improvements for Gitea Configuration

## Changes Made

This patch addresses the security vulnerability where plaintext passwords were committed to the repository and embedded in Docker images.

### 1. Password Handling
- **gitea.yaml**: Replaced all plaintext passwords with environment variable placeholders (e.g., `${JENKINS_PASSWORD}`)
- **Automatic Generation**: If passwords are not provided or are placeholders, the system now generates cryptographically secure random passwords at runtime
- **Force Password Change**: All users are now created with `must_change_password: true` by default

### 2. Docker Image Security
- **gitea.yaml excluded**: Added `gitea.yaml` to `.dockerignore` to prevent credential-bearing configuration from being embedded in Docker images
- **Template provided**: Created `gitea.yaml.template` as a reference for configuration structure

### 3. Code Changes
- **giteacasc/__init__.py**: 
  - Added `generate_random_password()` function using `secrets` module for cryptographically secure password generation
  - Modified user creation logic to generate random passwords when not provided or when placeholders are detected
  - Ensures `must_change_password` defaults to `True`
  
- **giteacasc/gitea.py**:
  - Changed `create_user()` method default parameter from `must_change_password=False` to `must_change_password=True`
  - This forces all provisioned users to change their password on first login

## Usage

### Option 1: Environment Variables (Recommended for Production)
Set environment variables before running:
```bash
export JENKINS_PASSWORD="your-secure-password"
export THEALICE_PASSWORD="your-secure-password"
export JENKINS_HATTER_PASSWORD="your-secure-password"
export MOCK_TURTLE_CI_PASSWORD="your-secure-password"
export JENKINS_CATERPILLAR_PASSWORD="your-secure-password"
```

### Option 2: Random Password Generation (Development/Testing)
Leave passwords as placeholders or omit them entirely. The system will:
1. Generate cryptographically secure random passwords
2. Create users with these passwords
3. Force users to change passwords on first login

### Option 3: Runtime Configuration
Provide a gitea.yaml file at runtime (not embedded in the image) with actual passwords, ensuring:
- The file is not committed to version control
- The file is not copied into Docker images
- Users are still required to change passwords on first login

## Security Benefits

1. **No Credential Exposure**: Plaintext passwords are no longer committed to the repository
2. **Image Security**: Docker images no longer contain embedded credentials
3. **Forced Password Rotation**: Users must change their initial passwords on first login
4. **Cryptographically Secure**: Random passwords use Python's `secrets` module (CSPRNG)
5. **Defense in Depth**: Multiple layers of protection against credential exposure

## Migration Notes

- Existing deployments should rotate all user passwords
- Previously built images should be rebuilt to exclude embedded credentials
- Update deployment scripts to provide passwords via environment variables or accept random generation

## Additional Security Considerations

While this patch addresses the primary vulnerability in Gitea configuration, note that other files in the repository may also contain credentials:

- **tests/conftest.py**: Contains hardcoded test credentials for `thealice` user
- **jenkins-server/jobdsl/mad-hatter.groovy**: Contains hardcoded Jenkins credentials
- **README.md**: Documents default credentials for testing

These files serve different purposes (testing, Jenkins configuration, documentation) and may require separate remediation strategies depending on your security requirements. For production deployments, ensure these are also addressed appropriately.
