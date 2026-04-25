
### **Resumo da Classificação:**

#### **Precisa de Testes de Integração (com sistemas reais ou simulados):**

- **Controllers** (OrderController.java, `OrderControllerAdvice.java`): Endpoints REST. Testar com Spring Boot Test (MockMvc) para validar requests/responses, validação e tratamento de erros.
- **Repositories** (OrderRepository.java): Interação com DynamoDB. Testar com DynamoDB local (Testcontainers ou embedded) para operações CRUD.
- **Producers** (mesmos acima): Além de unitários, testar envio real com Kafka embedded (Testcontainers) para validar serialização e publicação.