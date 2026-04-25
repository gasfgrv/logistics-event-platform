package com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.repositories;

import com.gasfgrv.logistics.order.domain.models.enums.OrderStatus;
import com.gasfgrv.logistics.order.infrastructure.configurations.DynamoDbConfiguration;
import com.gasfgrv.logistics.order.infrastructure.configurations.properties.DynamoDbProperties;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = {OrderRepository.class, DynamoDbConfiguration.class})
@EnableConfigurationProperties(DynamoDbProperties.class)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:4.14.0"))
            .withServices(LocalStackContainer.Service.DYNAMODB);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.dynamo.service-endpoint", localstack::getEndpoint);
        registry.add("aws.dynamo.region", localstack::getRegion);
        registry.add("aws.dynamo.access-key", localstack::getAccessKey);
        registry.add("aws.dynamo.secret-key", localstack::getSecretKey);
    }

    @BeforeEach
    void createTable() throws IOException, InterruptedException {
        localstack.execInContainer("awslocal", "dynamodb", "create-table",
                "--table-name", "orders",
                "--billing-mode", "PAY_PER_REQUEST",
                "--attribute-definitions",
                "AttributeName=order_id,AttributeType=S", "AttributeName=order_customer,AttributeType=S",
                "--key-schema",
                "AttributeName=order_id,KeyType=HASH", "AttributeName=order_customer,KeyType=RANGE");
    }

    @AfterEach
    void destroyContainer() throws IOException, InterruptedException {
        localstack.execInContainer("awslocal", "dynamodb", "delete-table", "--table-name", "orders");
    }

    @AfterAll
    static void stopContainer() {
        localstack.stop();
    }

    @Test
    @DisplayName("OrderRepository should save a new order in DynamoDb")
    void orderrepositoryShouldSaveANewOrderInDynamodb() throws IOException, InterruptedException {
        // Arrange
        OrdersEntity order = generateNewOrder();

        // Act
        repository.save(order);

        // Assert
        String stdout = localstack.execInContainer("awslocal", "dynamodb", "scan", "--table-name", "orders")
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
        OrdersEntity order = generateNewOrder();
        localstack.execInContainer("awslocal", "dynamodb", "put-item",
                "--table-name", "orders",
                "--item", String.format("""
                                {
                                    "order_id": {"S": "%s"},
                                        "order_customer": {"S": "%s"},
                                        "order_origin": {"S": "%s"},
                                        "order_destination": {"S": "%s"},
                                        "order_weight": {"N": "%s"},
                                        "order_status": {"S": "%s"},
                                        "order_creation": {"S": "%s"}
                                    }
                                """,
                        order.getId(),
                        order.getCustomerId(),
                        order.getOrigin(),
                        order.getDestination(),
                        order.getWeight(),
                        order.getStatus(),
                        order.getCreatedAt()
                ));

        // Act
        Optional<OrdersEntity> query = repository.query(order.getId());

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
        OrdersEntity order = generateNewOrder();

        // Act
        Optional<OrdersEntity> query = repository.query(order.getId());

        // Assert
        assertThat(query).isEmpty();
    }

    private OrdersEntity generateNewOrder() {
        return Instancio.of(OrdersEntity.class)
                .set(Select.field(OrdersEntity::getId), UUID.randomUUID())
                .set(Select.field(OrdersEntity::getCustomerId), UUID.randomUUID())
                .generate(Select.field(OrdersEntity::getOrigin), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(OrdersEntity::getDestination), gen -> gen.text().pattern("#d#d#d#d#d-#d#d#d"))
                .generate(Select.field(OrdersEntity::getWeight), gen -> gen.floats().range(1f, 100f))
                .supply(Select.field(OrdersEntity::getStatus), random -> random.oneOf(OrderStatus.values()).getStatus())
                .generate(Select.field(OrdersEntity::getCreatedAt), gen -> gen.temporal().localDateTime())
                .create();
    }

}
