package repository;

import model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository implements IRepository<Order> {
    private final List<Order> orders = new ArrayList<>();

    @Override
    public List<Order> getAll() {
        return orders;
    }

    @Override
    public void add(Order order) {
        if (order != null && getById(order.id) == null) {
            this.orders.add(order);
        }
    }

    @Override
    public void getInfo(int id) {
        for (Order order : orders) {
            if (order.id == id) {
                System.out.printf("""
                    Order Details:
                    ID: %d
                    Customer ID: %d
                    Price: %.2f
                    Date: %s
                    """,
                        order.id,
                        order.idCustomer,
                        order.price,
                        order.orderedDate
                );
                return;
            }
        }
    }

    @Override
    public Order delete(int id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).id == id) {
                return orders.remove(i);
            }
        }
        return null;
    }

    @Override
    public int getMaxId() {
        int rc = -1;
        for (Order order : orders) {
            if (order.id > rc) {
                rc = order.id;
            }
        }
        return rc;
    }

    @Override
    public Order getById(int id) {
        for (Order order : orders) {
            if (order.id == id) {
                return order;
            }
        }
        return null;
    }
}
