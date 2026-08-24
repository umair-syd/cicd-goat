# Jenkins Server Security Configuration

## Security Fix: Credential Exposure in Image Layers

### Issue
Previously, sensitive configuration files (`secrets.properties`, `jenkins.yaml`, and `jobdsl/`) were copied directly into the Docker image during build time. This caused credentials to be embedded in immutable Docker image layers, making them recoverable by anyone with access to:
- The published Docker image
- The Docker build cache
- The source repository

### Solution
The sensitive files are now mounted at runtime as read-only volumes instead of being copied into the image layers. This ensures:
1. Credentials are never embedded in Docker image layers
2. Credentials are not distributed via Docker registries or build caches
3. The image can be safely published without exposing secrets

### Deployment Requirements

When deploying the Jenkins server, you **must** mount the following files as volumes:

```yaml
volumes:
  - ./jenkins-server/secrets.properties:/var/jenkins_home/secrets.properties:ro
  - ./jenkins-server/jenkins.yaml:/var/jenkins_home/jenkins.yaml:ro
  - ./jenkins-server/jobdsl:/var/jenkins_home/jobdsl:ro
```

The `:ro` flag mounts these files as read-only, providing an additional security layer.

### File Descriptions

- **secrets.properties**: Contains sensitive credentials including SSH private keys, API tokens, and flags
- **jenkins.yaml**: Contains Jenkins Configuration as Code (JCasC) including user passwords and system configuration
- **jobdsl/**: Contains Job DSL scripts that reference credential IDs

### Verification

To verify that credentials are not embedded in the image:

```bash
# Build the image
docker build -t jenkins-test ./jenkins-server

# Inspect image layers - sensitive files should NOT appear in COPY commands
docker history jenkins-test

# Try to extract files from the image (should fail)
docker run --rm jenkins-test cat /var/jenkins_home/secrets.properties
# This should fail or return empty since files are not in the image
```

### Production Deployment

For production deployments, consider:
1. Using Docker secrets or Kubernetes secrets instead of volume mounts
2. Generating credentials dynamically at startup
3. Using external secret management systems (HashiCorp Vault, AWS Secrets Manager, etc.)
4. Rotating credentials regularly
5. Using environment variable substitution in configuration files

### Development vs Production

- **Development** (docker-compose-dev.yaml): Mounts local files for easy testing
- **Production** (docker-compose.yaml): Should use proper secret management solutions

This is a deliberately vulnerable training environment (CI/CD Goat), but the security fix demonstrates proper secret handling practices.
