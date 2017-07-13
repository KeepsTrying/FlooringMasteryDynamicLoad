/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sguild.flooringmastery1.dao;

import com.sguild.flooringmastery1.dto.Order;
import com.sguild.flooringmastery1.dto.Product;
import com.sguild.flooringmastery1.dto.State;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author apprentice
 */
public class FMDaoImpl implements FMDao {

    private static final String DELIMITER = ",";

    private List<Order> listOfRemovedOrders = new ArrayList<>();

    private List<String> listOfOrderFiles = new ArrayList<>();

    private List<String> listOfOrderDates = new ArrayList<>();

    private Map<Integer, Order> ordersFromDate = new HashMap<>();

    private Map<LocalDate, Map<Integer, Order>> atlas = new HashMap<>();

    //private Map<String, BigDecimal> mapOfStates = new HashMap<>();
    private List<State> listOfStates = new ArrayList<>();
    
    private List<Product> productList = new ArrayList<>();

    @Override
    public void saveFiles() throws FMDataTransferException {
        PrintWriter save;
        DateTimeFormatter MMddyyyy = DateTimeFormatter.ofPattern("MMddyyyy");

        Set<LocalDate> titan = atlas.keySet();
        String stringDate;
        String fileName;
        Set<Integer> orderNums;
        for (LocalDate eachDate : titan) {
            stringDate = eachDate.format(MMddyyyy);
            fileName = "Orders_" + stringDate + ".txt";

            try {
                save = new PrintWriter(new FileWriter(fileName));
            } catch (IOException e) {
                throw new FMDataTransferException("Could not save Inventory", e);
            }

            ordersFromDate = atlas.get(eachDate);
            orderNums = ordersFromDate.keySet();
            for (int orderNum : orderNums) {
                Order thisOrder = new Order();
                thisOrder = ordersFromDate.get(orderNum);
                save.println(
                        thisOrder.getOrderNum() + DELIMITER + thisOrder.getCustomer() + DELIMITER
                        + thisOrder.getState().getStateCode() + DELIMITER + thisOrder.getTaxRate() + DELIMITER
                        + thisOrder.getMaterial().getName() + DELIMITER + thisOrder.getArea() + DELIMITER
                        + thisOrder.getCostPerSqFt() + DELIMITER + thisOrder.getLaborCostPerSqFt() + DELIMITER
                        + thisOrder.getMatCost() + DELIMITER + thisOrder.getLaborCost() + DELIMITER
                        + thisOrder.getTtlTax() + DELIMITER + thisOrder.getTtlCost());
                save.flush();
            }
            save.close();
        }
    }

    @Override
    public Order addOrder(Order newOrder) throws FMDataTransferException {
        LocalDate orderDate = newOrder.getDate();
        DateTimeFormatter MMddyyyy = DateTimeFormatter.ofPattern("MMddyyyy");
        String stringOrderDate = orderDate.format(MMddyyyy);
        int orderNum = newOrder.getOrderNum();
        //try to populate ordersFromDate map
        if (!listOfOrderDates.contains(stringOrderDate)) {
            listOfOrderDates.add(stringOrderDate);
        } //else {
        try {
            ordersFromDate = getAllOrdersFromDate(newOrder.getDate());
            Set<Integer> keySet = ordersFromDate.keySet();
            //if you can populate a keyset and orderNum hasn't been set yet, make it the next order
            if (orderNum == 0) {
                for (int keys : keySet) {
                    if (keys >= orderNum) {
                        orderNum = keys + 1;
                    }
                }
                newOrder.setOrderNum(orderNum);
                //else user has input orderNum, check to see there isn't a matching number
            } else {
                for (int key : keySet) {
                    if (orderNum == key) {
                        // if user tries to put in an order number that already exists, exception is thrown
                        throw new FMDataTransferException("Order Number " + orderNum + " already exists.");
                    }
                }
            }
            //if doesn't exist, create new map and set orderNum
        } catch (FMDataTransferException | NullPointerException e) {
            Map<Integer, Order> ordersFromNewOrderDate = new HashMap<>();
            if (orderNum > 0) {
                newOrder.setOrderNum(orderNum);
            } else {
                newOrder.setOrderNum(1);
            }
            ordersFromNewOrderDate.put(orderNum, newOrder);
            atlas.put(newOrder.getDate(), ordersFromNewOrderDate);
            return newOrder;
        }

        // add order to map
        ordersFromDate.put(orderNum, newOrder);
        //add map to map of days with orders
        atlas.put(newOrder.getDate(), ordersFromDate);
        return newOrder;
    }

    @Override
    public Order removeOrder(Order orderToRemove) throws FMDataTransferException {
        LocalDate orderDate = orderToRemove.getDate();
        int orderNum = orderToRemove.getOrderNum();
        ordersFromDate.remove(orderNum, orderToRemove);
        atlas.put(orderDate, ordersFromDate);
        listOfRemovedOrders.add(orderToRemove);

        return orderToRemove;
    }

    @Override
    public Map<Integer, Order> getAllOrdersFromDate(LocalDate requestedDate) throws FMDataTransferException {
            DateTimeFormatter MMddyyyy = DateTimeFormatter.ofPattern("MMddyyyy");
        String stringDate = requestedDate.format(MMddyyyy);
        String fileName = "";
        listOfOrderFiles = listAllOrderFiles();

        for (String orderFile : listOfOrderFiles) {
            if (orderFile.contains("Orders_" + stringDate)) {
                fileName = orderFile + ".txt";
            }
        }

        Scanner ordersReader = null;

        try {
            ordersReader = new Scanner(new BufferedReader(new FileReader(fileName)));
            String currentLine;
            String[] currentTokens;
            boolean haveErrors = false;
            int orderNum = 0;

            while (ordersReader.hasNextLine()) {
                currentLine = ordersReader.nextLine();
                currentTokens = currentLine.split(DELIMITER);
                if (currentTokens.length == 12) {

                    Order currentOrder = new Order();

                    try {
                        currentOrder.setOrderNum(Integer.parseInt(currentTokens[0]));
                        orderNum = currentOrder.getOrderNum();
                    } catch (NumberFormatException e) {
                        haveErrors = true;
                    }

                    currentOrder.setCustomer(currentTokens[1]);

                    currentOrder.setState(getState(currentTokens[2]));
                    currentOrder.setTaxRate(validateBigD(currentTokens[3], stringDate, orderNum));
                    currentOrder.setDate(requestedDate);
                    currentOrder.setMaterial(getProduct(currentTokens[4]));
                    currentOrder.setArea(validateBigD(currentTokens[5], stringDate, orderNum));
                    currentOrder.setCostPerSqFt(validateBigD(currentTokens[6], stringDate, orderNum));
                    currentOrder.setLaborCostPerSqFt(validateBigD(currentTokens[7], stringDate, orderNum));
                    currentOrder.setMatCost(validateBigD(currentTokens[8], stringDate, orderNum));
                    currentOrder.setLaborCost(validateBigD(currentTokens[9], stringDate, orderNum));
                    currentOrder.setTtlTax(validateBigD(currentTokens[10], stringDate, orderNum));
                    currentOrder.setTtlCost(validateBigD(currentTokens[11], stringDate, orderNum));

                    ordersFromDate.put(orderNum, currentOrder);

                }
            }
            ordersReader.close();

            if (atlas.containsKey(requestedDate)) {
                int mapKey;
                Map<Integer, Order> ramMap = new HashMap<>();
                ramMap = atlas.get(requestedDate);
                Set<Integer> keys = ramMap.keySet();
                for (int key : keys) {
                    ordersFromDate.put(key, ramMap.get(key));
                }

            }

            //if there are removed orders in memory
            if (listOfRemovedOrders.size() > 0) {
                LocalDate removedDate;
                //for each order check to see if order dates match
                for (Order eachOrder : listOfRemovedOrders) {
                    removedDate = eachOrder.getDate();
                    //if orderDates match, remove order
                    if (removedDate == requestedDate) {
                        ordersFromDate.remove(eachOrder.getOrderNum());
                    }
                }

                /*
                Set removedKeys = mapOfRemovedOrders.keySet();
                for (Object keyDate : removedKeys)
                    dateAsString = keyDate.toString();
                removedDate = LocalDate.parse(dateAsString);
                
                //ordersFromDate.remove(mapOfRemovedOrders.get(removedDate));
                 */
            }
            atlas.put(requestedDate, ordersFromDate);

        } catch (FileNotFoundException e) {
            try {
                Map<Integer, Order> dateMap = getAllOrdersFromMap(requestedDate);
                atlas.put(requestedDate, dateMap);
                return dateMap;
            } catch (FMDataTransferException r) {
                throw new FMDataTransferException(r.getLocalizedMessage());
            }
            // throw new FMDataTransferException("Could not find applicable date.");
        }

        Map<Integer, Order> returnMap = new HashMap<>();
        returnMap = atlas.get(requestedDate);

        return returnMap;
    }

    @Override
    public Map<Integer, Order> getAllOrdersFromMap(LocalDate requestedDate) throws FMDataTransferException {
        Map<Integer, Order> requestedDateMap = new HashMap<>();
        try {
            requestedDateMap = atlas.get(requestedDate);
        } catch (NullPointerException e) {
            throw new FMDataTransferException("Date not found.");
        }
        return requestedDateMap;
    }

    public BigDecimal validateBigD(String currentToken, String date, int orderNum) throws FMDataTransferException {
        double bigDAsDouble;
        BigDecimal fullBigD, scaledBigD;

        try {
            bigDAsDouble = Double.parseDouble(currentToken);
            fullBigD = new BigDecimal(currentToken);
            scaledBigD = fullBigD.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new FMDataTransferException("Improper field found of Date " + date + " in Order# " + orderNum + ", " + currentToken);
        }
        return scaledBigD;
    }

    private BigDecimal validateBigD(String currentToken, String productOrStateName, boolean isProduct) throws FMDataTransferException {
        double bigDAsDouble;
        BigDecimal fullBigD, scaledBigD;

        try {
            bigDAsDouble = Double.parseDouble(currentToken);
            fullBigD = new BigDecimal(currentToken);
            scaledBigD = fullBigD.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            if (isProduct) {
                throw new FMDataTransferException("Improper field found in Material " + productOrStateName);
            } else {
                throw new FMDataTransferException("Improper field found in State " + productOrStateName);
            }
        }
        return scaledBigD;
    }

    @Override
    public List<String> listAllOrderFiles() {
        File folder = new File("/home/apprentice/NetBeansProjects/FlooringMastery1");
        File[] arrayOfFileObjects = folder.listFiles();

        for (File file : arrayOfFileObjects) {
            if (file.isFile()) {
                String fileName = file.getName();
                String txtExtension = ".txt";
                if (fileName.endsWith(txtExtension)) {
                    listOfOrderFiles.add(fileName.substring(0, fileName.length() - txtExtension.length()));
                    listOfOrderDates.add(fileName.substring(0, fileName.length() - txtExtension.length()));
                }
            }
        }
        return listOfOrderFiles;
    }

    @Override
    public List<String> listAllOrderDates() {
        return listOfOrderDates;
    }

    @Override
    public Order getOrderFromDate(LocalDate orderDate, int orderNum) throws FMDataTransferException {
        //Map<Integer, Order> ordersFromDate = new HashMap<>();
        //ordersFromDate = getAllOrdersFromDate(orderDate);
        //atlas.put(orderDate, ordersFromDate);

        Order requestedOrder = new Order();
        try {
            requestedOrder = ordersFromDate.get(orderNum);
        } catch (NullPointerException e) {
            throw new FMDataTransferException("date not found");
        }

        return requestedOrder;
    }

    @Override
    public List<Product> getAllProducts() throws FMDataTransferException {

        Scanner productReader;

        try {
            productReader = new Scanner(new BufferedReader(new FileReader("Products.txt")));
        } catch (FileNotFoundException e) {
            throw new FMDataTransferException("Could not load Product Inventory");
        }

        String currentLine;
        String[] currentTokens;

        while (productReader.hasNextLine()) {
            currentLine = productReader.nextLine();
            currentTokens = currentLine.split(DELIMITER);

            Product currentProduct = new Product();
            boolean isProduct = true;

            currentProduct.setName(currentTokens[0]);
            currentProduct.setMaterialCostSqFt(validateBigD(currentTokens[1], currentTokens[0], isProduct));
            currentProduct.setLaborCostSqFt(validateBigD(currentTokens[2], currentTokens[0], isProduct));

            productList.add(currentProduct);

        }
        productReader.close();
        return productList;
    }

    @Override
    public Product getProduct(String productName) throws FMDataTransferException {

        Product requestedProduct = new Product();
        boolean hasProduct = false;

        for (Product singleProduct : productList) {
            if (singleProduct.getName().equalsIgnoreCase(productName)) {
                hasProduct = true;
                requestedProduct = singleProduct;
            }
        }
        if (!hasProduct) {
            throw new FMDataTransferException("No such material");
        }
        return requestedProduct;
    }

    @Override
    public void loadStateTaxes() throws FMDataTransferException {

        Scanner stateReader;

        try {
            stateReader = new Scanner(new BufferedReader(new FileReader("Taxes.txt")));
        } catch (FileNotFoundException e) {
            throw new FMDataTransferException("Could not load State Tax Info.  Please confirm Taxes.txt exists.");
        }

        String currentLine;
        String[] currentTokens;

        while (stateReader.hasNextLine()) {
            currentLine = stateReader.nextLine();
            currentTokens = currentLine.split(DELIMITER);

            State currentState = new State();
            boolean isProduct = false;

            currentState.setStateCode(currentTokens[0]);
            currentState.setStateTax(validateBigD(currentTokens[1], currentTokens[0], isProduct));

           listOfStates.add(currentState);

        }
        stateReader.close();

    }

    @Override
    public State getState(String stateCode) throws FMDataTransferException {
        State requestedState = new State();
        for (State state : listOfStates) {
            if (state.getStateCode().equalsIgnoreCase(stateCode))
            requestedState = state;
        }
        
        if (requestedState.getStateCode() == null){
            throw new FMDataTransferException("Incorrect State Abbreviation.");
        }
        return requestedState;
    }
}
