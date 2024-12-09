package com.akichou.mysqlwithjpa.service;

import com.akichou.mysqlwithjpa.entity.dto.*;
import com.akichou.mysqlwithjpa.entity.vo.ProductVo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {

    ResponseEntity<ProductVo> getProductById(Long productId);

    ResponseEntity<ProductVo> addProduct(ProductDto productDto);

    ResponseEntity<ProductVo> updateProduct(ProductUpdateDto productUpdateDto);

    ResponseEntity<List<ProductVo>> addProducts(List<ProductDto> productDtoList);

    ResponseEntity<List<ProductVo>> getProductByKeyword(ProductKeywordDto productKeywordDto);

    ResponseEntity<List<ProductVo>> getProductsByExactName(ProductExactNameDto productExactNameDto);

    ResponseEntity<ProductVo> deleteProduct(Long productId);

    ResponseEntity<List<ProductVo>> deleteProducts(ProductDeleteDto productDeleteDto);
}
