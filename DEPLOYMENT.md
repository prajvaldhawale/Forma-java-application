# Deployment

The Ansible playbook deploys the Spring Boot JAR to the two EC2 instances discovered by `ansible/inventory/aws_ec2.yml` (`Name=web1` and `Name=web2`). It installs the Java 21 runtime, creates a restricted service account, installs a systemd unit, restarts the storefront, and checks its HTTP health on port 8080.

The controller (the master EC2 instance) needs the `amazon.aws` collection, an instance profile that allows `ec2:DescribeInstances`, SSH access to both hosts, and the target key at `/home/ubuntu/Josh.pem`.

## Deploy manually from the master

```bash
mvn --batch-mode clean verify
ansible-galaxy collection install amazon.aws
ansible-playbook -i ansible/inventory/aws_ec2.yml ansible/deploy.yml \
  -e "artifact_path=$PWD/target/ecom-proj-0.1.0-SNAPSHOT.jar"
```

The application listens on port 8080. After deployment, browse to `http://<host-public-ip>:8080`. The current EC2 security group allows that port range; review and restrict its broad inbound rule before using this outside a demo environment.

## GitHub Actions setup

The workflow in `.github/workflows/deploy.yml` runs on pushes to `main` and on manual dispatch. It tests/packages on a GitHub-hosted runner, then SSHes to the master and asks that master to deploy to the private EC2 addresses.

Add these GitHub Actions repository secrets:

- `DEPLOY_HOST`: the master instance's public DNS name or a stable Elastic IP.
- `DEPLOY_USER`: `ubuntu` (optional; defaults to `ubuntu`).
- `DEPLOY_SSH_PRIVATE_KEY`: private key that can SSH to the master. Store the key as a secret; never commit it.
- `DEPLOY_KNOWN_HOSTS`: the verified `known_hosts` line for the master. Obtain its host key through a trusted channel and verify its fingerprint before saving it.

Push this repository to a GitHub repository and configure the secrets before the workflow can deploy. The master must remain reachable on SSH port 22 from GitHub-hosted runners. Use GitHub Actions environment protections if deployments require approval.