# CTFd Docker Image

This directory contains the Dockerfile and configuration for the CTFd component of the CI/CD Goat project.

## Security Note

**Important**: This image has been hardened to prevent secret key disclosure. The following protections are in place:

1. **Dockerfile removes tracked secrets**: Any `.ctfd_secret_key` file copied during the build is explicitly removed
2. **Docker ignore**: `.dockerignore` prevents secret key files from being included in the build context
3. **Gitignore**: Root `.gitignore` prevents accidental commits of secret key files

## Deployment

When deploying CTFd, you should provide a unique `SECRET_KEY` environment variable:

```bash
docker run -e SECRET_KEY="$(openssl rand -hex 32)" cidersecurity/goat-ctfd:latest
```

If no `SECRET_KEY` is provided, CTFd will generate a random key at runtime. For multi-worker deployments, you **must** provide a `SECRET_KEY` environment variable or mount a shared `.ctfd_secret_key` file.

## Building

```bash
docker build -t cidersecurity/goat-ctfd:latest .
```

The build process will automatically remove any tracked secret key files to ensure the image does not contain hardcoded secrets.

## See Also

- [SECURITY_FIX.md](./SECURITY_FIX.md) - Detailed information about the security fix applied to this image
