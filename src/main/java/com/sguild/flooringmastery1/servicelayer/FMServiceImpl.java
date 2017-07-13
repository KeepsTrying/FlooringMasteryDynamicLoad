/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sguild.flooringmastery1.servicelayer;

import com.sguild.flooringmastery1.dao.FMDao;
import com.sguild.flooringmastery1.dto.Order;
import com.sguild.flooringmastery1.dto.Product;
import com.sguild.flooringmastery1.dao.FMDataTransferException;
import com.sguild.flooringmastery1.dto.State;
import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author apprentice
 */
public class FMServiceImpl implements FMService {

    public FMDao dao;

    public FMServiceImpl(FMDao dao) {
        this.dao = dao;
    }

    @Override
    public Order completeOrder(Order newOrder) {
        BigDecimal materialsCost, laborCost, ttlTax, ttlCost,
                costSqFt, laborSqFt, taxRate, area, subtotal;

        area = newOrder.getArea();

        costSqFt = newOrder.getMaterial().getMaterialCostSqFt();
        newOrder.setCostPerSqFt(costSqFt);

        laborSqFt = newOrder.getMaterial().getLaborCostSqFt();
        newOrder.setLaborCostPerSqFt(laborSqFt);

        taxRate = newOrder.getState().getStateTax();
        newOrder.setTaxRate(taxRate);

        materialsCost = costSqFt.multiply(area);
        newOrder.setMatCost(materialsCost);

        laborCost = laborSqFt.multiply(area);
        newOrder.setLaborCost(laborCost);

        subtotal = materialsCost.add(laborCost);

        if (taxRate.compareTo(ZERO) <= 0) {
            ttlCost = subtotal;
            ttlTax = new BigDecimal("0.00").setScale(2, HALF_UP);
        } else {
            ttlTax = subtotal.multiply(taxRate);
            newOrder.setTtlTax(ttlTax);
            ttlCost = subtotal.add(ttlTax).setScale(2, HALF_UP);
        }
        newOrder.setTtlCost(ttlCost);

        return newOrder;
    }

    @Override
    public void saveFiles() throws FMDataTransferException {
        dao.saveFiles();
    }

    @Override
    public List<String> listAllOrderDates() {
        return dao.listAllOrderDates();
    }

    @Override
    public List<String> listAllOrderFiles() {
        List<String> allTxtFiles = new ArrayList<>();
        List<String> orderDates = new ArrayList<>();
        allTxtFiles = dao.listAllOrderFiles();

        for (String fileName : allTxtFiles) {
            if (fileName.contains("Orders_")) {
                String date = fileName.substring(7, 9) + "-" + fileName.substring(9, 11) + "-" + fileName.substring(11);
                orderDates.add(date);
            }
        }
        return orderDates;
    }

    @Override
    public Order addOrder(Order newOrder) throws FMDataTransferException {
        return dao.addOrder(newOrder);
    }

    @Override
    public Order removeOrder(Order orderToRemove) throws FMDataTransferException {
        return dao.removeOrder(orderToRemove);
    }

    @Override
    public Map<Integer, Order> getAllOrdersFromDate(LocalDate requestedDate) throws FMDataTransferException {
        Map ordersFromDate = new HashMap<>();
        try {
            ordersFromDate = dao.getAllOrdersFromDate(requestedDate);
        } catch (FMDataTransferException ex) {
            System.out.println("Error loading Orders.  Please check that the date is applicable.");
        }
        return ordersFromDate;
    }

    @Override
    public Order getOrderFromDate(LocalDate orderDate, int orderNum) throws FMDataTransferException {
        return dao.getOrderFromDate(orderDate, orderNum);
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        try {
            productList = dao.getAllProducts();
        } catch (FMDataTransferException ex) {
            System.out.println("Products.txt not found.");
        }
        return productList;
    }

    @Override
    public Product getProduct(String productName) throws FMDataTransferException {
        return dao.getProduct(productName);
    }

    @Override
    public void loadStateTaxes() {
        try {
            dao.loadStateTaxes();
        } catch (FMDataTransferException ex) {
            System.out.println("Unable to load state taxes.  Please confirm the file Taxes.txt exists.");
        }
    }

    @Override
    public State getState(String stateCode) throws FMDataTransferException {
        return dao.getState(stateCode);
    }

}
