#!/bin/sh
# Generate secrets.properties from environment variables
cat > /var/jenkins_home/secrets.properties << EOF
AGENT_PRIVATE_KEY = ${AGENT_PRIVATE_KEY}
FLAG1 = ${FLAG1}
GITEA_ACCESS_TOKEN = ${GITEA_ACCESS_TOKEN}
FLAG8 = ${FLAG8}
ADMIN_PASSWORD = ${ADMIN_PASSWORD}
ALICE_PASSWORD = ${ALICE_PASSWORD}
KNAVE_PASSWORD = ${KNAVE_PASSWORD}
EOF
