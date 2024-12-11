package com.akichou.mysqlwithjpa.service;

import com.akichou.mysqlwithjpa.component.ConsistencyComponent;
import com.akichou.mysqlwithjpa.entity.dto.*;
import com.akichou.mysqlwithjpa.repository.ProductRepository;
import com.akichou.mysqlwithjpa.exception.ProductNotFoundException;
import com.akichou.mysqlwithjpa.entity.Product;
import com.akichou.mysqlwithjpa.entity.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "products")
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository ;
    private final ConsistencyComponent consistencyComponent ;

    @Override
    @Cacheable(key = "#productId")
    public ResponseEntity<ProductVo> getProductById(Long productId) {

        Product product = fetchProduct(productId) ;

        return ResponseEntity.ok(mapProductToProductVo(product)) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<ProductVo> addProduct(ProductDto productDto) {

        Product product = Product.builder()
                .productName(productDto.productName())
                .description(productDto.description())
                .build() ;

        Product savedProduct = productRepository.save(product) ;

        consistencyComponent.ensureConsistency(savedProduct) ;

        return ResponseEntity.ok(mapProductToProductVo(savedProduct)) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CachePut(key = "#result.body.id()")
    public ResponseEntity<ProductVo> updateProduct(ProductUpdateDto productUpdateDto) {

        Product product = fetchProduct(productUpdateDto.id()) ;

        product.setProductName(productUpdateDto.productName()) ;
        product.setDescription(productUpdateDto.description()) ;
        Product savedProduct = productRepository.save(product) ;

        consistencyComponent.ensureConsistency(savedProduct) ;

        return ResponseEntity.ok(mapProductToProductVo(savedProduct)) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<List<ProductVo>> addProducts(List<ProductDto> productDtoList) {

        List<Product> productList = productDtoList.stream()
                .map(dto -> Product.builder()
                        .productName(dto.productName())
                        .description(dto.description())
                        .build())
                .collect(Collectors.toList()) ;

        List<Product> savedProductList = productRepository.saveAll(productList) ;

        consistencyComponent.ensureConsistency(savedProductList) ;

        return ResponseEntity.ok(savedProductList.stream()
                    .map(this::mapProductToProductVo)
                    .toList()) ;
    }

    @Override
    @Cacheable(key = "#productKeywordDto.keyword() + ':' + #productKeywordDto.page() + ':' + #productKeywordDto.size()")
    public ResponseEntity<List<ProductVo>> getProductByKeyword(ProductKeywordDto productKeywordDto) {

        Pageable pageable =
                PageRequest.of(productKeywordDto.page() - 1, productKeywordDto.size()) ;

        Page<Product> products = productRepository.searchProductByKeyword(productKeywordDto.keyword(), pageable) ;

        return ResponseEntity.ok(products.stream()
                .map(this::mapProductToProductVo)
                .toList()) ;
    }

    @Override
    @Cacheable(key = "#productExactNameDto.productName() + ':' + #productExactNameDto.page() + ':' + #productExactNameDto.size()")
    public ResponseEntity<List<ProductVo>> getProductsByExactName(ProductExactNameDto productExactNameDto) {

        Pageable pageable =
                PageRequest.of(productExactNameDto.page() - 1, productExactNameDto.size()) ;

        Page<Product> products = productRepository.searchByProductNameExact(productExactNameDto.productName(), pageable) ;

        return ResponseEntity.ok(products.stream()
                .map(this::mapProductToProductVo)
                .toList()) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = "#result.body.id()")
    public ResponseEntity<ProductVo> deleteProduct(Long productId) {

        Product product = fetchProduct(productId) ;

        productRepository.delete(product) ;

        consistencyComponent.ensureConsistencyNoMerge(product) ;

        return ResponseEntity.ok(mapProductToProductVo(product)) ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public ResponseEntity<List<ProductVo>> deleteProducts(ProductDeleteDto productDeleteDto) {

        List<Product> products = productRepository.findAllById(productDeleteDto.productIds()) ;

        productRepository.deleteAll(products) ;

        consistencyComponent.ensureConsistencyNoMerge(products) ;

        return ResponseEntity.ok(products.stream()
                .map(this::mapProductToProductVo)
                .toList()) ;
    }

    private ProductVo mapProductToProductVo(Product product) {

        return new ProductVo(product.getId(), product.getProductName(), product.getDescription()) ;
    }

    private Product fetchProduct(Long productId) {

        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product [ ID = {} ] not found", productId)) ;
    }
}
