package com.george.productservice.service;

import com.george.productservice.dto.ProductRequest;
import com.george.productservice.dto.ProductResponse;
import com.george.productservice.model.InventoryRequest;
import com.george.productservice.model.Product;
import com.george.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service("productService")
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient.Builder webClientBuilder;

    public String createProduct(ProductRequest productRequest){
        try{
            Product product = Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .build();
            // Make call to inventory service to create inventory
            Mono<String> response = webClientBuilder.build().post()
                    .uri("http://inventory-service/api/inventories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(InventoryRequest.builder()
                            .productId(product.getId())
                            .quantity(productRequest.getQuantity())
                            .build())
                    .retrieve()
                    .bodyToMono(String.class);

            response.subscribe(result -> {
                log.info("Product added to inventory status: {}",result);
            });


            productRepository.save(product);

            log.info("Product {} is saved", product.getId());
            return String.format("Product %s was successfully created and added into inventory", product.getName());

        }
        catch (Exception e){
            log.info(e.getMessage());
            return "Product was not successfully created and added into stock";
        }
    }

    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapProductToProductResponse).toList();
    }

    private ProductResponse mapProductToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
