package org.ahmet.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @BeforeEach
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
        handleHumanVerification(page);
    }

    @AfterEach
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    private void handleHumanVerification(Page page) {
        // Add logic to handle CAPTCHA or human verification
        // This might include waiting for a specific element or solving the CAPTCHA
        // For example, wait for a specific element to disappear
        page.waitForSelector("selector-for-human-verification", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.DETACHED));
    }
}