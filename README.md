# Forma Electronics

Forma Electronics is a Java 21 and Spring Boot storefront demo for electronics. It includes a searchable, filterable product catalog, session-backed shopping bag, quantity controls, and a validated demo checkout. Checkout does not process payments or persist orders.

## Requirements

- Java 21 JDK and Maven 3.9 or newer to build the application.
- Ansible Core and the `amazon.aws` collection on the deployment controller (the master EC2 instance).
- AWS credentials on the controller with permission to describe EC2 instances. An attached instance profile is recommended.
- SSH access from the controller to both web hosts, using an Ubuntu login and private key.

## Run locally

From the project root:

```bash
mvn spring-boot:run
```

Open <http://localhost:8080>. Run the unit tests and build the deployable JAR with:

```bash
mvn clean verify
```

The artifact is `target/ecom-proj-0.1.0-SNAPSHOT.jar`. Product photos and display fonts come from external CDNs; an internet connection is needed for those assets.

## How Ansible finds the hosts

The dynamic inventory is [ansible/inventory/aws_ec2.yml](ansible/inventory/aws_ec2.yml). It queries `ap-south-1` and includes running EC2 instances tagged `Environment=dev` and `Role=web`. The EC2 `Name` tag becomes an Ansible group: `Name=web1` becomes `name_web1`, and `Name=web2` becomes `name_web2`. Instance private IPs are used for SSH.

Make sure each target instance has all three tags:

| Tag key | Value |
| --- | --- |
| `Name` | `web1` or `web2` |
| `Environment` | `dev` |
| `Role` | `web` |

From the master, install the AWS inventory collection and inspect discovered hosts:

```bash
ansible-galaxy collection install amazon.aws
ansible-inventory -i ansible/inventory/aws_ec2.yml --graph
```

The graph should include `name_web1` and `name_web2`. If it reports missing AWS credentials, attach an EC2 instance profile to the master with `ec2:DescribeInstances` permission in `ap-south-1`.

## Deploy from the master

1. Put the SSH private key on the master. The checked-in Ansible variables expect it at `/home/ubuntu/Josh.pem`; if yours is elsewhere, update `ansible/group_vars/all.yml`. Keep private keys out of Git.
2. Set restrictive key permissions and verify SSH access. The target security group must allow SSH from the master:

```bash
chmod 400 /home/ubuntu/Josh.pem
ssh -i /home/ubuntu/Josh.pem ubuntu@<web1-private-ip>
ssh -i /home/ubuntu/Josh.pem ubuntu@<web2-private-ip>
```

3. From the project root on the master, build/test and deploy:

```bash
mvn --batch-mode clean verify
ansible-galaxy collection install amazon.aws
ansible-playbook \
	-i ansible/inventory/aws_ec2.yml \
	ansible/deploy.yml \
	-e "artifact_path=$PWD/target/ecom-proj-0.1.0-SNAPSHOT.jar"
```

The playbook targets `name_web1` and `name_web2` one host at a time. It installs the Java 21 runtime, creates the restricted `ecomproj` service account, copies the JAR to `/opt/ecom-proj/app.jar`, installs and enables the `ecom-proj` systemd service, and checks that the site returns HTTP 200 on port `8080`.

Check both services and their logs:

```bash
ansible 'name_web1:name_web2' \
	-i ansible/inventory/aws_ec2.yml \
	-m command -a 'systemctl is-active ecom-proj'

ansible 'name_web1:name_web2' \
	-i ansible/inventory/aws_ec2.yml \
	-b -m command -a 'journalctl -u ecom-proj -n 50 --no-pager'
```

The storefront listens on port `8080`; browse to `http://<host-public-ip>:8080`. The AWS security group must allow inbound TCP `8080` from your client IP. Restrict broad inbound rules before using this demo publicly. The deployment does not route through nginx; nginx may continue running separately on port `80`.

## Continuous deployment with GitHub Actions

The workflow at [.github/workflows/deploy.yml](.github/workflows/deploy.yml) runs `mvn clean verify` on pushes to `main` and on manual dispatch. It packages the JAR, securely transfers the JAR and Ansible files to the master, then runs the same deployment playbook against the private EC2 addresses.

Add these **repository Actions secrets** under GitHub repository settings before relying on automated deployment:

| Secret | Value |
| --- | --- |
| `DEPLOY_HOST` | Master public DNS name or a stable Elastic IP. |
| `DEPLOY_SSH_PRIVATE_KEY` | Private key that can SSH to the master. Never commit it. |
| `DEPLOY_KNOWN_HOSTS` | Verified `known_hosts` entry for the master; verify its fingerprint through a trusted channel. |

`DEPLOY_USER` is optional and defaults to `ubuntu`. The master must have its own EC2 instance profile for AWS discovery and the target SSH key at the path configured in `ansible/group_vars/all.yml`. The master must be reachable from GitHub-hosted runners over SSH port `22`; configure the security group to allow only the runner/network sources you intend to trust. Protect production deployments with GitHub environment approvals.

See [DEPLOYMENT.md](DEPLOYMENT.md) for the deployment design and additional setup notes.