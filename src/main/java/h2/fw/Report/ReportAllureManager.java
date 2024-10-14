package h2.fw.Report;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import h2.fw.core.web.aspects.AssertExt;
import h2.fw.hooks.WebHooks;
import h2.fw.utils.ConfigKey;
import h2.fw.utils.ConfigReader;
import io.github.techgnious.IVCompressor;
import io.github.techgnious.dto.ImageFormats;
import io.github.techgnious.dto.ResizeResolution;
import io.qameta.allure.Allure;
import java.util.AbstractMap.SimpleEntry;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ReportAllureManager {
    private static IVCompressor compressor = new IVCompressor();
    static ConfigReader configReader = ConfigReader.getInstance(null);

    private ReportAllureManager() {
        // Private constructor to prevent instantiation
    }

    public static void attachScreenshot(String attachmentName) {
        WebHooks webHooks = WebHooks.getInstance();  // Access the WebHooks singleton
        Page page = webHooks.getPage();

        if (page != null && !AssertExt.isScreenshotTaken()) { // Check the flag before taking the screenshot
            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(Boolean.parseBoolean(configReader.getProperty(ConfigKey.FULLPAGE_SCREENSHOT))).setQuality(50).setType(ScreenshotType.JPEG));
            String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);


            String htmlContent = "<html><body style='display:flex; justify-content:center; align-items:center; background-color:#f4f4f4; padding:10px;'>" +
                    "<div style='border: 1px solid #ddd; padding: 10px; background-color: #fff; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);'>" +
                    "<img src='data:image/jpeg;base64," + base64Screenshot + "' alt='Screenshot' style='width:200px; height:auto; cursor:pointer;' onclick='viewFullImage()'/>" +
                    "</div>" +
                    "<script>" +
                    "function viewFullImage() {" +
                    "   var imageWindow = window.open('');" +
                    "   imageWindow.document.write('<img src=\"data:image/jpeg;base64," + base64Screenshot + "\" style=\"width:100%; height:auto;\"/>');" +
                    "}" +
                    "</script>" +
                    "</body></html>";

            // Attach the HTML content to Allure
            Allure.addAttachment(attachmentName, "text/html", htmlContent, ".html");
            AssertExt.setScreenshotTaken(true);

        }
    }
    private static ThreadLocal<List<AbstractMap.SimpleEntry<String, String>>> collectedScreenshots = ThreadLocal.withInitial(ArrayList::new);

    public static void collectScreenshot(String attachmentName) {
        try {
            WebHooks webHooks = WebHooks.getInstance();  // Access the WebHooks singleton
            Page page = webHooks.getPage();

            // Capture the screenshot using Playwright
            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(false).setQuality(50).setType(ScreenshotType.JPEG));
            String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);


//            // Encode the compressed screenshot to Base64
//            String base64Screenshot = Base64.getEncoder().encodeToString(compressedBytes);
            collectedScreenshots.get().add(new AbstractMap.SimpleEntry<>(attachmentName, base64Screenshot));  // Collect the screenshot with its name
            AssertExt.setScreenshotTaken(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void attachScreenshotsInGrid(String attachmentName) {
        try {
            List<SimpleEntry<String, String>> screenshots = collectedScreenshots.get();

            if (!screenshots.isEmpty()) {
                StringBuilder htmlContentBuilder = new StringBuilder();
                htmlContentBuilder.append("<html><body style='background-color:#f4f4f4; padding:10px;'>");
                htmlContentBuilder.append("<div style='display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 10px;'>");

                for (SimpleEntry<String, String> screenshotEntry : screenshots) {
                    String imageName = screenshotEntry.getKey();
                    String base64Screenshot = screenshotEntry.getValue();

                    htmlContentBuilder.append("<div style='border: 1px solid #ddd; padding: 10px; background-color: #fff; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);'>");
                    htmlContentBuilder.append("<p style='text-align: center; font-weight: bold;'>" + imageName + "</p>");  // Add the image name as a caption
                    htmlContentBuilder.append("<img src='data:image/jpeg;base64," + base64Screenshot + "' alt='" + imageName + "' style='width:100%; height:auto; cursor:pointer;' onclick='viewFullImage(\"" + base64Screenshot + "\")'/>");
                    htmlContentBuilder.append("</div>");
                }

                htmlContentBuilder.append("</div>");
                htmlContentBuilder.append("<script>");
                htmlContentBuilder.append("function viewFullImage(base64) {");
                htmlContentBuilder.append("   var imageWindow = window.open('');");
                htmlContentBuilder.append("   imageWindow.document.write('<img src=\"data:image/jpeg;base64,' + base64 + '\" style=\"width:100%; height:auto;\"/>');");
                htmlContentBuilder.append("}");
                htmlContentBuilder.append("</script>");
                htmlContentBuilder.append("</body></html>");

                // Attach the HTML content to Allure
                Allure.addAttachment(attachmentName, "text/html", htmlContentBuilder.toString(), ".html");
                AssertExt.setScreenshotTaken(true);

                // Clear the list after attaching
                screenshots.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void attachHtml(String attachmentName, String htmlContent) {
        Allure.addAttachment(attachmentName, "text/html", new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)), ".html");
    }

    public static void attachText(String attachmentName, String textContent) {
        Allure.addAttachment(attachmentName, "text/plain", new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8)), ".txt");
    }

    // Add more static attachment methods as needed
}
