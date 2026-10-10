# Forma Electronics

A Java 21 and Spring Boot storefront demo for electronics. It includes a searchable, filterable catalog, a session-backed shopping bag, quantity controls, and a validated demo checkout. Checkout does not process payments or persist orders.

## Run locally

Requirements: Java 21 and Maven 3.9 or newer.

```bash
mvn spring-boot:run
```

Open <http://localhost:8080>.

Run the tests with:

```bash
mvn test
```

Product photos and the display fonts load from external image/font CDNs, so an internet connection is needed for those assets. The shop and sample product data run locally.

See [DEPLOYMENT.md](DEPLOYMENT.md) for the Ansible playbook and GitHub Actions deployment setup for the EC2 hosts.