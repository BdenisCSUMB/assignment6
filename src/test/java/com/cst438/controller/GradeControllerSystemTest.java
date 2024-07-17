package com.cst438.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
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
            "/Users/Downloads/chromedriver-mac-arm64/chromedriver";

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

        WebElement assignmentLink = driver.findElement(By.id("assignments"));
        assignmentLink.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement assignmentRow = driver.findElement(By.xpath("//tr[td='Assignment 1']"));
        List<WebElement> gradeButtons = assignmentRow.findElements(By.tagName("button"));
        assertEquals(1, gradeButtons.size());
        gradeButtons.get(0).click();
        Thread.sleep(SLEEP_DURATION);

        List<WebElement> scoreInputs = driver.findElements(By.className("score-input"));
        for(WebElement scoreInput : scoreInputs) {
            scoreInput.clear();
            scoreInput.sendKeys("90");
        }

        WebElement uploadButton = driver.findElement(By.id("upload-scores"));
        uploadButton.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement successMessage = driver.findElement(By.id("upload-success-message"));
        assertTrue(successMessage.getText().contains("Scores uploaded successfully"));

    }

    @Test
    public void systemTestFinalClassGrades() throws Exception {

        WebElement finalGradesLink = driver.findElement(By.id("final-grades"));
        finalGradesLink.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement sectionDropdown = driver.findElement(By.id("section-dropdown"));
        sectionDropdown.sendKeys("Spring 2024");
        Thread.sleep(SLEEP_DURATION);

        List<WebElement> gradeInputs = driver.findElements(By.className("grade-input"));
        for (WebElement gradeInput : gradeInputs) {
            gradeInput.clear();
            gradeInput.sendKeys("A");
        }

        WebElement submitButton = driver.findElement(By.id("submit-grades"));
        submitButton.click();
        Thread.sleep(SLEEP_DURATION);

        WebElement successMessage = driver.findElement(By.id("submit-success-message"));
        assertTrue(successMessage.getText().contains("Grades submitted successfully"));
    }

}





