package dev.products.products;

import dev.products.products.dto.CreateProductRequest;
import dev.products.products.dto.PatchProductRequest;
import dev.products.products.dto.ProductsListResponse;
import dev.products.products.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class ProductsService {

    private final ProductsRepository productsRepository;

    public ProductResponse createProduct(CreateProductRequest request, Long userId) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.category(),
                userId
        );
        Product savedProduct = productsRepository.save(product);

        ProductResponse response = ProductResponse.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .stock(savedProduct.getStock())
                .category(savedProduct.getCategory())
                .userId(savedProduct.getUserId())
                .build();

        // HATEOAS 링크
        response.add(linkTo(methodOn(ProductsController.class)
                .getProduct(savedProduct.getId())).withSelfRel());

        // profile (문서 링크)
        response.add(Link.of("/docs/index.html#product-create").withRel("profile"));

        // list-products (templated)
        response.add(Link.of(
                "http://localhost:8080/api/products?page=0&size=10{&category}"
        ).withRel("list-products").withType("GET"));

        // update
        response.add(Link.of(
                "http://localhost:8080/api/products/" + product.getId()
        ).withRel("update-product").withType("PUT"));

        // delete
        response.add(Link.of(
                "http://localhost:8080/api/products/" + product.getId()
        ).withRel("delete-product").withType("DELETE"));

        return response;
    }

    public ProductResponse getProduct(Long id) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 상품"));

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .userId(product.getUserId())
                .build();

        // HATEOAS 링크
        response.add(linkTo(methodOn(ProductsController.class)
                .getProduct(product.getId())).withSelfRel());

        // profile (문서 링크)
        response.add(Link.of("/docs/index.html#product-create").withRel("profile"));

        /*
        인증된 사용자 중에서 자신이 등록한 상품일 경우, update, delete 링크도 추가 응답
        인증된 사용자이고, 상품의 재고가 있을 경우 상품을 주문할 수 있는 order 링크도 추가 응답
         */

        return response;
    }

    public ProductsListResponse getProducts(String category, Integer page, Integer size) {
        int currentPage = (page == null || page < 0) ? 0 : page;
        int currentSize = (size == null || size <= 0) ? 10 : size;

        Pageable pageable = PageRequest.of(currentPage, currentSize);

        Page<Product> productPage;
        if (category == null || category.isBlank()) {
            productPage = productsRepository.findAll(pageable);
        } else {
            productPage = productsRepository.findByCategory(category, pageable);
        }

        List<ProductResponse> products = productPage.getContent().stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .category(product.getCategory())
                        .userId(product.getUserId())
                        .build())
                .toList();

        ProductsListResponse response = ProductsListResponse.builder()
                .products(products)
                .build();

        // profile
        response.add(Link.of("/swagger-ui/index.html").withRel("profile").withType("GET"));

        // self
        response.add(buildPageLink(category, productPage.getNumber(), productPage.getSize(), "self"));

        // next
        if (productPage.hasNext()) {
            response.add(buildPageLink(category, productPage.getNumber() + 1, productPage.getSize(), "next"));
        }

        // prev
        if (productPage.hasPrevious()) {
            response.add(buildPageLink(category, productPage.getNumber() - 1, productPage.getSize(), "prev"));
        }

        return response;
    }

    private Link buildPageLink(String category, int page, int size, String rel) {
        String href;
        if (category == null || category.isBlank()) {
            href = "/api/products?page=" + page + "&size=" + size;
        } else {
            href = "/api/products?page=" + page + "&size=" + size + "&category=" + category;
        }

        return Link.of(href).withRel(rel).withType("GET");
    }

    public ProductResponse updateProduct(Long id, PatchProductRequest request) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 상품"));

        if (request.name() != null) {
            product.setName(request.name());
        }

        if (request.description() != null) {
            product.setDescription(request.description());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (request.stock() != null) {
            product.setStock(request.stock());
        }

        if (request.category() != null) {
            product.setCategory(request.category());
        }

        productsRepository.save(product);

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .userId(product.getUserId())
                .build();

        // self
        response.add(linkTo(methodOn(ProductsController.class)
                .getProduct(product.getId()))
                .withSelfRel()
                .withType("GET"));

        // profile
        response.add(Link.of("/swagger-ui/index.html")
                .withRel("profile"));

        // list-products (templated)
        response.add(Link.of("http://localhost:8080/api/products?page=0&size=10{&category}")
                .withRel("list-products")
                .withType("GET"));

        // delete-product
        response.add(Link.of("http://localhost:8080/api/products/" + product.getId())
                .withRel("delete-product")
                .withType("DELETE"));

        return response;
    }

    public ProductResponse deleteProduct(Long id) {
        Product product = productsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 상품"));

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .userId(product.getUserId())
                .build();

        // self
        response.add(linkTo(methodOn(ProductsController.class)
                .getProduct(product.getId()))
                .withSelfRel()
                .withType("GET"));

        // profile
        response.add(Link.of("/swagger-ui/index.html")
                .withRel("profile"));

        // list-products (templated)
        response.add(Link.of("http://localhost:8080/api/products?page=0&size=10{&category}")
                .withRel("list-products")
                .withType("GET"));

        productsRepository.delete(product);

        return response;
    }
}
