## Deployment Strategy

### Frontend Hosting

- For now **Vercel** would be the best choice for frontend deployment, it provides the simplest setup and best native support for Next.js, including automatic builds, preview environments, and global CDN delivery.
- In the future, if full AWS integration is preferred, the frontend can be migrated to **AWS Amplify** Hosting to unify hosting, monitoring, and cost management within AWS.

### Backend Deployment on AWS

- The backend should be deployed on **AWS App Runner**, which provides a fully managed container environment with automatic HTTPS, health checks, and scaling.
- **App Runner** offers stable performance without cold starts (better than Lambda for Java) and lower baseline cost and setup effort compared to ECS with an ALB.

### Security Measures

- _Secure communication_: All traffic between the frontend (Vercel) and backend (AWS App Runner) uses HTTPS with AWS-managed certificates to protect data in transit.
- _Controlled access_: The API accepts requests only from trusted domains through a strict CORS allow-list, blocking any unauthorized origins.
- _Secret management_: Sensitive values such as API keys are stored in AWS Secrets Manager or SSM
- _Logging and monitoring_: Application logs are structured, sent to CloudWatch, and exclude sensitive data; alerts can be configured for error rates and unusual activity.

### Scalability and Cost Considerations

The chosen architecture is both highly scalable and cost-efficient.

- _Elastic frontend_: The frontend on Vercel scales automatically through its global CDN — static assets and API calls are served from edge nodes with no manual configuration or extra cost for traffic spikes.
- _Autoscaling backend_: AWS App Runner scales containers based on concurrency/CPU (set sensible min/max instances). Keep min instances = 1 for stable latency; raise max for traffic spikes.
- _Avoid unnecessary costs_: App Runner does not require an ALB or NAT, which reduces baseline cost compared to ECS. Keep only one warm instance in low-traffic hours; scale out on demand.

### Infrastructure as Code

- I would automate the provisioning of the AWS infrastructure using Terraform. This includes defining resources such as the ECR repository, App Runner service, Route 53 domain records, and ACM certificates as code.
- Secrets would be stored in AWS SSM Parameter Store and accessed securely through least-privilege IAM roles.
- The CI/CD pipeline (via GitHub Actions with OIDC) would automatically run terraform plan and apply to deploy updates, ensuring consistent, repeatable, and secure infrastructure setup.
