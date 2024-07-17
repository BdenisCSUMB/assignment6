package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GradeControllerSystemTest {

    // TODO edit the following to give the location and file name
    // of the Chrome driver.
    //  for WinOS the file name will be chromedriver.exe
    //  for MacOS the file name will be chromedriver
    public static final String CHROME_DRIVER_FILE_LOCATION =
            "/Users/valentinahanna/Downloads/chromedriver-mac-arm64/chromedriver";

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
    public void systemTestGradeAssignment() throws Exception {

        // get element for link /sections
        WebElement we = driver.findElement(By.id("isections"));

        // enter cst499, 2024, Spring and click search sections
        driver.findElement(By.id("iyear")).sendKeys("2024");
        driver.findElement(By.id("isemester")).sendKeys("Fall");

        // click link to /sections
        we.click();
        Thread.sleep(SLEEP_DURATION);

        try{
            while(true) {
                WebElement row363 = driver.findElement(By.xpath("//tr[td='cst363' and td='11']"));
                List<WebElement> links = row363.findElements(By.tagName("a"));
                // assignments is the second link
                assertEquals(2, links.size());
                links.get(1).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (NoSuchElementException e) {
            System.out.println("'cst363 section 11' not found");
        }

        WebElement assignmentRow = driver.findElement(By.xpath("//tr[td='db test homework']"));
        List<WebElement> gradeButtons = assignmentRow.findElements(By.tagName("button"));
        assertEquals(3, gradeButtons.size());
        gradeButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        List<WebElement> scoreInputs = driver.findElements(By.id("score-input"));
        for(WebElement scoreInput : scoreInputs) {
            scoreInput.clear();
            scoreInput.sendKeys("33");
        }

        WebElement uploadButton = driver.findElement(By.id("upload-scores"));
        uploadButton.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement successMessage = driver.findElement(By.id("gradeMessage"));
        assertTrue(successMessage.getText().contains("Grades saved"));

    }

    @Test
    public void systemTestFinalClassGrades() throws Exception {

        // get element for link /sections
        WebElement we = driver.findElement(By.id("isections"));

        // enter cst499, 2024, Spring and click search sections
        driver.findElement(By.id("iyear")).sendKeys("2024");
        driver.findElement(By.id("isemester")).sendKeys("Fall");

        // click link to /sections
        we.click();
        Thread.sleep(SLEEP_DURATION);

        try{
            while(true) {
                WebElement row363 = driver.findElement(By.xpath("//tr[td='cst363' and td='11']"));
                List<WebElement> links = row363.findElements(By.tagName("a"));
                // enrollments is the first link
                assertEquals(2, links.size());
                links.get(0).click();
                Thread.sleep(SLEEP_DURATION);
            }
        } catch (NoSuchElementException e) {
            System.out.println("'cst363 section 11' not found");
        }

        List<WebElement> gradeInputs = driver.findElements(By.id("grade-input"));
        for (WebElement gradeInput : gradeInputs) {
            gradeInput.clear();
            gradeInput.sendKeys("A");
        }

        WebElement submitButton = driver.findElement(By.tagName("button"));
        submitButton.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement successMessage = driver.findElement(By.id("submit-success-message"));
        assertTrue(successMessage.getText().contains("Grades saved"));
    }

}
