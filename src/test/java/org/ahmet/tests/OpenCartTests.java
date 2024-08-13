package org.ahmet.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenCartTests extends BaseTest {

    private void handleHumanVerification() {
        page.waitForSelector("selector-for-human-verification", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.DETACHED));
    }

    private void waitForElement(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(60000));
    }

    @Test
    public void shouldLoadHomePage() {
        page.navigate("https://demo.opencart.com/");
        handleHumanVerification();
        assertEquals("Your Store", page.title());
    }

    @Test
    public void shouldNavigateToProductCategory() {
        page.navigate("https://demo.opencart.com/");
        handleHumanVerification();
        page.click("text=Desktops");
        handleHumanVerification();
        waitForElement(page.locator("a.list-group-item:has-text('Mac (1)')"));
        handleHumanVerification();
        page.click("a.list-group-item:has-text('Mac (1)')", new Page.ClickOptions().setTimeout(60000));
        handleHumanVerification();
        assertEquals("https://demo.opencart.com/en-gb/catalog/desktops/mac", page.url().split("\\?")[0]);
    }

    @Test
    public void shouldAddProductToCart() {
        page.navigate("https://demo.opencart.com/");
        handleHumanVerification();
        page.click("text=Desktops");
        handleHumanVerification();
        waitForElement(page.locator("a.list-group-item:has-text('Mac (1)')"));
        handleHumanVerification();
        page.click("a.list-group-item:has-text('Mac (1)')", new Page.ClickOptions().setTimeout(60000));
        handleHumanVerification();
        page.click("text=iMac");
        handleHumanVerification();
        page.click("text=Add to Cart");
        handleHumanVerification();
        waitForElement(page.locator(".alert-success"));
        assertEquals(true, page.locator(".alert-success").textContent().contains("Success: You have added iMac to your shopping cart!"));
    }
}