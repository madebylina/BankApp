# Bank Microservices App

A microservices-based banking application built with Spring Boot, Kubernetes, and Kafka.

## What It Does

Users can:
- Sign up and create an account
- Log in with username and password
- Add accounts in different currencies
- Deposit and withdraw virtual money
- Transfer money between their own accounts (with currency conversion)
- Send money to other users (with currency conversion)

## Frontend Pages

**Registration Page**
- Fields: Last name, First name, Email, Date of birth, Username, Password
- Validation: All fields required, must be 18+ years old
- After signup, you're automatically logged in and redirected to the dashboard

**Login Page**
- Username and password fields
- Optional "remember me" feature
- Takes you to the dashboard after login

**Dashboard** (only for logged-in users)
Has sections for:
- Account settings
- Depositing and withdrawing money
- Transferring between your accounts
- Sending money to other users
- Viewing exchange rates

**Logout**
- Just a link that logs you out and sends you back to the login page

## Microservices

**Accounts Service**
- Stores user accounts and their banking accounts (including login credentials)
- Handles REST requests from the frontend for managing accounts
- Sends notifications through the Notifications service

**Cash Service**
- Handles deposits and withdrawals
- Checks with Accounts service to verify and update balances
- Uses Blocker service to detect suspicious activity
- Sends notifications

**Transfer Service**
- Processes money transfers between accounts (same user or different users)
- Works with Accounts, Exchange, Blocker, and Notifications services
- Handles currency conversion when needed

**Exchange Service**
- Stores exchange rates and handles currency conversions
- Base currency is RUB (rate = 1)
- Provides current rates to the frontend

**Exchange Generator Service**
- Generates new exchange rates every second
- Supports at least 3 currencies: RUB (base), USD, and CNY
- When converting USD to CNY, it goes through RUB first
- Sends rates to Exchange service via REST

**Blocker Service**
- Watches for suspicious transactions
- Helps protect against fraud

**Notifications Service**
- Sends notifications about important actions (transfers, deposits, withdrawals, etc.)
- Can send via email or alerts

## Architecture

![Service Architecture](https://github.com/mynameisSergey/BankApp/blob/main/image/schema.png)

_Check out the [full diagram on GitHub](https://github.com/mynameisSergey/BankApp/blob/main/image/schema.png)_

## Tech Stack

- PostgreSQL
- Kafka
- Nginx
- Keycloak (authentication)
- Spring Boot microservices
- React frontend

## Running with Helm on Windows 10

**1. Build all Maven modules:**
```bash
mvn clean package
```

**2. Start Minikube with Docker:**
```bash
minikube start --driver=docker
```

**3. Install ingress-nginx controller:**
```bash
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx --namespace ingress-nginx --create-namespace
```

**4. Use Docker inside Minikube:**
```bash
minikube docker-env | Invoke-Expression
```

**5. Build Docker images:**
```bash
docker build -t exchange-api ./exchange
docker build -t exchange-generator ./exchange-generator
docker build -t blocker-api ./blocker
docker build -t notifications-api ./notifications
docker build -t accounts-api ./accounts
docker build -t transfer-api ./transfer
docker build -t cash-api ./cash
docker build -t front-ui ./front-ui
```

**6. Update Helm dependencies:**
```bash
helm dependency update ./bank-app
```

**7. Install the app:**
```bash
helm install bank-app ./bank-app
```

**8. Check if pods are ready:**
```bash
kubectl get pods
```

**9. Forward the frontend port:**
```bash
kubectl port-forward svc/bank-app-front-ui 8080:8080
```
Open http://localhost:8080/ in your browser

**10. (Optional) Add a custom host:**
Add to `/etc/hosts`:
```
127.0.0.1 bankapp
```
Then run:
```bash
minikube tunnel
```
Now you can access it at http://bankapp/

## Stopping the App

```bash
helm uninstall bank-app
```

## Running with Jenkins

**1. Enable Docker daemon in Docker Desktop:**
```
Settings -> General -> Expose daemon on tcp://localhost:2375 without TLS
```

**2. Configure environment variables in `jenkins/.env`:**
- `MINIKUBE_PATH` - path to minikube profile (e.g., C:/Users/your_user/.minikube)
- `GHCR_TOKEN` - GitHub Container Registry token
- `GITHUB_USERNAME` - your GitHub username
- `DOCKER_REGISTRY` - Docker Registry address

**3. Start Jenkins:**
```bash
cd jenkins
docker-compose up -d
```

**4. Connect Jenkins to Minikube network:**
```bash
docker network connect minikube jenkins
```

## Deploying Individual Services with Jenkins

Run these builds in order (each deploys to the `default` namespace):

1. 01_kafka
2. 02_keycloak
3. 03_postgresql
4. 04_exchange-api
5. 05_exchange-generator
6. 06_blocker-api
7. 07_notifications-api
8. 08_accounts-api
9. 09_transfer-api
10. 10_cash-api
11. 11_front-ui

## Deploying the Full App

Run the Jenkins build:
```
00_bank-app
```

Add to `/etc/hosts`:
```
127.0.0.1 BankApp-test
127.0.0.1 BankApp-prod
```

Access the app:
- Test: http://BankApp-test/
- Production: http://BankApp-prod/
