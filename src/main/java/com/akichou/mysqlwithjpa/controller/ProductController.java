package com.akichou.mysqlwithjpa.controller;

import com.akichou.mysqlwithjpa.entity.dto.*;
import com.akichou.mysqlwithjpa.service.ProductService;
import com.akichou.mysqlwithjpa.entity.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService ;

    @GetMapping
    public ResponseEntity<ProductVo> getProductById(@RequestParam("productId") Long productId) {

        return productService.getProductById(productId) ;
    }

    @PostMapping
    public ResponseEntity<ProductVo> addProduct(@Validated @RequestBody ProductDto productDto) {

        return productService.addProduct(productDto) ;
    }

    @PostMapping("/multi")
    public ResponseEntity<List<ProductVo>> addProducts(@Validated @RequestBody List<ProductDto> productDtoList) {

        return productService.addProducts(productDtoList) ;
    }

    @PostMapping("/keyword")
    public ResponseEntity<List<ProductVo>> getProductsByKeyword(@Validated @RequestBody ProductKeywordDto productKeywordDto) {

        return productService.getProductByKeyword(productKeywordDto) ;
    }

    @PostMapping("/exact")
    public ResponseEntity<List<ProductVo>> getProductsByExactName(@Validated @RequestBody ProductExactNameDto productExactNameDto) {

        return productService.getProductsByExactName(productExactNameDto) ;
    }

    @PutMapping
    public ResponseEntity<ProductVo> updateProduct(@Validated @RequestBody ProductUpdateDto productUpdateDto) {

        return productService.updateProduct(productUpdateDto) ;
    }

    @DeleteMapping
    public ResponseEntity<ProductVo> deleteProduct(@RequestParam("productId") Long productId) {

        return productService.deleteProduct(productId) ;
    }

    @DeleteMapping("/multi")
    public ResponseEntity<List<ProductVo>> deleteProducts(@Validated @RequestBody ProductDeleteDto productDeleteDto) {

        return productService.deleteProducts(productDeleteDto) ;
    }
}
