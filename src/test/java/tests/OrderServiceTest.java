package tests;

import model.Customer;
import model.Dish;
import model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.IRepository;
import repository.OrderRepository;
import service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private IRepository<Order> repo;
    private OrderService service;

    @BeforeEach
    void setUp() {
        repo = new OrderRepository();
        service = new OrderService(repo, 300.0);
    }

    @Test
    void createOrder_Positive() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 1000.0);
        Dish dish = new Dish(1, "Стейк", new String[]{}, 500.0);

        Order createdOrder = service.createOrder(customer, List.of(dish));

        assertNotNull(createdOrder);
        assertEquals(500.0, customer.cash);
        assertEquals(1, repo.getAll().size());
    }

    @Test
    void createOrder_Negative_NotEnoughCash() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 100.0);
        Dish dish = new Dish(1, "Стейк", new String[]{}, 500.0);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createOrder(customer, List.of(dish));
        });

        assertEquals(100.0, customer.cash);
        assertEquals(0, repo.getAll().size());
    }

    @Test
    void createOrder_Negative_BelowMinOrderPrice() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 1000.0);
        Dish cheapDish = new Dish(1, "Чай", new String[]{}, 100.0);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createOrder(customer, List.of(cheapDish));
        });

        assertEquals(1000.0, customer.cash);
        assertEquals(0, repo.getAll().size());
    }

    @Test
    void createOrder_WithSeveralDishes() {
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 1000.0);

        Dish steak = new Dish(1, "Стейк", new String[]{}, 500.0);
        Dish salad = new Dish(2, "Салат", new String[]{}, 200.0);
        List<Dish> dishes = List.of(steak, salad);

        Order order = service.createOrder(customer, dishes);

        assertEquals(700.0, order.price, 0.001);
        assertEquals(300.0, customer.cash, 0.001);
        assertEquals(customer.id, order.idCustomer);
        assertIterableEquals(dishes, order.orderedDishes);
        assertEquals(1, repo.getAll().size());
        assertSame(order, repo.getById(order.id));
    }


    @Test
    void createOrder_RepositoryError() {
        IRepository<Order> brokenRepository = new OrderRepository() {
            @Override
            public void add(Order order) {
                throw new RuntimeException("Ошибка базы данных");
            }
        };

        OrderService brokenService = new OrderService(brokenRepository, 300.0);
        Customer customer = new Customer(1, "Иванов", "Иван", "Иванович", "123", 1000.0);
        Dish dish = new Dish(1, "Стейк", new String[]{}, 500.0);

        assertThrows(RuntimeException.class, () -> {
            brokenService.createOrder(customer, List.of(dish));
        });
    }

}