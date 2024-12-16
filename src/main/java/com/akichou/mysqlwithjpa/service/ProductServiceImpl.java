package com.akichou.mysqlwithjpa.service;

import com.akichou.mysqlwithjpa.component.ConsistencyComponent;
import com.akichou.mysqlwithjpa.entity.dto.*;
import com.akichou.mysqlwithjpa.repository.ProductRepository;
import com.akichou.mysqlwithjpa.exception.ProductNotFoundException;
import com.akichou.mysqlwithjpa.entity.Product;
import com.akichou.mysqlwithjpa.entity.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "products")
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository ;
    private final ConsistencyComponent consistencyComponent ;

    @Override
    @Cacheable(key = "#productId")
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<ProductVo> getProductById(Long productId) {

        Product product = fetchProduct(productId) ;

        return ResponseEntity.ok(mapProductToProductVo(product)) ;
    }

    @Override
    @CachePut(key = "#result.body.id()")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<ProductVo> addProduct(ProductDto productDto) {

        try {
            Product product = Product.builder()
                    .productName(productDto.productName())
                    .description(productDto.description())
                    .build();

            Product savedProduct = productRepository.save(product);

            return ResponseEntity.ok(mapProductToProductVo(savedProduct));
        } catch (Exception e) {

            log.error("Error occurred while adding product: {}", e.getMessage());

            throw e ;
        }
    }

    @Override
    @CacheEvict(key = "#result.body.id()")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<ProductVo> updateProduct(ProductUpdateDto productUpdateDto) {

        try {
            Product product = fetchProduct(productUpdateDto.id());

            product.setProductName(productUpdateDto.productName());
            product.setDescription(productUpdateDto.description());
            Product savedProduct = productRepository.save(product);

            return ResponseEntity.ok(mapProductToProductVo(savedProduct));
        } catch (Exception e) {

            log.error("Error occurred while updating product: {}", e.getMessage());

            throw e ;
        }
    }

    @Override
    @CachePut(key = "#result.body.?[true].id()")
    @Transactional(rollbackFor = Exception.class,
                   isolation = Isolation.READ_COMMITTED,
                   propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<List<ProductVo>> addProducts(List<ProductDto> productDtoList) {

        try {
            List<Product> productList = productDtoList.stream()
                    .map(dto -> Product.builder()
                            .productName(dto.productName())
                            .description(dto.description())
                            .build())
                    .collect(Collectors.toList());

            List<Product> savedProductList = productRepository.saveAll(productList);

            // Avoid OOM
            consistencyComponent.ensureConsistency(savedProductList);

            return ResponseEntity.ok(savedProductList.stream()
                    .map(this::mapProductToProductVo)
                    .toList());
        } catch (Exception e) {

            log.error("Error occurred while adding products: {}", e.getMessage());

            throw e ;
        }
    }

    @Override
    @Cacheable(key = "#result.body.?[true].id()")
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<List<ProductVo>> getProductByKeyword(ProductKeywordDto productKeywordDto) {

        Pageable pageable =
                PageRequest.of(productKeywordDto.page() - 1, productKeywordDto.size()) ;

        Page<Product> products = productRepository.searchProductByKeyword(productKeywordDto.keyword(), pageable) ;

        return ResponseEntity.ok(products.stream()
                .map(this::mapProductToProductVo)
                .toList()) ;
    }

    @Override
    @Cacheable(key = "#result.body.?[true].id()")
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<List<ProductVo>> getProductsByExactName(ProductExactNameDto productExactNameDto) {

        Pageable pageable =
                PageRequest.of(productExactNameDto.page() - 1, productExactNameDto.size()) ;

        Page<Product> products = productRepository.searchByProductNameExact(productExactNameDto.productName(), pageable) ;

        return ResponseEntity.ok(products.stream()
                .map(this::mapProductToProductVo)
                .toList()) ;
    }

    @Override
    @CacheEvict(key = "#result.body.id()")
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public ResponseEntity<ProductVo> deleteProduct(Long productId) {

        try {
            Product product = fetchProduct(productId);

            productRepository.delete(product);

            return ResponseEntity.ok(mapProductToProductVo(product));
        } catch (Exception e) {

            log.error("Error occurred while deleting product: {}", e.getMessage());

            throw e ;
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class,
                   isolation = Isolation.READ_COMMITTED,
                   propagation = Propagation.REQUIRES_NEW)
    public ResponseEntity<List<ProductVo>> deleteProducts(ProductDeleteDto productDeleteDto) {

        try {
            List<Product> products = productRepository.findAllById(productDeleteDto.productIds());

            productRepository.deleteAll(products);

            // Avoid OOM
            consistencyComponent.ensureConsistency(products);

            return ResponseEntity.ok(products.stream()
                    .map(this::mapProductToProductVo)
                    .toList());
        } catch (Exception e) {

            log.error("Error occurred while deleting products: {}", e.getMessage());

            throw e ;
        }
    }

    private ProductVo mapProductToProductVo(Product product) {

        return new ProductVo(product.getId(), product.getProductName(), product.getDescription()) ;
    }

    private Product fetchProduct(Long productId) {

        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product [ ID = {} ] not found", productId)) ;
    }
}
