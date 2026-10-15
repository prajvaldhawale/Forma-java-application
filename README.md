# Forma Electronics

Forma Electronics is a Java 21 and Spring Boot storefront demo for electronics. It includes a searchable, filterable product catalog, session-backed shopping bag, quantity controls, and a validated demo checkout. Checkout does not process payments or persist orders.

## Requirements

Ansible Dynamic Inventory Setup with AWS EC2
Project Overview
This project demonstrates how to connect an Ansible Master Server to AWS EC2 Host Servers using:

Ansible
AWS Dynamic Inventory
SSH Authentication using PEM Key
EC2 Tags
AWS CLI
Dynamic Host Discovery
Architecture
                    Josh.pem
                       |
                       |
            +--------------------+
            |  Master Server     |
            |   65.2.78.117      |
            |  Ansible Installed |
            +--------------------+
                  /         \
                 / SSH       \ SSH
                /             \
               /               \
+--------------------+   +--------------------+
| Host Server 1      |   | Host Server 2      |
| 13.201.225.186     |   | 15.252.242.134     |
+--------------------+   +--------------------+
Why Dynamic Inventory?
Static Inventory
Traditional inventory:

[web]
13.201.225.186
15.252.242.134
Problems:

Manual maintenance
Not scalable
Difficult in cloud environments
Need to update inventory whenever servers are added or removed
Dynamic Inventory
Instead of manually maintaining hosts:

Ansible
    ↓
AWS API
    ↓
Discover EC2 Instances
    ↓
Create Inventory Automatically
Benefits:

Automatic host discovery
No manual updates
Scalable
Cloud-friendly
Uses EC2 tags for grouping
Prerequisites
AWS EC2 Instances
Server	Public IP	Purpose
Master	65.2.78.117	Ansible Control Node
Host-1	13.201.225.186	Managed Node
Host-2	15.252.242.134	Managed Node
Required Files
Josh.pem
PEM key is required to SSH into EC2 instances.

Step 1: Install Ansible
Update packages:

sudo apt update
Install Ansible:

sudo apt install ansible -y
Verify installation:

ansible --version
Why?
Ansible is the automation tool that connects from the master server to the host servers.

Step 2: Install AWS CLI
sudo apt install awscli -y
Verify:

aws --version
Why?
AWS CLI allows access to AWS services and EC2 information.

Step 3: Configure AWS Credentials
aws configure
Provide:

Access Key
Secret Key
Region
Output Format
Verify credentials:

aws sts get-caller-identity
Expected:

{
    "UserId": "xxxxx",
    "Account": "xxxxx"
}
Why?
Ansible Dynamic Inventory needs access to AWS APIs.

Step 4: Install AWS Dynamic Inventory Plugin
Install collection:

ansible-galaxy collection install amazon.aws
Verify:

ansible-galaxy collection list | grep amazon.aws
Expected:

amazon.aws
Why?
Allows Ansible to communicate with AWS EC2.

Step 5: Install Python Dependencies
pip3 install boto3 botocore
Why?
AWS inventory plugin uses:

boto3
botocore
to query AWS APIs.

Step 6: Configure EC2 Tags
Master Server
Name=Ansible-Master
Role=master
Environment=dev
Host Server 1
Name=Host-1
Role=web
Environment=dev
Host Server 2
Name=Host-2
Role=web
Environment=dev
Why?
Tags help Dynamic Inventory filter and group servers.

Step 7: Verify Tags
aws ec2 describe-instances \
--region ap-south-1 \
--query "Reservations[*].Instances[*].[PublicIpAddress,Tags]" \
--output table
Expected:

65.2.78.117     Role=master
13.201.225.186  Role=web
15.252.242.134  Role=web
Step 8: Create Dynamic Inventory File
Create file:

mkdir ~/ansible
cd ~/ansible

nano aws_ec2.yml
Add:

---
plugin: amazon.aws.aws_ec2

regions:
  - ap-south-1

filters:
  instance-state-name: running
  tag:Role: web
  tag:Environment: dev

hostnames:
  - ip-address

compose:
  ansible_host: public_ip_address
Save file.

Explanation of Inventory File
plugin
plugin: amazon.aws.aws_ec2
Uses AWS EC2 inventory plugin.

region
regions:
  - ap-south-1
Checks EC2 instances in Mumbai Region.

filters
filters:
  tag:Role: web
Returns only servers tagged as:

Role=web
Master node is excluded.

hostnames
hostnames:
  - ip-address
Uses EC2 IP address as inventory hostname.

compose
ansible_host: public_ip_address
Maps AWS public IP to SSH host.

Step 9: Create ansible.cfg
nano ansible.cfg
Add:

[defaults]
inventory = aws_ec2.yml
host_key_checking = False
private_key_file = /home/ubuntu/Josh.pem
remote_user = ubuntu
Explanation
inventory
inventory = aws_ec2.yml
Uses Dynamic Inventory automatically.

private_key_file
private_key_file=/home/ubuntu/Josh.pem
Uses PEM key automatically.

remote_user
remote_user=ubuntu
Logs into EC2 using Ubuntu user.

Step 10: Verify Dynamic Inventory
ansible-inventory --graph
Expected:

@all
 |--@aws_ec2
 |  |--13.201.225.186
 |  |--15.252.242.134
Why?
Confirms AWS Dynamic Inventory is working.

Step 11: Verify SSH Connectivity
Manual SSH:

ssh -i /home/ubuntu/Josh.pem ubuntu@13.201.225.186
Verify Host-2:

ssh -i /home/ubuntu/Josh.pem ubuntu@15.252.242.134
Why?
SSH must work before Ansible can work.

Step 12: Test Ansible Connection
ansible all -m ping
Expected:

13.201.225.186 | SUCCESS
15.252.242.134 | SUCCESS
Output:

{
    "ping": "pong"
}
What Happens Internally?
When we run:

ansible all -m ping
Process:

1. Read aws_ec2.yml
2. Query AWS API
3. Get running EC2 instances
4. Apply tag filters
5. Collect public IPs
6. SSH using Josh.pem
7. Execute Ansible module
8. Return results
Step 13: Install Nginx Using Ansible
ansible all \
-b \
-m apt \
-a "name=nginx state=present update_cache=yes"
Why?
Installs nginx on every discovered host.

Verify Nginx
ansible all -a "nginx -v"
or

ansible all -a "systemctl status nginx"
