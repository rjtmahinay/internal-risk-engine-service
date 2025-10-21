# Internal Risk Engine Service

A comprehensive internal risk assessment engine API for loan underwriting built with Spring Boot WebFlux and R2DBC.

## Overview

The Internal Risk Engine Service provides a reactive REST API specifically for internal risk assessment operations. It receives loan application data from external systems and returns comprehensive risk evaluations including risk scores, approval recommendations, and interest rate suggestions based on sophisticated multi-factor analysis.

## Features

- **Reactive Architecture**: Built with Spring WebFlux for high-performance, non-blocking operations
- **Comprehensive Risk Assessment**: Multi-factor risk scoring algorithm
- **Multiple Loan Types**: Support for Personal, Mortgage, Auto, Business, Student, and Credit Card loans
- **Real-time Processing**: Immediate risk assessment upon application submission
- **Statistics & Analytics**: Built-in reporting endpoints for loan portfolio analysis
- **Robust Validation**: Comprehensive input validation and error handling
- **Database Integration**: R2DBC with H2 for reactive database operations

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring WebFlux** (Reactive Web Framework)
- **Spring Data R2DBC** (Reactive Database Access)
- **H2 Database** (In-memory for development)
- **Lombok** (Boilerplate reduction)
- **Bean Validation** (Input validation)
- **JUnit 5** (Testing)

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. Clone the repository:
```bash
git clone <repository-url>
cd internal-risk-engine-service
```

2. Build and run the application:
```bash
mvn spring-boot:run
```

3. The application will start on `http://localhost:8080`

4. Access the H2 Console at `http://localhost:8080/h2-console` (for development)
   - JDBC URL: `jdbc:h2:mem:riskenginedb`
   - Username: `sa`
   - Password: (leave blank)

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

### Risk Assessment Endpoints

#### Evaluate Risk (Primary Endpoint)
```http
POST /risk-assessment/evaluate
Content-Type: application/json

{
  "applicantName": "John Smith",
  "email": "john.smith@email.com",
  "age": 35,
  "annualIncome": 75000.00,
  "loanAmount": 25000.00,
  "loanType": "PERSONAL",
  "loanTermMonths": 60,
  "creditScore": 720,
  "employmentYears": 5,
  "monthlyDebtPayments": 1200.00,
  "downPayment": 0.00,
  "hasCollateral": false,
  "collateralValue": null
}
```

**Response:**
```json
{
  "id": null,
  "loanApplicationId": 1,
  "riskScore": 275,
  "riskLevel": "LOW",
  "approvalRecommendation": true,
  "recommendedInterestRate": 9.50,
  "debtToIncomeRatio": 0.1920,
  "loanToValueRatio": 1.0000,
  "creditScoreFactor": 100,
  "incomeFactor": 75,
  "employmentFactor": 50,
  "collateralFactor": 150,
  "loanTypeFactor": 150,
  "assessmentNotes": "Risk Assessment Summary:\n- Overall Risk Score: 275 (Low Risk)\n- Excellent credit score (720)\n- Unsecured loan increases risk",
  "createdAt": "2025-10-21T21:23:30"
}
```

#### Batch Risk Evaluation
```http
POST /risk-assessment/batch-evaluate
Content-Type: application/json

[
  {
    "applicantName": "John Smith",
    "email": "john.smith@email.com",
    "age": 35,
    "annualIncome": 75000.00,
    "loanAmount": 25000.00,
    "loanType": "PERSONAL",
    ...
  },
  {
    "applicantName": "Jane Doe",
    "email": "jane.doe@email.com",
    "age": 42,
    "annualIncome": 95000.00,
    "loanAmount": 350000.00,
    "loanType": "MORTGAGE",
    ...
  }
]
```

#### Get Risk Assessment by ID
```http
GET /risk-assessment/assessments/{assessmentId}
```

#### Get All Risk Assessments
```http
GET /risk-assessment/assessments
```

### Statistics Endpoints

#### Get Overview Statistics
```http
GET /statistics/overview
```

**Response:**
```json
{
  "totalAssessments": 100,
  "approvedAssessments": 75,
  "rejectedAssessments": 20,
  "pendingAssessments": 5,
  "approvalRate": 75.0,
  "rejectionRate": 20.0,
  "averageRiskScore": 325.5
}
```

#### Individual Statistics
```http
GET /statistics/assessments/count
GET /statistics/assessments/approved/count
GET /statistics/assessments/rejected/count
GET /statistics/risk-score/average
```

## Risk Assessment Algorithm

The risk assessment engine evaluates multiple factors to generate a comprehensive risk score:

### Risk Factors

1. **Credit Score Factor** (Weight: High)
   - 750+: 50 points (Excellent)
   - 700-749: 100 points (Good)
   - 650-699: 150 points (Fair)
   - 600-649: 200 points (Poor)
   - <600: 250 points (Very Poor)
   - No Score: 200 points

2. **Income Factor** (Based on Income-to-Loan Ratio)
   - 3.0+: 50 points (Very Good)
   - 2.0-2.99: 100 points (Good)
   - 1.5-1.99: 150 points (Fair)
   - 1.0-1.49: 200 points (Poor)
   - <1.0: 250 points (Very Poor)

3. **Employment Factor**
   - 5+ years: 50 points (Stable)
   - 2-4 years: 100 points (Good)
   - 1-2 years: 150 points (Recent)
   - <1 year: 200 points (New)

4. **Collateral Factor**
   - Collateral 1.5x+ loan: 25 points
   - Collateral 1.2x+ loan: 50 points
   - Collateral 1.0x+ loan: 75 points
   - Insufficient collateral: 100 points
   - No collateral: 150 points

5. **Loan Type Factor**
   - Mortgage: 50 points
   - Auto: 75 points
   - Student: 100 points
   - Business: 125 points
   - Personal: 150 points
   - Credit Card: 175 points

### Risk Levels

- **Low Risk**: 1-300 points
- **Moderate Risk**: 301-500 points
- **High Risk**: 501-700 points
- **Very High Risk**: 701-1000 points

### Approval Logic

- **Low Risk**: Automatic approval
- **Moderate Risk**: Approval if debt-to-income ratio ≤ 43%
- **High/Very High Risk**: Automatic rejection

### Interest Rate Calculation

Base rates by loan type plus risk premium:

**Base Rates:**
- Mortgage: 3.5%
- Auto: 4.0%
- Student: 5.0%
- Business: 6.0%
- Personal: 8.0%
- Credit Card: 15.0%

**Risk Premiums:**
- Risk Score 701+: +8.0%
- Risk Score 501-700: +5.0%
- Risk Score 301-500: +2.0%
- Risk Score 1-300: +1.0%

## Loan Types

- `PERSONAL`: Unsecured personal loans
- `MORTGAGE`: Home mortgage loans (secured by property)
- `AUTO`: Vehicle loans (secured by vehicle)
- `BUSINESS`: Business loans
- `STUDENT`: Educational loans
- `CREDIT_CARD`: Credit card applications

## Validation Rules

### Loan Application Validation

- **Applicant Name**: 2-100 characters, required
- **Email**: Valid email format, required
- **Age**: 18-100 years, required
- **Annual Income**: > $0, required
- **Loan Amount**: ≥ $1,000, required
- **Loan Term**: 1-480 months, required
- **Credit Score**: 300-850 (optional)
- **Employment Years**: ≥ 0 (optional)
- **Monthly Debt Payments**: ≥ $0 (optional)
- **Down Payment**: ≥ $0 (optional)

## Error Handling

The API provides comprehensive error handling with detailed error responses:

```json
{
  "timestamp": "2025-10-21T21:23:30",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "fieldErrors": {
    "age": "Applicant must be at least 18 years old",
    "email": "Email should be valid"
  }
}
```

## Sample Data

The application includes sample data for testing:

- 5 sample loan applications
- Corresponding risk assessments
- Various loan types and risk profiles

## Testing

Run the test suite:

```bash
mvn test
```

The test suite includes:
- Unit tests for risk scoring algorithms
- Integration tests for API endpoints
- Validation tests for all business rules

## Performance Considerations

- **Reactive Architecture**: Non-blocking I/O for high throughput
- **Database Indexing**: Optimized queries with strategic indexes
- **Connection Pooling**: Efficient database connection management
- **Stateless Design**: Horizontal scaling capability

## Security Considerations

For production deployment, consider implementing:

- Authentication and authorization
- API rate limiting
- Input sanitization
- HTTPS/TLS encryption
- Database security and encryption
- Audit logging

## Monitoring and Observability

The application includes Spring Boot Actuator endpoints:

- `/actuator/health`: Health check
- `/actuator/info`: Application information
- `/actuator/metrics`: Application metrics

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.r2dbc.url=r2dbc:h2:mem:///riskenginedb
spring.r2dbc.username=sa
spring.r2dbc.password=

# Logging
logging.level.com.rjtmahinay.underwriting=DEBUG

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
```

## Future Enhancements

Potential improvements for production use:

1. **Machine Learning Integration**: AI-driven risk scoring
2. **External Credit Bureau Integration**: Real-time credit score retrieval
3. **Workflow Engine**: Multi-step approval processes
4. **Document Management**: Upload and verification of supporting documents
5. **Notification System**: Email/SMS notifications for application status
6. **Advanced Analytics**: Predictive analytics and risk modeling
7. **Multi-tenant Support**: Support for multiple lenders
8. **Audit Trail**: Complete audit logging for compliance

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.
