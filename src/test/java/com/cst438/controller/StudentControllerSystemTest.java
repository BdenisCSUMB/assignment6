package com.cst438.controller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentControllerSystemTest {

    // TODO edit the following to give the location and file name
    // of the Chrome driver.
    //  for WinOS the file name will be chromedriver.exe
    //  for MacOS the file name will be chromedriver
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "C:/chromedriver-win64/chromedriver.exe";

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
    public void systemTestAddEnrollment() throws Exception {
        // enroll in a section for cst363 Fall 2024 term
        // verify section shows on the schedule for Fall 2024
        // drop the section
        // verify the student is no longer enrolled in the section

        /* enroll in a section for cst363 Fall 2024 term */

        // click link to navigate to open sections
        WebElement we = driver.findElement(By.id("qaddcourse"));
        we.click();
        Thread.sleep(SLEEP_DURATION);

        // find and click button to add a section
        driver.findElement(By.id("qonAdd")).click();
        Thread.sleep(SLEEP_DURATION);

        // find the YES to confirm button
        List<WebElement> confirmButtons = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons.size());
        confirmButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        String message = driver.findElement(By.id("qaddMessage")).getText();
        assertTrue(message.startsWith("section added"));

        /* verify section shows on the schedule for Fall 2024 */

        // click link to navigate to schedule
        WebElement we2 = driver.findElement(By.id("qschedule"));
        we2.click();
        Thread.sleep(SLEEP_DURATION);

        // enter year as 2024 and semester as Spring
        driver.findElement(By.id("year")).sendKeys("2024");
        driver.findElement(By.id("semester")).sendKeys("Fall");

        // find and click button to get schedule
        driver.findElement(By.id("qgetschedule")).click();
        Thread.sleep(SLEEP_DURATION);

        /* drop the section */

        // find and click button to drop
        driver.findElement(By.id("qdrop")).click();
        Thread.sleep(SLEEP_DURATION);

        // find the YES to confirm button
        List<WebElement> confirmButtons2 = driver
                .findElement(By.className("react-confirm-alert-button-group"))
                .findElements(By.tagName("button"));
        assertEquals(2, confirmButtons2.size());
        confirmButtons2.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        String message2 = driver.findElement(By.id("qdropMessage")).getText();
        assertTrue(message2.startsWith("course dropped"));

        /* verify the student is no longer enrolled in the section */
        // additional sleep time to view schedule and confirm
        Thread.sleep(SLEEP_DURATION);
    }
}
