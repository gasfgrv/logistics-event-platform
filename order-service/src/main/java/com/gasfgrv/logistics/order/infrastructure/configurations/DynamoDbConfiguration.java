package com.gasfgrv.logistics.order.infrastructure.configurations;

import com.gasfgrv.logistics.order.infrastructure.configurations.properties.DynamoDbProperties;
import com.gasfgrv.logistics.order.infrastructure.integrations.dynamodb.entities.OrdersEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class DynamoDbConfiguration {

    private final DynamoDbProperties dynamoDbProperties;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(dynamoDbProperties.serviceEndpoint()))
                .region(Region.of(dynamoDbProperties.region()))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<OrdersEntity> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table("orders", TableSchema.fromBean(OrdersEntity.class));
    }

}
