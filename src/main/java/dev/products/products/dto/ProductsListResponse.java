package dev.products.products.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Builder
public class ProductsListResponse extends RepresentationModel<ProductsListResponse> {

    private List<ProductResponse> products;
}