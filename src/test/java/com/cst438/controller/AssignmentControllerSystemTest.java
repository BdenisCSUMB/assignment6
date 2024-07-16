package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentControllerSystemTest {

    // TODO edit the following to give the location and file name
    // of the Chrome driver.
    //  for WinOS the file name will be chromedriver.exe
    //  for MacOS the file name will be chromedriver
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver_win64/chromedriver.exe";

    //public static final String CHROME_DRIVER_FILE_LOCATION =
    //        "~/chromedriver_macOS/chromedriver";
    public static final String URL = "http://localhost:3000";

    public static final int SLEEP_DURATION = 1000; // 1 second.


    // add selenium dependency to pom.xml

    // these tests assumes that test data does NOT contain any
    // sections for course cst499 in 2024 Spring term.

    WebDriver driver;

    @BeforeEach
    public void setUpDriver() throws Exception {

        // set properties required by Chrome Driver
        System.setProperty(
                "webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        // start the driver
        driver = new ChromeDriver(ops);

        driver.get(URL);
        // must have a short wait to allow time for the page to download
        Thread.sleep(SLEEP_DURATION);

    }

    @AfterEach
    public void terminateDriver() {
        if (driver != null) {
            // quit driver
            driver.close();
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void systemTestAddAssignment() throws Exception {
        // add a assignment for cst363 Spring 2024 term
        // verify assignment shows on the list of assignments for Spring 2024
        // delete the assignment
        // verify the assignment is gone


        // get element for link /sections
        WebElement we = driver.findElement(By.id("isections"));

        // enter cst499, 2024, Spring and click search sections
        driver.findElement(By.id("iyear")).sendKeys("2024");
        driver.findElement(By.id("isemester")).sendKeys("Spring");

        // click link to /sections
        we.click();
        Thread.sleep(SLEEP_DURATION);

        // find section 8, cst363 in list
        // throw exception if not found
        try{
            while(true) {
                WebElement row363 = driver.findElement(By.xpath("//tr[td='cst363' and td='8']"));
                List<WebElement> links = row363.findElements(By.tagName("a"));
                // assignments is the second link
                assertEquals(2, links.size());
                links.get(1).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (NoSuchElementException e) {
            System.out.println("'cst363 section 8' not found");
        }

        // verify that assignment "test" is not in the list of assignments
        // if it exists, then delete it
        // Selenium throws NoSuchElementException when the element is not found
        try {
            while (true) {
                WebElement atest = driver.findElement(By.xpath("//tr[td='test']"));
                List<WebElement> buttons = atest.findElements(By.tagName("button"));
                // delete is the second button
                assertEquals(3, buttons.size());
                buttons.get(2).click();
                Thread.sleep(SLEEP_DURATION);
                // find the YES to confirm button
                List<WebElement> confirmButtons = driver
                        .findElement(By.className("react-confirm-alert-button-group"))
                        .findElements(By.tagName("button"));
                assertEquals(2, confirmButtons.size());
                confirmButtons.get(0).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (NoSuchElementException e) {
            // do nothing, continue with test
        }

        // find and click button to add an assignment
        driver.findElement(By.id("addAssignment")).click();
        Thread.sleep(SLEEP_DURATION);

        // enter data
        //  title: test, dueDate: 2024-5-5
        driver.findElement(By.name("title")).sendKeys("test");
        //  secId: 1,
        driver.findElement(By.name("dueDate")).sendKeys("2024-5-5");
        // click Save
        driver.findElement(By.id("asave")).click();
        Thread.sleep(SLEEP_DURATION);

        String message = driver.findElement(By.id("addMessage")).getText();
        assertTrue(message.startsWith("Assignment created"));

        // close the dialog
        // driver.findElement(By.id("close")).click();

        // verify that new Assignment shows up on Assignment list
        // find the row for test
        WebElement atest = driver.findElement(By.xpath("//tr[td='test']"));
        List<WebElement> buttons = atest.findElements(By.tagName("button"));
        // delete is the third button
        assertEquals(3, buttons.size());
        buttons.get(2).click();
        Thread.sleep(SLEEP_DURATION);
        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);
        //verify test assignment deleted
        message = driver.findElement(By.id("addMessage")).getText();
        assertTrue(message.startsWith("Assignment deleted"));

        // verify that test is no longer in the list
        assertThrows(NoSuchElementException.class, () ->
                driver.findElement(By.xpath("//tr[td='test']")));

    }
}