FROM jenkins/jenkins:2.332.1-jdk11
ARG COMMIT_SHA
ARG VERSION

LABEL org.opencontainers.image.vendor="Cider Security" \
    org.opencontainers.image.title="CI/CD Goat - Jenkins server" \
    org.opencontainers.image.description="Deliberately vulnerable CI/CD environment." \
    org.opencontainers.image.url="https://hub.docker.com/r/cidersecurity/goat-jenkins-server" \
    org.opencontainers.image.source="https://github.com/cider-security-research/cicd-goat" \
    org.opencontainers.image.licenses="Apache-2.0" \
    org.opencontainers.image.version=$VERSION \
    org.opencontainers.image.revision=$COMMIT_SHA


ENV JAVA_OPTS "-Djenkins.install.runSetupWizard=false"
ENV SECRETS_FILE /var/jenkins_home/secrets.properties

COPY --chown=jenkins:jenkins flag5.txt /var/jenkins_home/flag5.txt
COPY --chown=jenkins:jenkins cleanup /var/jenkins_home/cleanup
COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --clean-download-directory -f /usr/share/jenkins/ref/plugins.txt && \
    chmod 755 /var/jenkins_home/cleanup
# Security: secrets.properties, jenkins.yaml, and jobdsl/ must be mounted at runtime
# as volumes or secrets to prevent credential exposure in image layers.
# These files should NOT be copied into the image during build.
# Expected mount paths:
#   - /var/jenkins_home/secrets.properties
#   - /var/jenkins_home/jenkins.yaml
#   - /var/jenkins_home/jobdsl/
ENTRYPOINT ["/var/jenkins_home/cleanup"]
