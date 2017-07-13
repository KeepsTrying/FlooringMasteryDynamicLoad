/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sguild.flooringmastery1.ui;

import com.sguild.flooringmastery1.dao.FMDataTransferException;
import com.sguild.flooringmastery1.dto.Order;
import com.sguild.flooringmastery1.dto.Product;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author apprentice
 */
public class FMView {

    private UserIO io;

    public FMView(UserIO io) {
        this.io = io;
    }

    public void displayErrorMessage(String errorMsg) {
        io.print("=== ERROR ===");
        io.print(errorMsg);
    }

    public int printMainMenuAndGetSelection() {

        displayBanner("Main Menu");

        io.print("1)  Display Applicable Dates");
        io.print("2)  Display Orders From Date");
        io.print("3)  Add an Order");
        io.print("4)  Edit an Order");
        io.print("5)  Remove an Order");
        io.print("6)  Save Current Work");
        io.print("7)  Exit");
        io.print("");

        return io.readInt("Please enter selection, 1-7", 1, 7);
    }

    public void displayBanner(String bannerTitle) {
        io.print("=== " + bannerTitle + " ===");
        io.print("---------------------------");
    }

    public void listApplicableDates(List<String> orderDates) {
        for (String orderDate : orderDates) {
            System.out.println(orderDate);
        }
        io.print("");
    }

    public LocalDate getDate(String prompt) {
        LocalDate requestedDate = null;
        boolean incorrect = false;

        do {
            io.print(prompt);
            int month = io.readInt("Please enter the month 1-12", 1, 12);
            int day = io.readInt("Please enter the day of the month 1-(28-31)", 1, 31);
            int year = io.readInt("Please enter the four digit year, 1970-2020", 1970, 2020);
            try {
                requestedDate = LocalDate.of(year, month, day);
                incorrect = false;
            } catch (DateTimeException e) {
                System.out.println("Please input a valid date.");
                incorrect = true;
            }
        } while (incorrect);
        return requestedDate;
    }

    public void listOrdersFromDate(Map<Integer, Order> mapOfOrders) {
        Set<Integer> keySet = mapOfOrders.keySet();
        io.print("");
        for (int key : keySet) {
            io.print("--------------------------------------------------------------------------------");
            io.print("Order # " + key + "   Customer Name: " + mapOfOrders.get(key).getCustomer()
                    + "          State / Tax Rate: " + mapOfOrders.get(key).getState().getStateCode() + " / " + mapOfOrders.get(key).getState().getStateTax());
            io.print(mapOfOrders.get(key).getArea() + " sqFt of " + mapOfOrders.get(key).getMaterial().getName() + " at "
                    + mapOfOrders.get(key).getLaborCostPerSqFt().add(mapOfOrders.get(key).getCostPerSqFt()) + " per feet installed");
            io.print("Invoice Total:   $" + mapOfOrders.get(key).getTtlCost());
            io.print("--------------------------------------------------------------------------------");
        }
        io.print("");
    }

    public Order addOrder() {
        displayBanner("Create New Order");
        LocalDate orderDate = getDate("Please Date the Order Invoice.");
        Order newOrder = new Order();
        newOrder.setDate(orderDate);
        newOrder.setCustomer(io.readString("What is the Customer's Name?"));
        newOrder.setArea(io.readBigD("How many square feet are they looking to cover?"));

        //still need to set state
        return newOrder;
    }

    public String displayAndRequestProduct(List<Product> productList) {
        io.print("");
        io.print("Material    |   Cost / sqFt    |   Labor / sqFt");
        for (Product singleProduct : productList) {
            io.print(singleProduct.getName() + "          " + singleProduct.getMaterialCostSqFt() + "         " + singleProduct.getLaborCostSqFt());
        }
        io.print("");

        return io.readString("What Material are they looking for?");
    }

    public String requestState() {
        return io.readString("What state is it being delivered to?  Input 2 letter state abbreviation.");
    }
    
    public int requestOrderNumber(String prompt){
        int orderNum = io.readInt(prompt);
        return orderNum;
    }
    
    public boolean displayOrderRequestChange(Order requestedRemoval) {
        io.print("--------------------------------------------------------------------------------");
            io.print("Order # " + requestedRemoval.getOrderNum() + "   Customer Name: " + requestedRemoval.getCustomer()
                    + "         State / Tax Rate: " + requestedRemoval.getState().getStateCode() + " / " + requestedRemoval.getState().getStateTax());
            io.print(requestedRemoval.getArea() + " sqFt of " + requestedRemoval.getMaterial().getName() + " at "
                    + requestedRemoval.getLaborCostPerSqFt().add(requestedRemoval.getCostPerSqFt()) + " per feet installed");
            io.print("Invoice Total:   $" + requestedRemoval.getTtlCost());
            io.print("--------------------------------------------------------------------------------");
            boolean reply = io.readIntBoolean("Are you sure you want to delete this record?  1) Yes   2) No");
            return reply;
    }

}
