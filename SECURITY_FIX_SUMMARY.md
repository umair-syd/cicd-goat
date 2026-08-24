# Security Fix: CI Supply-Chain Access Control Issue

## Vulnerability Summary
Write-capable CI identities (`jenkins_caterpillar` and `mock-turtle-ci`) had write or merge permissions on repositories that Jenkins automatically discovers and executes. This allowed potential arbitrary code execution on Jenkins agents through malicious Jenkinsfile modifications.

## Root Cause
1. **jenkins_caterpillar**: Member of Maintainers team with write access to `caterpillar` repository, which Jenkins scans via multibranch pipeline
2. **mock-turtle-ci**: Had write access and merge whitelist permission on `mock-turtle` repository's main branch
3. **Static passwords**: All CI account passwords were committed in plaintext in gitea.yaml
4. **No branch protection**: The `caterpillar` repository had no branch protection rules

## Changes Applied

### 1. Removed jenkins_caterpillar from Maintainers Team
- **File**: gitea/gitea.yaml, lines 27-31
- **Change**: Removed `jenkins_caterpillar` from the Maintainers team members list
- **Rationale**: CI identities must not have write access to repositories they execute

### 2. Downgraded jenkins_caterpillar to Read-Only Access
- **File**: gitea/gitea.yaml, lines 42-47
- **Change**: Added explicit read-only collaborator permission for `jenkins_caterpillar` on `caterpillar` repository
- **Rationale**: Jenkins only needs read access to checkout and execute code, not modify it

### 3. Added Branch Protection to caterpillar Repository
- **File**: gitea/gitea.yaml, lines 48-51
- **Change**: Added branch protection requiring 1 approval for main branch
- **Rationale**: Prevents direct pushes to main branch, requiring code review

### 4. Downgraded mock-turtle-ci to Read-Only Access
- **File**: gitea/gitea.yaml, lines 101-106
- **Change**: Changed `mock-turtle-ci` permission from write to read on `mock-turtle` repository
- **Rationale**: CI identities must not have write access to repositories they execute

### 5. Removed mock-turtle-ci from Merge Whitelist
- **File**: gitea/gitea.yaml, lines 107-113
- **Change**: Removed `mock-turtle-ci` from merge_whitelist_usernames and added required_approvals
- **Rationale**: CI identities must not be able to merge code that they will subsequently execute

### 6. Rotated Static Passwords
- **File**: gitea/gitea.yaml, lines 1-23
- **Change**: Replaced all static passwords and tokens with placeholder values
- **Rationale**: Previously exposed credentials must be rotated immediately

## Security Principle Applied
**Principle of Least Privilege**: CI service accounts should only have the minimum permissions necessary to perform their function (read-only access to checkout code). They should never have write access or merge permissions on repositories whose code they execute, as this creates a privilege escalation path from repository modification to arbitrary code execution on the CI infrastructure.

## Verification Steps
1. Verify `jenkins_caterpillar` can no longer push to any branch in `caterpillar` repository
2. Verify `mock-turtle-ci` can no longer push to or merge into `mock-turtle` repository
3. Verify both CI accounts can still checkout code for Jenkins execution
4. Verify branch protection rules require human approval before merging to main branches
5. Rotate all passwords and tokens to new secure values not committed to version control

## Impact
- CI pipelines will continue to function normally (read access preserved)
- Code changes now require human review and approval before execution
- Compromised CI credentials can no longer be used to inject malicious code
