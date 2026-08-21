package com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.repositories;

import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.infrastructure.configurations.DynamoDbConfiguration;
import com.gasfgrv.logistics.order.infrastructure.configurations.properties.DynamoDbProperties;
import com.gasfgrv.logistics.order.infrastructure.containers.LocalstackTestcontainersConfiguration;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {OrderRepository.class, DynamoDbConfiguration.class})
@EnableConfigurationProperties(DynamoDbProperties.class)
@Import(LocalstackTestcontainersConfiguration.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private GenericContainer<?> localStack;

    @BeforeEach
    void createTable() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "dynamodb", "create-table",
                "--table-name", "orders",
                "--billing-mode", "PAY_PER_REQUEST",
                "--attribute-definitions", "AttributeName=order_id,AttributeType=S", "AttributeName=order_customer,AttributeType=S",
                "--key-schema", "AttributeName=order_id,KeyType=HASH", "AttributeName=order_customer,KeyType=RANGE");
    }

    @AfterEach
    void destroyContainer() throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "dynamodb", "delete-table", "--table-name", "orders");
    }

    @Test
    @DisplayName("OrderRepository should save a new order in DynamoDb")
    void orderrepositoryShouldSaveANewOrderInDynamodb() throws IOException, InterruptedException {
        // Arrange
        var order = generateNewOrder();

        // Act
        repository.save(order);

        // Assert
        var stdout = localStack.execInContainer("awslocal", "dynamodb", "scan", "--table-name", "orders")
                .getStdout();

        assertThat(stdout)
                .isNotBlank()
                .contains(
                        order.getId().toString(),
                        order.getCustomerId().toString(),
                        order.getOrigin(),
                        order.getDestination(),
                        String.valueOf(order.getWeight()),
                        order.getStatus(),
                        order.getCreatedAt().toString().substring(0, 20)
                );
    }

    @Test
    @DisplayName("OrderRepository should return an order when it exists in the table")
    void orderrepositoryShouldReturnAnOrderWhenItExistsInTheTable() throws IOException, InterruptedException {
        // Arrange
        var order = generateNewOrder();
        localStack.execInContainer("awslocal", "dynamodb", "put-item",
                "--table-name", "orders",
                "--item", generateDynamoItem(order));

        // Act
        var query = repository.query(order.getId());

        // Assert
        assertThat(query).isPresent()
                .get()
                .returns(order.getId(), OrdersEntity::getId)
                .returns(order.getCustomerId(), OrdersEntity::getCustomerId)
                .returns(order.getOrigin(), OrdersEntity::getOrigin)
                .returns(order.getDestination(), OrdersEntity::getDestination)
                .returns(order.getWeight(), OrdersEntity::getWeight)
                .returns(order.getStatus(), OrdersEntity::getStatus)
                .returns(order.getCreatedAt(), OrdersEntity::getCreatedAt);
    }

    @Test
    @DisplayName("OrderRepository should return an empty optional when it does not exist in the table")
    void orderrepositoryShouldReturnAnEmptyOptionalWhenItDoesNotExistInTheTable() {
        // Arrange
        var order = generateNewOrder();

        // Act
        var query = repository.query(order.getId());

        // Assert
        assertThat(query).isEmpty();
    }

    private static String generateDynamoItem(OrdersEntity order) {
        var tableSchema = """
                {
                    "order_id": {"S": "%s"},
                    "order_customer": {"S": "%s"},
                    "order_origin": {"S": "%s"},
                    "order_destination": {"S": "%s"},
                    "order_weight": {"N": "%s"},
                    "order_status": {"S": "%s"},
                    "order_creation": {"S": "%s"}
                }
                """;

        return String.format(tableSchema,
                order.getId(),
                order.getCustomerId(),
                order.getOrigin(),
                order.getDestination(),
                order.getWeight(),
                order.getStatus(),
                order.getCreatedAt());
    }

    private static OrdersEntity generateNewOrder() {
        var zipCodePattern = "#d#d#d#d#d-#d#d#d";
        return Instancio.of(OrdersEntity.class)
                .set(Select.field(OrdersEntity::getId), UUID.randomUUID())
                .set(Select.field(OrdersEntity::getCustomerId), UUID.randomUUID())
                .generate(Select.field(OrdersEntity::getOrigin), gen -> gen.text().pattern(zipCodePattern))
                .generate(Select.field(OrdersEntity::getDestination), gen -> gen.text().pattern(zipCodePattern))
                .generate(Select.field(OrdersEntity::getWeight), gen -> gen.floats().range(1f, 100f))
                .supply(Select.field(OrdersEntity::getStatus), random -> random.oneOf(OrderStatus.values()).getStatus())
                .generate(Select.field(OrdersEntity::getCreatedAt), gen -> gen.temporal().localDateTime())
                .create();
    }

}
