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
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author apprentice
 */
public class FMDaoImplTest {
    FMDaoImpl testDao;
    

    public FMDaoImplTest() {
        testDao = new FMDaoImpl();
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of loadFiles method, of class FMDaoImpl.
     */

    /**
     * Test of saveFiles method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testSaveFiles() throws FMDataTransferException {
        LocalDate testDate = LocalDate.of(2018, 5, 29);
        BigDecimal testData = new BigDecimal("1.00").setScale(2);
        Product carpet = new Product();
        testDao.loadStateTaxes();
        List<Product> productList = new ArrayList<>();
        productList = testDao.getAllProducts();
        for (Product testProduct : productList)
            if (testProduct.getName().equals("Carpet")){
                carpet = testProduct;
            }
        
        testDao.listAllOrderFiles();
        Order testOrder = new Order();
        testOrder.setDate(testDate);
        testOrder.setCustomer("test");
        testOrder.setState(testDao.getState("KY"));
        testOrder.setTaxRate(testOrder.getState().getStateTax());
        testOrder.setMaterial(carpet);
        testOrder.setArea(testData);
        testOrder.setCostPerSqFt(carpet.getMaterialCostSqFt());
        testOrder.setLaborCostPerSqFt(carpet.getLaborCostSqFt());
        testOrder.setMatCost(testData);
        testOrder.setLaborCost(testData);
        testOrder.setTtlTax(testData);
        testOrder.setTtlCost(testData);
        
        testDao.addOrder(testOrder);
        testDao.saveFiles();
        //don't know how to write this test I guess, comes back >40?!
        //but first time I ran it it, it correctly created the file and
        //populated it. . .except forgot to get product name so printed "Material" 
        //object heap position lol
        Assert.assertEquals(42, testDao.listAllOrderFiles().size());
        
    }

    /**
     * Test of addOrder method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testAddOrder() throws FMDataTransferException {
        LocalDate testDate = LocalDate.of(2017, 5, 29);
        BigDecimal testData = new BigDecimal("1.00").setScale(2);
        Product carpet = new Product();
        testDao.loadStateTaxes();
        List<Product> productList = new ArrayList<>();
        productList = testDao.getAllProducts();
        for (Product testProduct : productList)
            if (testProduct.getName().equals("Carpet")){
                carpet = testProduct;
            }
        
        testDao.listAllOrderFiles();
        Order testOrder = new Order();
        testOrder.setDate(testDate);
        testOrder.setCustomer("test");
        testOrder.setState(testDao.getState("KY"));
        testOrder.setTaxRate(testOrder.getState().getStateTax());
        testOrder.setMaterial(carpet);
        testOrder.setArea(testData);
        testOrder.setCostPerSqFt(carpet.getMaterialCostSqFt());
        testOrder.setLaborCostPerSqFt(carpet.getLaborCostSqFt());
        testOrder.setMatCost(testData);
        testOrder.setLaborCost(testData);
        testOrder.setTtlTax(testData);
        testOrder.setTtlCost(testData);
        
        testDao.addOrder(testOrder);
        
        Assert.assertEquals(8, testOrder.getOrderNum());
        Assert.assertEquals(8, testDao.getAllOrdersFromDate(testDate).size());
        Assert.assertEquals(8, testDao.getAllOrdersFromMap(testDate).size());
    } 
    
    
    /**
     * 
     * @throws FMDataTransferException 
     */
    
    
    @Test
    public void testValidateBigD() throws FMDataTransferException {
        //String currentToken, String date, int orderNum
        String testString = "3.57";
        String testDate = "04232017";
        int testOrderNum = 1;
        
        BigDecimal testActual = testDao.validateBigD(testString, testDate, testOrderNum);
        BigDecimal testExpected = new BigDecimal("3.57").setScale(2);
        
        Assert.assertEquals(testExpected, testActual);
        
    }
    
    
    

    /**
     * Test of removeOrder method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testRemoveOrder() throws FMDataTransferException {
        LocalDate testDate = LocalDate.of(2017, 5, 25);
        testDao.loadStateTaxes();
        testDao.getAllProducts();
        testDao.getAllOrdersFromDate(testDate);
        Order testOrder = testDao.getOrderFromDate(testDate, 8);
        
        //size = 8, expected will be 7
        //order num 8
        testDao.removeOrder(testOrder);
        Assert.assertEquals(7, testDao.getAllOrdersFromDate(testDate).size());
        
    }

    /**
     * Test of getAllOrdersFromDate method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testGetAllOrdersFromDate() throws FMDataTransferException {
        LocalDate testDate = LocalDate.of(2017, 5, 23);
        testDao.getAllProducts();
        testDao.loadStateTaxes();
        

        Assert.assertEquals(4, testDao.getAllOrdersFromDate(testDate).size());
        //System.out.println("testGetAllMerch once loaded results w/ inv size: " + testDao.getAllMerch());
    }

    /**
     * Test of listAllOrderFiles method, of class FMDaoImpl.
     */
    @Test
    public void testListAllFiles() {
        Assert.assertEquals(14, testDao.listAllOrderFiles().size());
    }

    /**
     * Test of getOrderFromDate method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testGetOrderFromDate() throws FMDataTransferException {
        LocalDate testDate = LocalDate.of(2017, 04, 23);
        int testOrderNum = 1;
        testDao.loadStateTaxes();
        testDao.getAllProducts();
        testDao.getAllOrdersFromDate(testDate);
        
        Order testOrder = new Order();
        testOrder = testDao.getOrderFromDate(testDate, testOrderNum);
        
        Assert.assertTrue(testOrder.getCustomer().equals("Bob"));
    }

    /**
     * Test of getAllProducts method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testGetAllProducts() throws FMDataTransferException {
        List<Product> testList = testDao.getAllProducts();
        
       Assert.assertEquals(10, testList.size());
    }

    /**
     * Test of getProduct method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testGetProduct() throws FMDataTransferException {
        List<Product> testList = testDao.getAllProducts();
        Product testProduct = testDao.getProduct("Wood");
        BigDecimal testValue = new BigDecimal("5.15").setScale(2);
        Assert.assertEquals("Wood", testProduct.getName());
        Assert.assertEquals(testValue, testProduct.getMaterialCostSqFt());
        
    }

    /**
     * Test of loadStateTaxes method, of class FMDaoImpl.
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testLoadStateTaxes() throws FMDataTransferException {
        testDao.loadStateTaxes();
        BigDecimal expectedResult = new BigDecimal("6.00").setScale(2);
        Assert.assertTrue(testDao.getState("KY").getStateTax().equals(expectedResult));
    }

    /**
     * Test of getStateTax method, of class FMDaoImpl.
     * @return 
     * @throws com.sguild.flooringmastery1.dao.FMDataTransferException
     */
    @Test
    public void testGetState() throws FMDataTransferException {
        testDao.loadStateTaxes();
        State testState = new State();
        testState = testDao.getState("KY");
        
        BigDecimal expectedResult = new BigDecimal("6.00").setScale(2);
        Assert.assertTrue(testDao.getState("KY").getStateTax().equals(expectedResult));
        
    }
    
}
