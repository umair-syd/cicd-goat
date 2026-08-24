# Security Fix: Hardcoded Password Removal

## Issue
Previously, Jenkins local user account passwords were hardcoded directly in the `jenkins-server/jenkins.yaml` configuration file and committed to the repository. This created a security vulnerability where anyone with access to the repository could authenticate to Jenkins using these fixed credentials.

## Resolution
The following changes have been implemented to address this security issue:

### 1. Configuration Changes
- **jenkins-server/jenkins.yaml**: User passwords now reference environment variables (`${ADMIN_PASSWORD}`, `${ALICE_PASSWORD}`, `${KNAVE_PASSWORD}`) instead of hardcoded values
- **jenkins-server/secrets.properties**: Updated to use placeholder values that are replaced at runtime
- **jenkins-server/generate-secrets.sh**: New script that generates the secrets.properties file from environment variables at container startup
- **jenkins-server/Dockerfile**: Modified to copy and execute the generate-secrets.sh script instead of copying a static secrets.properties file
- **jenkins-server/cleanup**: Updated to call generate-secrets.sh before starting Jenkins
- **docker-compose.yaml**: Added environment variable configuration for user passwords with secure defaults

### 2. Runtime Password Configuration
Passwords are now configured via environment variables that can be set:
- Through a `.env` file (not committed to the repository)
- Via environment variables passed to docker-compose
- With default placeholder values if not specified (which should be changed in production)

### 3. Documentation
- **JENKINS_PASSWORD_CONFIG.md**: Comprehensive guide on how to configure passwords securely
- **.env.example**: Template file showing the required environment variables

## Security Benefits
1. **No Hardcoded Credentials**: Passwords are no longer stored in version control
2. **Environment-Specific Configuration**: Each deployment can use unique passwords
3. **Separation of Concerns**: Configuration is separated from code
4. **Audit Trail**: Password changes don't require code commits
5. **Compliance**: Aligns with security best practices for credential management

## Migration Guide
For existing deployments:
1. Copy `.env.example` to `.env`
2. Set secure passwords in the `.env` file
3. Rebuild and restart the Jenkins container
4. Verify that authentication works with the new passwords

## Related Files
- `jenkins-server/jenkins.yaml` (lines 137-146)
- `jenkins-server/secrets.properties`
- `jenkins-server/generate-secrets.sh`
- `jenkins-server/Dockerfile`
- `jenkins-server/cleanup`
- `docker-compose.yaml`
- `.env.example`
- `JENKINS_PASSWORD_CONFIG.md`
