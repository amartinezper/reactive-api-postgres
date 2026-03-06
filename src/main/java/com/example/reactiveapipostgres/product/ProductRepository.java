package com.example.reactiveapipostgres.product;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    @Query("SELECT id, name, price FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Flux<Product> searchByName(String name);
}
