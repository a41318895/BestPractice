package com.akichou.mysqlwithjpa.component;

import com.akichou.mysqlwithjpa.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ConsistencyComponent {

    @PersistenceContext
    private EntityManager entityManager ;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ensureConsistency(List<Product> products) {

        entityManager.flush() ;

        entityManager.clear() ;
    }
}
