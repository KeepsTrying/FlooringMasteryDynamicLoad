/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sguild.flooringmastery1.dao;

import com.sguild.flooringmastery1.dto.Order;
import com.sguild.flooringmastery1.dto.Product;
import com.sguild.flooringmastery1.dto.State;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 *
 * @author apprentice
 */
public interface FMDao {
    
    
    public void saveFiles() throws FMDataTransferException;
    
    public List<String> listAllOrderFiles();
    
    public List<String> listAllOrderDates();
    
    public Order addOrder(Order newOrder) throws FMDataTransferException;
    
    public Order removeOrder(Order orderToRemove) throws FMDataTransferException;
    
    public Map<Integer, Order> getAllOrdersFromDate(LocalDate requestedDate) throws FMDataTransferException;
    
    public Map<Integer, Order> getAllOrdersFromMap(LocalDate requestedDate) throws FMDataTransferException;
    
    public Order getOrderFromDate(LocalDate orderDate, int orderNum) throws FMDataTransferException;
    
    public List<Product> getAllProducts() throws FMDataTransferException;
    
    public Product getProduct(String productName)  throws FMDataTransferException;
    
    public void loadStateTaxes() throws FMDataTransferException;
    
    public State getState(String stateCode) throws FMDataTransferException;
    
}
