# Serviços

## 1. `order.created`

**Origem:** Order Service

**Motivo:** início do fluxo do pedido

```json
{
  "eventId": "uuid",
  "eventType": "ORDER_CREATED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:00:00Z",

  "orderId": "uuid",
  "customerId": "uuid",
  "origin": "SP",
  "destination": "RJ",
  "weight": 120.5
}
```

### Observações

- Payload mínimo para permitir evolução.
- Não inclui status do pedido.

---

## 2. `order.cancelled`

**Origem:** Order Service

**Motivo:** pedido cancelado antes da conclusão do processo

```json
{
  "eventId": "uuid",
  "eventType": "ORDER_CANCELLED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:05:00Z",

  "orderId": "uuid",
  "reason": "CUSTOMER_REQUEST"
}
```

---

## 3. `freight.calculated`

**Origem:** Freight Service

**Motivo:** frete calculado com sucesso

```json
{
  "eventId": "uuid",
  "eventType": "FREIGHT_CALCULATED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:01:10Z",

  "orderId": "uuid",
  "distanceKm": 430,
  "price": 780.90,
  "currency": "BRL"
}
```

### Observações

- Inclui valores financeiros claros.
- Não depende do modelo de Order.

---

## 4. `freight.failed`

**Origem:** Freight Service

**Motivo:** falha irrecuperável no cálculo do frete

```json
{
  "eventId": "uuid",
  "eventType": "FREIGHT_FAILED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:01:20Z",

  "orderId": "uuid",
  "errorCode": "DISTANCE_SERVICE_UNAVAILABLE",
  "errorMessage": "Unable to calculate distance"
}
```

---

## 5. `transport.reserved`

**Origem:** Transport Service

**Motivo:** transporte reservado com sucesso

```json
{
  "eventId": "uuid",
  "eventType": "TRANSPORT_RESERVED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:02:30Z",

  "orderId": "uuid",
  "vehicleId": "TRUCK-8821",
  "driverName": "João Silva"
}
```

---

## 6. `transport.failed`

**Origem:** Transport Service

**Motivo:** reserva de transporte não disponível

```json
{
  "eventId": "uuid",
  "eventType": "TRANSPORT_FAILED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:02:45Z",

  "orderId": "uuid",
  "reason": "NO_VEHICLE_AVAILABLE"
}
```

---

## 7. `shipment.dispatched`

**Origem:** Tracking Service

**Motivo:** envio iniciado

```json
{
  "eventId": "uuid",
  "eventType": "SHIPMENT_DISPATCHED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T10:10:00Z",

  "orderId": "uuid",
  "status": "DISPATCHED"
}
```

---

## 8. `shipment.delivered`

**Origem:** Tracking Service

**Motivo:** entrega concluída

```json
{
  "eventId": "uuid",
  "eventType": "SHIPMENT_DELIVERED",
  "eventVersion": 1,
  "occurredAt": "2026-01-18T18:45:00Z",

  "orderId": "uuid",
  "status": "DELIVERED",
  "deliveredAt": "2026-01-18T18:45:00Z"
}
```

---

## Resumo dos tópicos e schemas

| Tópico | Serviço | Tipo |
| --- | --- | --- |
| order.created | Order | Evento de domínio |
| order.cancelled | Order | Evento de domínio |
| freight.calculated | Freight | Evento de domínio |
| freight.failed | Freight | Evento de erro |
| transport.reserved | Transport | Evento de domínio |
| transport.failed | Transport | Evento de erro |
| shipment.dispatched | Tracking | Evento de domínio |
| shipment.delivered | Tracking | Evento de domínio |

---

## Boas práticas aplicadas aqui

- Eventos não reutilizam entidades JPA
- Schemas são estáveis e versionáveis
- Erros são eventos, não exceções distribuídas
- Cada serviço publica apenas eventos do seu domínio
- Payloads são pequenos e evolutivos
