package service;

import model.Customer;
import model.Dish;
import model.Order;
import repository.IRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final IRepository<Order> repository;
    private final double minOrderPrice;

    public OrderService(IRepository<Order> repository, double minOrderPrice) {
        this.repository = repository;
        this.minOrderPrice = minOrderPrice;
    }

    public Order createOrder(Customer customer, List<Dish> dishes) {
        if (customer == null || dishes == null || dishes.isEmpty()) {
            throw new IllegalArgumentException("Данные заказа заполнены неверно");
        }

        double orderPrice = 0;
        for (Dish dish : dishes) {
            orderPrice += dish.price;
        }

        if (orderPrice < minOrderPrice) {
            throw new IllegalArgumentException(
                    String.format(
                            "Сумма заказа ({0}) меньше допустимой минимальной суммы ({1})",
                            orderPrice,
                            minOrderPrice)
                    );
        }

        if (customer.cash < orderPrice) {
            logger.warn("Недостаточно средств у клиента {}. Баланс: {}, сумма заказа: {}", customer.id, customer.cash, orderPrice);
            throw new IllegalArgumentException(
                    String.format(
                            "Не хватает средств ({0}) для оплаты заказа на сумму {1}",
                            customer.cash,
                            orderPrice)
            );
        }

        customer.cash -= orderPrice;
        int id = repository.getMaxId() + 1;
        Order order = new Order(id, customer.id, dishes);

        try {
            repository.add(order);
        } catch (RuntimeException e) {
            logger.error("Ошибка сохранения заказа клиента {}", customer.id, e);
            throw e;
        }

        logger.info("Заказ №{} успешно оформлен. Клиент: {}, сумма: {}", order.id, customer.id, order.price);

        return order;
    }

    public String testMethod() {
        StringBuilder sb = new StringBuilder("String var2 =");

        for (int i = 1; i < 5; i++) {
            sb.append(" ").append(i);
        }

        return sb.toString();
    }
}
