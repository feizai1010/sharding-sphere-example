package io.shardingsphere.example.repository.api.service;

import io.shardingsphere.example.repository.api.entity.Order;
import io.shardingsphere.example.repository.api.entity.OrderItem;
import io.shardingsphere.example.repository.api.repository.OrderItemRepository;
import io.shardingsphere.example.repository.api.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CommonServiceImpl implements CommonService {
    
    @Override
    public void initEnvironment() {
        System.out.println("初始化环境之开始创建表--------------------");
        getOrderRepository().createTableIfNotExists();
        getOrderItemRepository().createTableIfNotExists();
        System.out.println("初始化环境之清空表中的记录--------------------");
        getOrderRepository().truncateTable();
        getOrderItemRepository().truncateTable();
    }
    
    @Override
    public void cleanEnvironment() {
        getOrderItemRepository().dropTable();
        getOrderItemRepository().dropTable();
    }
    
    @Transactional
    @Override
    public void processSuccess() {
        System.out.println("-------------- 开始插入数据 ---------------");
        List<Long> orderIds = insertData();;
        System.out.println("--------------插入数据完成 --------------");
        printData();
        //deleteData(orderIds);
        printData();

    }
    
    @Transactional
    @Override
    public void processFailure() {
        System.out.println("-------------- Process Failure Begin ---------------");
        insertData();
        System.out.println("-------------- Process Failure Finish --------------");
        throw new RuntimeException("Exception occur for transaction test.");
    }
    
    private List<Long> insertData() {
        System.out.println("--------------------开始插入数据---------------------");
        List<Long> result = new ArrayList<>(10);
        Random random=new Random();
        for (int i = 1; i <= 10; i++) {
            int orderId= random.nextInt(1000);
            Order order = newOrder();
            order.setUserId(i);
            order.setOrderId(orderId);
            order.setStatus("插入测试");
            getOrderRepository().insert(order);
            OrderItem item = newOrderItem();
            item.setOrderId(order.getOrderId());
            item.setUserId(i);
            item.setStatus("插入测试");
            getOrderItemRepository().insert(item);
            result.add(order.getOrderId());
        }
        return result;
    }
    
    private void deleteData(final List<Long> orderIds) {
        System.out.println("---------------------------- Delete Data ----------------------------");
        for (Long each : orderIds) {
            getOrderRepository().delete(each);
            getOrderItemRepository().delete(each);
        }
    }
    
    @Override
    public void printData() {
        System.out.println("---------------------------- Print Order Data -----------------------");
        System.out.println("查询order表中的数据"+getOrderRepository().selectAll().size());
        System.out.println("---------------------------- Print OrderItem Data -------------------");
        System.out.println(getOrderItemRepository().selectAll());
    }
    
    protected abstract OrderRepository getOrderRepository();
    
    protected abstract OrderItemRepository getOrderItemRepository();
    
    protected abstract Order newOrder();
    
    protected abstract OrderItem newOrderItem();
}
