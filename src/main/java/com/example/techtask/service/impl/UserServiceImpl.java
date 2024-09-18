package com.example.techtask.service.impl;

import java.util.List;
import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.OrderStatus;
import com.example.techtask.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final String FIND_USERS_QUERY = """
                    SELECT u
                    FROM User u
                    JOIN Order o
                        ON u.id = o.userId
                    WHERE EXTRACT(YEAR FROM o.created_at) = :year
                        AND
                          CAST(o.orderStatus AS text) = CAST(:orderStatus AS text)
                    GROUP BY u.id
                """;
    private static final String FIND_USER_QUERY =
                FIND_USERS_QUERY + """
                    ORDER BY sum(o.price * o.quantity) DESC
                    LIMIT 1
                """;
    private static final String PARAM_YEAR = "year";
    private static final String PARAM_ORDER_STATUS = "orderStatus";
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User findUser() {
        TypedQuery<User> query = entityManager.createQuery(FIND_USER_QUERY, User.class)
                .setParameter(PARAM_YEAR, 2003)
                .setParameter(PARAM_ORDER_STATUS, OrderStatus.DELIVERED.toString());
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public List<User> findUsers() {
        return entityManager.createQuery(FIND_USERS_QUERY, User.class)
                .setParameter(PARAM_YEAR, 2010)
                .setParameter(PARAM_ORDER_STATUS, OrderStatus.PAID.toString())
                .getResultList();
    }

}
