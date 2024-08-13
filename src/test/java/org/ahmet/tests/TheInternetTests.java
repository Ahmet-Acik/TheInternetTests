package org.ahmet.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Geolocation;
import com.microsoft.playwright.options.HttpCredentials;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TheInternetTests extends BaseTest {

    @Test
    public void shouldAddAndRemoveElement() {
        page.navigate("https://the-internet.herokuapp.com/");
        page.click("text=Add/Remove Elements");
        page.click("button[onclick='addElement()']");
        assertTrue(page.isVisible("button.added-manually"));
        page.click("button.added-manually");
        assertTrue(page.locator("button.added-manually").count() == 0);
    }

    @Test
    public void shouldLoginWithBasicAuth() {
        page.navigate("https://admin:admin@the-internet.herokuapp.com/basic_auth");
        assertTrue(page.isVisible("text=Congratulations! You must have the proper credentials."));
    }

    @Test
    public void shouldCheckAndUncheckCheckboxes() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
        Locator checkbox1 = page.locator("input[type='checkbox']").first();
        Locator checkbox2 = page.locator("input[type='checkbox']").last();
        checkbox1.check();
        assertTrue(checkbox1.isChecked());
        checkbox1.uncheck();
        assertFalse(checkbox1.isChecked());
        checkbox2.check();
        assertTrue(checkbox2.isChecked());
        checkbox2.uncheck();
        assertFalse(checkbox2.isChecked());
    }

    @Test
    public void shouldHandleContextMenu() {
        page.navigate("https://the-internet.herokuapp.com/context_menu");
        page.click("#hot-spot", new Page.ClickOptions().setButton(MouseButton.RIGHT));
        page.onDialog(dialog -> dialog.accept());
    }

    @Test
    public void shouldDragAndDrop() {
        page.navigate("https://the-internet.herokuapp.com/drag_and_drop");
        page.dragAndDrop("#column-a", "#column-b");
        assertEquals("B", page.locator("#column-a header").textContent());
        assertEquals("A", page.locator("#column-b header").textContent());
    }

    @Test
    public void shouldSelectFromDropdown() {
        page.navigate("https://the-internet.herokuapp.com/dropdown");
        page.selectOption("#dropdown", "1");
        assertEquals("Option 1", page.locator("#dropdown option[selected]").textContent());
        page.selectOption("#dropdown", "2");
        assertEquals("Option 2", page.locator("#dropdown option[selected]").textContent());
    }

    @Test
    public void shouldToggleDynamicControls() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");
        page.click("button[onclick='swapCheckbox()']");
        page.waitForSelector("#checkbox", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.DETACHED));
        page.click("button[onclick='swapCheckbox()']");
        page.waitForSelector("#checkbox");
        assertTrue(page.isVisible("#checkbox"));
    }

    @Test
    public void shouldUploadFile() {
        page.navigate("https://the-internet.herokuapp.com/upload");

        // Ensure the file upload element is visible
        page.waitForSelector("#file-upload", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

        // Use a relative path to the file
        String filePath = "src/test/resources/file.txt";

        // Set the file to the file upload input
        page.setInputFiles("#file-upload", Paths.get(filePath));

        // Submit the form
        page.click("#file-submit");

        // Verify the file upload success message
        assertTrue(page.isVisible("text=File Uploaded!"));
    }

    @Test
    public void shouldLoginWithFormAuthentication() {
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("#username", "tomsmith");
        page.fill("#password", "SuperSecretPassword!");
        page.click("button[type='submit']");
        assertTrue(page.isVisible("text=You logged into a secure area!"));
    }

    @Test
    public void shouldHandleJavaScriptAlerts() {
        page.navigate("https://the-internet.herokuapp.com/javascript_alerts");
        page.click("button[onclick='jsAlert()']");
        page.onDialog(dialog -> dialog.accept());
        assertTrue(page.isVisible("text=You successfully clicked an alert"));
    }

    @Test
    public void shouldHandleFrames() {
        page.navigate("https://the-internet.herokuapp.com/iframe");
        Frame frame = page.frame("mce_0_ifr");

        // Wait for the element to be visible before interacting
        frame.locator("#tinymce").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // Check if the editor is in read-only mode
        if (frame.locator("text=TinyMCE is in read-only mode").isVisible()) {
            System.out.println("Editor is in read-only mode. Skipping interaction.");
            return;
        }

        // Click inside the iframe and type text
        frame.click("#tinymce");
        frame.type("#tinymce", "Hello, World!");

        // Verify the text content
        assertEquals("Hello, World!", frame.locator("#tinymce").textContent());
    }
    @Test
    public void shouldHandleHover() {
        page.navigate("https://the-internet.herokuapp.com/hovers");
        page.hover(".figure:nth-child(3)");
        assertTrue(page.isVisible(".figcaption h5:text('name: user1')"));
    }

    @Test
    public void shouldHandleInfiniteScroll() {
        page.navigate("https://the-internet.herokuapp.com/infinite_scroll");
        for (int i = 0; i < 5; i++) {
            page.mouse().wheel(0, 1000);
            page.waitForTimeout(1000);
        }
        assertTrue(page.locator(".jscroll-added").count() > 0);
    }

    @Test
    public void shouldHandleKeyPresses() {
        page.navigate("https://the-internet.herokuapp.com/key_presses");
        page.press("body", "Enter");
        assertTrue(page.isVisible("text=You entered: ENTER"));
    }

    @Test
    public void shouldDownloadFile() {
        page.navigate("https://the-internet.herokuapp.com/download");
        Download download = page.waitForDownload(() -> {
            page.click("a[href='download/some-file.txt']");
        });
        assertEquals("some-file.txt", download.suggestedFilename());
        download.saveAs(Paths.get("downloads/some-file.txt"));
    }

    @Test
    public void shouldInterceptNetworkRequests() {
        page.route("**/*", route -> {
            if (route.request().url().contains("analytics")) {
                route.abort();
            } else {
                route.resume();
            }
        });
        page.navigate("https://the-internet.herokuapp.com");
        assertTrue(page.isVisible("text=Welcome to the-internet"));
    }

    @Test
    public void shouldHandleAuthenticationPopup() {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setHttpCredentials(new HttpCredentials("admin", "admin")));
        Page authPage = context.newPage();
        authPage.navigate("https://the-internet.herokuapp.com/basic_auth");
        assertTrue(authPage.isVisible("text=Congratulations! You must have the proper credentials."));
    }

    @Test
    public void shouldHandleGeolocation() {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setGeolocation(new Geolocation(37.7749, -122.4194))
                .setPermissions(Arrays.asList("geolocation")));
        Page geoPage = context.newPage();
        geoPage.navigate("https://the-internet.herokuapp.com/geolocation");

        // Ensure the button is visible before clicking
        geoPage.waitForSelector("button[onclick='getLocation()']", new Page.WaitForSelectorOptions().setTimeout(10000));

        // Retry mechanism for clicking the button
        for (int i = 0; i < 5; i++) {
            try {
                geoPage.click("button[onclick='getLocation()']", new Page.ClickOptions().setTimeout(10000));
                break;
            } catch (TimeoutError e) {
                if (i == 4) throw e; // Rethrow if last attempt fails
            }
        }

        // Wait for the geolocation text to appear
        geoPage.waitForSelector("text=Latitude: 37.7749, Longitude: -122.4194", new Page.WaitForSelectorOptions().setTimeout(10000));

        // Assert the geolocation is displayed correctly
        assertTrue(geoPage.isVisible("text=Latitude: 37.7749, Longitude: -122.4194"));
    }

    @Test
    public void shouldHandlePermissions() {
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setPermissions(Arrays.asList("notifications")));
        Page permPage = context.newPage();
        permPage.navigate("https://the-internet.herokuapp.com/notification_message_rendered");
        assertTrue(permPage.isVisible("text=Action successful"));
    }
}