# Multi-Currency Invoice

This project is a **full-stack application** built as part of the VerifiMe technical challenge.  
It demonstrates the design and implementation of a **multi-currency invoice calculator**, featuring:

- **Backend (Quarkus, Java)**

  - REST API `POST /invoice/total` running on `http://localhost:8080`
  - Integrates with [Frankfurter API](https://www.frankfurter.app/docs/) for historical exchange rates
  - Applies strict rounding rules:
    - Exchange rates rounded to **4 decimal places**
    - Line totals and invoice total rounded to **2 decimal places**
  - Returns results as plain text (e.g. `1600.86`)
  - Error handling with plain-text responses prefixed by `Error:` (`400`, `404`, `500`)

- **Frontend (Next.js, TypeScript, MUI)**
  - Invoice form with:
    - Invoice Date (DatePicker)
    - Base Currency selector
    - Dynamic list of invoice lines (description, amount, currency)
  - "Calculate Total" button triggers API call to backend
  - Displays final total or error message gracefully

---

## 🚀 Getting Started

### Prerequisites

- Node.js (>=18)
- Java 17+ and Maven

### Installation

```bash
# clone repository
git clone https://github.com/<your-username>/multi-currency-invoice.git
cd multi-currency-invoice

# start backend
cd backend
./mvnw quarkus:dev

# start frontend
cd ../frontend
npm install
npm run dev
```
