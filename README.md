# Credit Application System

Credit Application System with Spring Boot and Kotlin

## Usage

### Customer

**Rota: POST `/api/customers`**

```bash
curl -s -X POST -H "Content-Type: application/json" -d '{
  "firstName": "John",
  "lastName": "Doe",
  "cpf": "980.860.250-98",
  "income": 1000.0,
  "email": "john@example.com",
  "password": "123456",
  "zipCode": "654321",
  "street": "John Street"
}' "http://localhost:8080/api/customers" | jq
```

**Rota: GET `/api/customers/{id}`**

```bash
curl -s -X GET "http://localhost:8080/api/customers/1" | jq
```

**Rota: DELETE `/api/customers/{id}`**

```bash
curl -s -X DELETE "http://localhost:8080/api/customers/1" | jq
```

**Rota: PATCH `/api/customers?customerId={id}`**

```bash
curl -s -X PATCH -H "Content-Type: application/json" -d '{
  "firstName": "John",
  "lastName": "Smith",
  "income": 10500.0,
  "zipCode": "654321",
  "street": "John Street"
}' "http://localhost:8080/api/customers?customerId=1" | jq
```

---

### Credits

**Rota: POST `/api/credits`**

```bash
curl -s -X POST -H "Content-Type: application/json" -d '{
  "customerId": 1,
  "creditValue": 300.0,
  "dayFirstOfInstallment": "2023-07-01",
  "numberOfInstallments": 5
}' "http://localhost:8080/api/credits" | jq
```

**Rota: GET `/api/credits?customerId={customerId}`**

```bash
curl -s -X GET "http://localhost:8080/api/credits?customerId=1" | jq
```

**Rota: GET `/api/credits/{creditCode}?customerId={customerId}`**

```bash
curl -s -X GET "http://localhost:8080/api/credits/30d540c4-2e7f-44f7-95f0-0cbbc980365f?customerId=1" | jq
```
