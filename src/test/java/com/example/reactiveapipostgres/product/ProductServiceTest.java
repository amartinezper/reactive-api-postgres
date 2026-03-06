package com.example.reactiveapipostgres.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductRepository.class);
        service = new ProductService(repository);
    }

    @Test
    void findAllReturnsRepositoryData() {
        Mockito.when(repository.findAll()).thenReturn(Flux.just(
                new Product(1L, "Keyboard", BigDecimal.valueOf(39.99)),
                new Product(2L, "Mouse", BigDecimal.valueOf(19.50))
        ));

        StepVerifier.create(service.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void createForcesNullIdBeforeSaving() {
        Product input = new Product(99L, "Monitor", BigDecimal.valueOf(199.99));
        Product saved = new Product(3L, "Monitor", BigDecimal.valueOf(199.99));
        Mockito.when(repository.save(new Product(null, "Monitor", BigDecimal.valueOf(199.99))))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(service.create(input))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void updateReturnsEmptyWhenProductDoesNotExist() {
        Mockito.when(repository.findById(7L)).thenReturn(Mono.empty());

        StepVerifier.create(service.update(7L, new Product(null, "Desk", BigDecimal.valueOf(80.00))))
                .verifyComplete();
    }

    @Test
    void searchByNameDelegatesToRepository() {
        Mockito.when(repository.searchByName("key"))
                .thenReturn(Flux.just(new Product(1L, "Keyboard", BigDecimal.valueOf(39.99))));

        StepVerifier.create(service.searchByName("key"))
                .expectNextMatches(product -> "Keyboard".equals(product.name()))
                .verifyComplete();
    }
}
