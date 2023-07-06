package com.zakura.stockservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;

@TestConfiguration(proxyBeanMethods = false)
public class TestStockServiceApplication {

	static Network network = Network.newNetwork();

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
		return new MongoDBContainer("mongo:latest").withNetwork(network).withNetworkAliases("mongodb");
	}

	@Bean
	@ServiceConnection(name = "openzipkin/zipkin")
	GenericContainer<?> zipkinContainer() {
		return new GenericContainer<>("openzipkin/zipkin:latest").withExposedPorts(9411)
		.withNetwork(network)
        .withNetworkAliases("zipkin");
	}

	@Bean
	GenericContainer<?> discoveryServiceContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
		GenericContainer<?> discoveryServiceContainer = new GenericContainer<>("dockertmt/discovery-service")
				.withExposedPorts(8761).withNetwork(network).withNetworkAliases("discovery-service");
		dynamicPropertyRegistry.add("eureka.client.service-url.defaultZone", () -> String.format("http://%s:%d/eureka",
				discoveryServiceContainer.getHost(), discoveryServiceContainer.getMappedPort(8761)));
		return discoveryServiceContainer;
	}

	@Bean
	GenericContainer<?> portfolioServiceContainer(GenericContainer<?> discoveryServiceContainer,
			MongoDBContainer mongoDbContainer) {
		return new GenericContainer<>("dockertmt/portfolio-service:0.0.1")
				.withExposedPorts(8002).withNetwork(network)
				.dependsOn(discoveryServiceContainer, mongoDbContainer)
				.withEnv("eureka.client.service-url.defaultZone", String.format("http://%s:%d/eureka",
						discoveryServiceContainer.getHost(), discoveryServiceContainer.getMappedPort(8761)))
				.withEnv("spring.data.mongodb.uri", mongoDbContainer.getReplicaSetUrl());
	}

	public static void main(String[] args) {
		SpringApplication.from(StockServiceApplication::main).with(TestStockServiceApplication.class).run(args);
	}

}
