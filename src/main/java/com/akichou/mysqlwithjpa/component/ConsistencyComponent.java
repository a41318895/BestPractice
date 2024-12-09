package com.akichou.mysqlwithjpa.component;

import com.akichou.mysqlwithjpa.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ConsistencyComponent {

    @PersistenceContext
    private EntityManager entityManager ;

    @Transactional
    public void ensureConsistency(Product product) {

        entityManager.merge(product) ;

        // Make pending changes committed to database
        entityManager.flush() ;

        // Clear entities from EntityManager.
        // It represents that EntityManager don't manage the status modification of entities.
        entityManager.clear() ;
    }

    @Transactional
    public void ensureConsistencyNoMerge(Product product) {

        // Make pending changes committed to database
        entityManager.flush() ;

        // Clear entities from EntityManager.
        // It represents that EntityManager don't manage the status modification of entities.
        entityManager.clear() ;
    }

    @Transactional
    public void ensureConsistency(List<Product> products) {

        products.forEach(entityManager::merge) ;

        entityManager.flush() ;

        entityManager.clear() ;
    }

    @Transactional
    public void ensureConsistencyNoMerge(List<Product> products) {

        entityManager.flush() ;

        entityManager.clear() ;
    }
}
