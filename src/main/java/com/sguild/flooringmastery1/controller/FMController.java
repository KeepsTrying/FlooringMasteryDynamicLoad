/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sguild.flooringmastery1.controller;

import com.sguild.flooringmastery1.dao.FMDataTransferException;
import com.sguild.flooringmastery1.dto.Order;
import com.sguild.flooringmastery1.servicelayer.FMService;
import com.sguild.flooringmastery1.ui.FMView;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author apprentice
 */
public class FMController {
    
    public FMView view;
    public FMService alfred;
    
    public FMController(FMService service, FMView view) {
        this.alfred = service;
        this.view = view;
    }
    
    public void run() {
        boolean keepGoing = true;
        int mainMenuSelection = 0;
        try {
            alfred.loadStateTaxes();
            alfred.getAllProducts();
        } catch (FMDataTransferException e) {
            view.displayErrorMessage(e.getLocalizedMessage());
        }
        alfred.listAllOrderFiles();
        
        while (keepGoing) {
            
            mainMenuSelection = printMainMenuAndGetSelection();
            
            switch (mainMenuSelection) {
                
                case 1:
                    listDates();
                    keepGoing = true;
                    break;
                case 2:
                    listOrdersFromDate();
                    keepGoing = true;
                    break;
                case 3:
                    addOrder();
                    keepGoing = true;
                    break;
                case 4:
                    System.out.println("edit an order");
                    keepGoing = true;
                    break;
                case 5:
                    removeOrder();
                    keepGoing = true;
                    break;
                case 6:
                    System.out.println("Thanks for remembering to save!");
                    save();
                    keepGoing = true;
                    break;
                case 7:
                    keepGoing = false;
                    System.out.println("Exiting. . . ");
                    break;
                default:
                    unknownCommand();
            }
        }
    }
    
    private int printMainMenuAndGetSelection() {
        return view.printMainMenuAndGetSelection();
    }
    
    private void listDates() {
        view.listApplicableDates(alfred.listAllOrderDates());
    }
    
    private void listOrdersFromDate() {
        Map<Integer, Order> orders = new HashMap<>();
        try {
            orders = alfred.getAllOrdersFromDate(view.getDate("What Date's orders would you like to view?"));
        } catch (FMDataTransferException ex) {
            view.displayErrorMessage(ex.getLocalizedMessage());
        }
        view.listOrdersFromDate(orders);
    }
    
    private void unknownCommand() {
        view.displayBanner("Unknown Command");
    }
    
    private void addOrder() {
        Order newOrder = new Order();
        newOrder = view.addOrder();
        String materialRequest;
        boolean valid = false;
        
        while (!valid) {
            try {
                newOrder.setState(alfred.getState(view.requestState()));
                valid = true;
            } catch (FMDataTransferException | NullPointerException e) {
                valid = false;
                view.displayErrorMessage("Please enter a 2 letter state code.");
            }
        }
        
        valid = false;
        
        while (!valid) {
            try {
                materialRequest = view.displayAndRequestProduct(alfred.getAllProducts());
                newOrder.setMaterial(alfred.getProduct(materialRequest));
                valid = true;
            } catch (FMDataTransferException ex) {
                valid = false;
                view.displayErrorMessage(ex.getLocalizedMessage());
            }
            
        }
        
        newOrder = alfred.completeOrder(newOrder);
        try {
            alfred.addOrder(newOrder);
            view.displayBanner("Order for " + newOrder.getCustomer() + " Added");
        } catch (FMDataTransferException ex) {
            view.displayErrorMessage(ex.getLocalizedMessage());
        }
        
    }
    
    private void removeOrder() {
        view.displayBanner("Removing an Order");
        LocalDate removeFromDate = view.getDate("What date was the order placed?");
        
        try {
            view.listOrdersFromDate(alfred.getAllOrdersFromDate(removeFromDate));
        } catch (FMDataTransferException e) {
            view.displayErrorMessage("Invoice date does not exist.");
        }
        
        int orderNum = view.requestOrderNumber("Which order would you like to remove?");
        Order removeThisOrder = new Order();
        try {
            removeThisOrder = alfred.getOrderFromDate(removeFromDate, orderNum);
        } catch (FMDataTransferException e) {
            view.displayErrorMessage("Order " + orderNum + " does not exist.");
        }
        
        boolean willRemove = view.displayOrderRequestChange(removeThisOrder);
        if (willRemove) {
            try {
                alfred.removeOrder(removeThisOrder);
                view.displayBanner("Order Removed");
            } catch (FMDataTransferException e) {
                view.displayErrorMessage(e.getLocalizedMessage());
            }
        } else {
            view.displayBanner("Order Retained");
        }
    }
    
    private void save() {
        try {
            alfred.saveFiles();
        } catch (FMDataTransferException e) {
            view.displayErrorMessage("Could not persist data to storage. Please check storage is available.");
        }
    }
}
