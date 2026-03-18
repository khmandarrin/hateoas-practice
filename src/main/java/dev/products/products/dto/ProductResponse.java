package dev.products.products.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ProductResponse extends RepresentationModel<ProductResponse> {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private String category;

    private Long userId;
}
