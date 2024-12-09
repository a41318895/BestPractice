package com.akichou.mysqlwithjpa.repository;

import com.akichou.mysqlwithjpa.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page ;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT p FROM Product p WHERE
            p.productName LIKE %:keyword% OR p.description LIKE %:keyword%""")
    Page<Product> searchProductByKeyword(@Param("keyword") String keyword, Pageable pageable) ;

    @Query(nativeQuery = true,
           value = "SELECT * FROM Product p WHERE p.product_name = :productName",
    	   countQuery = "SELECT count(*) FROM Product p WHERE p.product_name = :productName")
    Page<Product> searchByProductNameExact(@Param("productName") String productName, Pageable pageable) ;
}
