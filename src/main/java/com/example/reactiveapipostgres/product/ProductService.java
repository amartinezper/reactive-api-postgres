package com.example.reactiveapipostgres.product;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Flux<Product> findAll() {
        return repository.findAll();
    }

    public Mono<Product> findById(Long id) {
        return repository.findById(id);
    }

    public Mono<Product> create(Product product) {
        Product toSave = new Product(null, product.name(), product.price());
        return repository.save(toSave);
    }

    public Mono<Product> update(Long id, Product product) {
        return repository.findById(id)
                .flatMap(existing -> repository.save(new Product(id, product.name(), product.price())));
    }

    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }

    public Flux<Product> searchByName(String name) {
        return repository.searchByName(name);
    }
}
