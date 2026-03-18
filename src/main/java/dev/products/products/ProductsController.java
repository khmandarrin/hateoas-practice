package dev.products.products;

import dev.products.products.dto.CreateProductRequest;
import dev.products.products.dto.PatchProductRequest;
import dev.products.products.dto.ProductsListResponse;
import dev.products.products.dto.ProductResponse;
import dev.products.auth.support.CustomUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "상품")
public class ProductsController {
    private final ProductsService productsService;

    @PostMapping
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 생성 성공"),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 (Validation 실패)"),

            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),

            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @Operation(summary = "상품 등록", description = "인증된 사용자만 상품 등록 가능")
    public ResponseEntity<ProductResponse> createProducts(
            @Valid @RequestBody CreateProductRequest productsRequest,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
    ProductResponse productsResponse = productsService.createProduct(productsRequest, principal.getId());

    return new ResponseEntity<>(productsResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @Operation(summary = "상품 상세 조회", description = "상품 id로 조회")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {

        ProductResponse productsResponse = productsService.getProduct(id);

        return new ResponseEntity<>(productsResponse, HttpStatus.OK);
    }

    @GetMapping
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 조회 성공"),
    })
    @Operation(summary = "상품 목록 조회", description = "상품 카테고리별 필터링, 페이징 (옵셔널)")
    public ResponseEntity<ProductsListResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {

        ProductsListResponse productsResponseList = productsService.getProducts(category, page, size);

        return new ResponseEntity<>(productsResponseList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 수정 성공"),

            @ApiResponse(responseCode = "400", description = "잘못된 요청 (Validation 실패)"),

            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),

            @ApiResponse(responseCode = "403", description = "권한 없음"),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @Operation(summary = "상품 수정", description = "인증된 사용자 중에서 자신이 등록한 상품만 수정 가능")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody PatchProductRequest productsRequest, @AuthenticationPrincipal CustomUserPrincipal principal) {

        ProductResponse productsResponse = productsService.updateProduct(id, productsRequest, principal.getId());

        return new ResponseEntity<>(productsResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 제거 성공 (204는 응답body가 안 내려옴)"),

            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품"),

            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),

            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @Operation(summary = "상품 삭제", description = "인증된 사용자 중에서 자신이 등록한 상품만 삭제 가능")
    public ResponseEntity<ProductResponse> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        ProductResponse productResponse = productsService.deleteProduct(id, principal.getId());

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
}
