# GitHub Push Guide - MSA Upgraded Project

## Overview
This guide provides step-by-step instructions to push the upgraded microservices project to GitHub repository.

## Prerequisites

### Required Software
- **Git** (version 2.0+)
- **GitHub Account** with proper permissions
- **SSH Key or GitHub Personal Access Token** (recommended)
- **Command Line Interface** (Git Bash, PowerShell, or Terminal)

### GitHub Repository Setup
- **Repository Name**: `msa-upgraded` (recommended)
- **Visibility**: Public or Private (your choice)
- **Initialize**: Empty repository (no README, no .gitignore)

## Step-by-Step GitHub Push Process

### Step 1: Initialize Git Repository

```bash
# Navigate to project directory
cd E:\learning\workspaces\workspaces-0\msa-upgraded

# Initialize Git repository
git init

# Check Git status
git status
```

**Expected Output:**
```
Initialized empty Git repository in E:/learning/workspaces/workspaces-0/msa-upgraded/.git/
Untracked files:
  (use "git add <file>..." to include in what will be committed)
        pom.xml
        employee-service/
        employee-payroll-service/
        role-service/
        ...
```

### Step 2: Configure Git User (if not already configured)

```bash
# Set Git user name
git config user.name "Your Name"

# Set Git email (use your GitHub email)
git config user.email "your-email@example.com"

# Verify configuration
git config --list
```

### Step 3: Create .gitignore File

```bash
# Create .gitignore file
echo "# Maven
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Logs
*.log
logs/

# Temporary files
*.tmp
*.temp
*.bak
*.orig

# Node.js (if any)
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*

# Spring Boot
application-local.properties
application-dev.properties
application-prod.properties" > .gitignore

# Verify .gitignore
cat .gitignore
```

### Step 4: Add Files to Git

```bash
# Add all files
git add .

# Check staged files
git status

# Add specific files if needed
git add pom.xml
git add employee-service/
git add employee-payroll-service/
git add role-service/
git add eureka-naming-server/
git add spring-cloud-config-server/
git add zuul-edge-server/
git add zipkin-tracing/
git add *.md
```

### Step 5: Create Initial Commit

```bash
# Create initial commit
git commit -m "Initial commit: Spring Boot 3.2.5 microservices upgrade

- Upgraded Spring Boot from 1.5.2 to 3.2.5
- Migrated from javax to jakarta packages
- Replaced deprecated dependencies (Hystrix → Resilience4j, Zuul → Spring Cloud Gateway)
- Added Swagger/OpenAPI documentation
- Updated to Java 17
- Enhanced with modern Spring Cloud features
- Added comprehensive test coverage with JUnit 5 and Mockito
- Created documentation and guides

Features:
- Service discovery with Eureka
- Configuration management with Spring Cloud Config
- API gateway with Spring Cloud Gateway
- Circuit breaker patterns with Resilience4j
- Distributed tracing with Micrometer
- Comprehensive test suite
- Complete documentation"

# Verify commit
git log --oneline -1
```

### Step 6: Create GitHub Repository

**Option A: Using GitHub Web Interface**
1. Go to https://github.com/RaviGit18
2. Click "New repository"
3. Repository name: `msa-upgraded`
4. Description: `Upgraded microservices project with Spring Boot 3.2.5`
5. Choose Public/Private
6. **DO NOT** initialize with README, .gitignore, or license
7. Click "Create repository"

**Option B: Using GitHub CLI (if installed)**
```bash
# Create repository
gh repo create msa-upgraded --public --description "Upgraded microservices project with Spring Boot 3.2.5"

# Or private
gh repo create msa-upgraded --private --description "Upgraded microservices project with Spring Boot 3.2.5"
```

### Step 7: Add Remote Repository

```bash
# Add remote origin (replace with your actual repo URL)
git remote add origin https://github.com/RaviGit18/msa-upgraded.git

# Verify remote
git remote -v

# Alternative with SSH (recommended)
git remote set-url origin git@github.com:RaviGit18/msa-upgraded.git
```

### Step 8: Push to GitHub

```bash
# Push to main branch
git push -u origin main

# Or if using master branch
git push -u origin master

# Alternative: Force push (if needed)
git push -f origin main
```

**Expected Output:**
```
Enumerating objects: 150, done.
Counting objects: 100% (150/150), done.
Delta compression using up to 8 threads
Compressing objects: 100% (120/120), done.
Writing objects: 100% (150/150), 1.2 MiB | 2.5 MiB/s, done.
Total 150 (delta 50), reused 0 (delta 0), pack-reused 0
To https://github.com/RaviGit18/msa-upgraded.git
 * [new branch]      main -> main
Branch 'main' set up to track remote branch 'main' from 'origin'.
```

## Authentication Setup

### Option 1: HTTPS with Personal Access Token
```bash
# Generate Personal Access Token on GitHub
# Settings → Developer settings → Personal access tokens → Generate new token
# Select scopes: repo (full control)

# Use token as password
git push https://RaviGit18:YOUR_TOKEN@github.com/RaviGit18/msa-upgraded.git main
```

### Option 2: SSH Key Setup
```bash
# Generate SSH key (if not exists)
ssh-keygen -t rsa -b 4096 -C "your-email@example.com"

# Start SSH agent
eval "$(ssh-agent -s)"

# Add SSH key
ssh-add ~/.ssh/id_rsa

# Copy public key to GitHub
cat ~/.ssh/id_rsa.pub

# Test SSH connection
ssh -T git@github.com

# Use SSH remote
git remote set-url origin git@github.com:RaviGit18/msa-upgraded.git
```

## Verification Steps

### Step 9: Verify Repository on GitHub

1. **Visit Repository**: https://github.com/RaviGit18/msa-upgraded
2. **Check Files**: All project files should be visible
3. **Verify README**: If you have one, it should display
4. **Check Branches**: Main branch should be default
5. **Verify Size**: Repository should show correct file sizes

### Step 10: Clone Test (Optional)

```bash
# Navigate to test directory
cd E:\learning\workspaces\workspaces-0

# Clone repository
git clone https://github.com/RaviGit18/msa-upgraded.git msa-upgraded-test

# Navigate to cloned repo
cd msa-upgraded-test

# Verify files
dir /B

# Test build
mvn clean compile

# Clean up
cd ..
rm -rf msa-upgraded-test
```

## Advanced Git Operations

### Branch Management
```bash
# Create feature branch
git checkout -b feature/add-documentation

# Switch branches
git checkout main
git checkout feature/add-documentation

# Merge branch
git checkout main
git merge feature/add-documentation

# Push branch
git push origin feature/add-documentation
```

### Tag Management
```bash
# Create tag for version
git tag -a v1.0.0 -m "Spring Boot 3.2.5 upgrade release"

# Push tags
git push origin --tags

# List tags
git tag -l
```

### Commit History Management
```bash
# View commit history
git log --oneline --graph --decorate

# View changes
git show HEAD

# Amend last commit (if needed)
git commit --amend -m "Updated commit message"

# Reset to previous commit (if needed)
git reset --hard HEAD~1
```

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: Authentication Failed
**Error**: `remote: Invalid username or password`
**Solution**:
```bash
# Use Personal Access Token
git config credential.helper store
git push origin main
# Enter username and token as password
```

#### Issue 2: Permission Denied
**Error**: `Permission denied (publickey)`
**Solution**:
```bash
# Setup SSH keys properly
ssh-keygen -t rsa -b 4096
# Add public key to GitHub SSH settings
ssh-add ~/.ssh/id_rsa
```

#### Issue 3: Repository Not Found
**Error**: `ERROR: Repository not found`
**Solution**:
```bash
# Verify repository URL
git remote -v

# Update remote URL
git remote set-url origin https://github.com/RaviGit18/msa-upgraded.git
```

#### Issue 4: Large File Push Failed
**Error**: `remote: error: GH001: Large files detected`
**Solution**:
```bash
# Check for large files
find . -type f -size +100M

# Use Git LFS for large files
git lfs track "*.jar"
git lfs track "*.zip"
git add .gitattributes
git commit -m "Configure Git LFS"
```

#### Issue 5: Merge Conflicts
**Error**: Merge conflicts during pull
**Solution**:
```bash
# Stash local changes
git stash

# Pull latest changes
git pull origin main

# Apply stashed changes
git stash pop

# Resolve conflicts manually
git add .
git commit -m "Resolve merge conflicts"
```

## Best Practices

### Commit Message Guidelines
```bash
# Good commit messages
git commit -m "feat: Add Spring Cloud Gateway configuration"
git commit -m "fix: Resolve H2 database connection issue"
git commit -m "docs: Update API documentation"
git commit -m "test: Add comprehensive unit tests"
git commit -m "refactor: Upgrade Resilience4j dependency"

# Bad commit messages
git commit -m "fixed stuff"
git commit -m "update"
git commit -m "wip"
```

### Branch Strategy
```bash
# Main branch: main (production-ready code)
# Develop branch: develop (integration branch)
# Feature branches: feature/feature-name
# Hotfix branches: hotfix/issue-description
# Release branches: release/version-number
```

### Repository Structure
```
msa-upgraded/
├── README.md                    # Project overview
├── CONTRIBUTING.md              # Contribution guidelines
├── LICENSE                     # License file
├── .gitignore                  # Git ignore rules
├── pom.xml                     # Parent POM
├── docs/                       # Documentation
│   ├── RUN_AND_TEST_GUIDE.md
│   ├── JUNIT_TEST_GUIDE.md
│   └── MICROSERVICES_FLOW_DIAGRAM.md
├── employee-service/             # Employee microservice
├── employee-payroll-service/     # Payroll microservice
├── role-service/               # Role microservice
├── eureka-naming-server/       # Discovery server
├── spring-cloud-config-server/   # Config server
├── zuul-edge-server/           # API gateway
└── zipkin-tracing/            # Tracing service
```

## GitHub Actions Integration (Optional)

### Create CI/CD Pipeline
```yaml
# .github/workflows/ci.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn test
    - name: Build project
      run: mvn clean package
```

## Repository Settings Configuration

### Recommended GitHub Settings
1. **Branch Protection Rules**:
   - Require pull request reviews
   - Require status checks to pass
   - Include administrators

2. **Security Settings**:
   - Enable vulnerability alerts
   - Enable dependency graph
   - Enable code scanning

3. **Collaborators**:
   - Add team members with appropriate permissions
   - Set up code owners

## Post-Push Checklist

### ✅ Verification Items
- [ ] Repository accessible at https://github.com/RaviGit18/msa-upgraded
- [ ] All files uploaded correctly
- [ ] README.md displays properly
- [ ] Project builds successfully from clone
- [ ] Documentation files are accessible
- [ ] Git history shows commits correctly
- [ ] Branches and tags are pushed
- [ ] Repository settings configured

### 📝 Next Steps
- [ ] Add README.md with project overview
- [ ] Set up GitHub Pages for documentation
- [ ] Configure GitHub Actions for CI/CD
- [ ] Add issue templates
- [ ] Set up project boards
- [ ] Create release tags
- [ ] Invite collaborators

This comprehensive guide ensures successful GitHub push of the upgraded MSA project with proper version control practices.
