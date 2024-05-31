# MyCoolService

## Overview

Welcome to **MyCoolService**! This project showcases the latest in software development trends by combining the power of reactive programming with state-of-the-art security features. 
Built using [**Spring Boot 3**](https://spring.io/projects/spring-boot), [**Spring Security**](https://spring.io/projects/spring-security), and [**Spring WebFlux**](https://spring.io/projects/spring-webflux), **MyCoolService** delivers high-performance, scalable, and secure RESTful services, perfect for modern applications.

## Key Features

- **Reactive Programming with WebFlux**: Utilizes Spring WebFlux to create non-blocking, event-driven RESTful services, ensuring high throughput and responsiveness.
- **Advanced Security**:
    - **Spring Security**: Implements the latest Spring Security features for robust authentication.
    - **JWT Tokens**: Uses JSON Web Tokens (JWT) for secure and stateless authentication.
    - **Open Policy Agent (OPA)**: Integrates OPA for flexible and powerful authorization policies.

## Technologies Used

- [**Spring Boot 3**](https://spring.io/projects/spring-boot): The backbone of the application, providing ease of setup and configuration with the latest features and improvements.
- [**Spring WebFlux**](https://spring.io/projects/spring-webflux): A reactive, non-blocking web framework that enables the creation of responsive and resilient services.
- [**Spring Security**](https://spring.io/projects/spring-security): Industry-standard framework, powerful & highly customizable authentication and access-control with protection against common attacks. The de-facto standard for securing Spring-based applications.
- [**JWT (JSON Web Tokens)**](https://jwt.io/): A compact, URL-safe means of representing claims to be transferred between two parties.
- [**OPA (Open Policy Agent)**](https://www.openpolicyagent.org/): A policy engine that decouples policy decision-making from your application, enabling dynamic and fine-grained authorization.

## Getting Started

### Prerequisites

Ensure you have the following installed:
- **JDK 17** or higher
- Maven 3.9.6 or higher
- Docker (optional, for containerized deployment)
- Minikube (optional, for k8 deployment & orchestration)

### Installation

1. **Clone the repository**:
```bash
git clone https://github.com/Karan-patel/mycoolservice.git
cd mycoolservice
```

2. **Build the application**:
```bash
mvn clean install
```

3. **Run the application**:
```bash
mvn spring-boot:run
```

### Docker Deployment (Optional)

To deploy the application using Docker, follow these steps:

1. **Build the Docker image**:
```bash
docker build -t mycoolservice-v1:latest .
```

2. **Run the Docker container**:
```bash
docker run -p 8080:8080 mycoolservice-v1:latest
```
3. **Deploy into minikube**:
Now since we have container that runs and exposes port 8080, all we require to run in **minikube** is deployment.yaml.
Speaking of Kubernetes, here comes the magic ! :)
**kubectl** will do it for us. Run the following commands !
```bash
kubectl create namespace mycoolservice
minikube image load mycoolservice-v1:latest
kubectl create deployment mycoolservice --image=mycoolservice-v1:latest --dry-run=client -o=yaml > deployment.yaml -n mycoolservice
echo --- >> deployment.yaml
kubectl create service clusterip mycoolservice --tcp=8080:8080 --dry-run=client -o=yaml >> deployment.yaml -n mycoolservice
kubectl apply -f deployment.yaml -n mycoolservice
```
4. **Time to check if application is running !**
```bash
kubectl get all -n mycoolservice
```
5. **To connect with application running in container exposed as service, sometimes need SSH-tunneling**
Better to use [**Ingress addon**](https://minikube.sigs.k8s.io/docs/handbook/addons/ingress-dns/#Windows) for minikube.
```bash
kubectl port-forward svc/mycoolservice 8080:8080 -n mycoolservice
```
6. **Final step**
```bash
curl localhost:8080/actuator/health
```
7. **For OPA based policy authorization deploy opa container**
 To load OPA with above policy in Minikube follow [**guideline**](https://github.com/Karan-patel/opa?tab=readme-ov-file#opa-deployment-guide) here.
 Git repository for detailed guide to load policy in Open Policy Agent (OPA) and deploy in Minikube.
#### **It is better to deploy OPA first!**
 ```bash
git clone https://github.com/Karan-patel/mycoolservice.git
   ```

## Usage

### API Endpoints

The application exposes a set of RESTful endpoints. Here are a few examples:

- **GET /mycoolservice/authenticate**: To obtain a jwt token for authentication and authorization.
```bash
curl --location 'http://localhost:8080/mycoolservice/authenticate' \
--header 'Content-Type: application/json' \
--data '{
    "username" : "user",
    "password" : "password"
}'
  ```
- **POST /mycoolservice/api/users**: Creates a new user.
```bash
curl --location 'http://localhost:8080/mycoolservice/api/users' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJhZG1pbiJdLCJzdWIiOiJzd2lzc2NvbSIsImlhdCI6MTcxNzE2OTM0MywiZXhwIjoxNzE3MjA1MzQzfQ.NU3TkVFkigKwtzf07KRvnPtC2vvaqkvq5DK9VOOxMSw' \
--data-raw '{
"name":"John wick",
"email":"John@matrix.com"
}'
  ```
- **GET /mycoolservice/api/users**: Get list of user.
```bash
curl --location 'http://localhost:8080/mycoolservice/api/users' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJhZG1pbiJdLCJzdWIiOiJzd2lzc2NvbSIsImlhdCI6MTcxNzE2OTM0MywiZXhwIjoxNzE3MjA1MzQzfQ.NU3TkVFkigKwtzf07KRvnPtC2vvaqkvq5DK9VOOxMSw'
   ```
### Authentication and Authorization

- **Authentication**: Users authenticate using JWT tokens. Obtain a token by providing valid credentials to the `/mycoolservice/authenticate` endpoint.
- **Authorization**: All endpoints are secured using OPA policies, ensuring fine-grained access control.

### Authorization [**policy**](https://github.com/Karan-patel/opa/blob/main/config/opa/policy.rego)

- Only users with "admin" role can create new user
- Only authenticated users can read the list of users
- Everyone else has no access

## Contributing

We welcome contributions! Please follow these steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Create a new Pull Request.


## Contact

For any questions or feedback, please reach out to [**Karan Patel**](https://www.linkedin.com/in/karanptel/).

---

By leveraging the power of reactive programming, advanced security mechanisms, and modern authorization frameworks, MyCoolService is designed to be a high-performance, secure, and scalable solution for your RESTful service needs. Enjoy building with the latest tech trends!