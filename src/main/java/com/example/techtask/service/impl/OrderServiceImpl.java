package com.example.techtask.service.impl;

import java.util.List;
import com.example.techtask.model.Order;
import com.example.techtask.model.enumiration.UserStatus;
import com.example.techtask.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String FIND_ORDER_QUERY = """
                SELECT o
                FROM Order o
                WHERE o.quantity > 1
                ORDER BY o.createdAt DESC
                LIMIT 1
            """;
    private static final String FIND_ORDERS_QUERY = """
                SELECT o
                FROM Order o
                JOIN User u
                    ON o.userId = u.id
                WHERE CAST(u.userStatus AS text) = CAST(:userStatus AS text)
                ORDER BY o.createdAt
            """;
    private static final String PARAM_USER_STATUS = "userStatus";
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Order findOrder() {
        TypedQuery<Order> query = entityManager.createQuery(FIND_ORDER_QUERY, Order.class);
        return query.getSingleResult();
    }

    //4. Возвращать заказы от активных пользователей, отсортированные по дате создания.
    @Override
    @Transactional
    public List<Order> findOrders() {
        return entityManager.createQuery(FIND_ORDERS_QUERY, Order.class)
                .setParameter(PARAM_USER_STATUS, UserStatus.ACTIVE.toString())
                .getResultList();
    }

}
