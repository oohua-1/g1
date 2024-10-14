package h2.fw.Report;

import com.epam.reportportal.listeners.LogLevel;
import com.epam.reportportal.message.ReportPortalMessage;
import com.epam.reportportal.utils.files.ByteSource;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import com.epam.reportportal.service.ReportPortal;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import h2.fw.core.web.aspects.AssertExt;
import h2.fw.hooks.WebHooks;
import io.cucumber.datatable.DataTable;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ReportPortalAttachmentUtils {
    private static final ThreadLocal<List<AbstractMap.SimpleEntry<String, String>>> collectedScreenshots = ThreadLocal.withInitial(ArrayList::new);

    /**
     * Collect a screenshot using Playwright and store it in ThreadLocal.
     *
     * @param attachmentName The name of the screenshot to collect.
     */
    public static void collectScreenshot(String attachmentName) {
        try {
            WebHooks webHooks = WebHooks.getInstance();  // Access the WebHooks singleton
            Page page = webHooks.getPage();

            // Capture the screenshot using Playwright
            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(false).setQuality(50).setType(ScreenshotType.JPEG));
            String base64Screenshot = Base64.getEncoder().encodeToString(screenshotBytes);

            // Collect the screenshot with its name in ThreadLocal
            collectedScreenshots.get().add(new AbstractMap.SimpleEntry<>(attachmentName, base64Screenshot));
            AssertExt.setScreenshotTaken(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Attach the collected screenshots in a grid to ReportPortal as an HTML attachment.
     *
     * @param attachmentName The name of the attachment.
     */
    public static void attachScreenshotsInGrid(String attachmentName) {
        try {
            List<AbstractMap.SimpleEntry<String, String>> screenshots = collectedScreenshots.get();

            if (!screenshots.isEmpty()) {
                StringBuilder htmlContentBuilder = new StringBuilder();
                htmlContentBuilder.append("<html><body style='background-color:#f4f4f4; padding:10px;'>");
                htmlContentBuilder.append("<div style='display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 10px;'>");

                for (AbstractMap.SimpleEntry<String, String> screenshotEntry : screenshots) {
                    String imageName = screenshotEntry.getKey();
                    String base64Screenshot = screenshotEntry.getValue();

                    htmlContentBuilder.append("<div style='border: 1px solid #ddd; padding: 10px; background-color: #fff; box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);'>");
                    htmlContentBuilder.append("<p style='text-align: center; font-weight: bold;'>" + imageName + "</p>");
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

                // Convert HTML content to bytes
                byte[] htmlBytes = htmlContentBuilder.toString().getBytes(StandardCharsets.UTF_8);

                // Attach the HTML content as an attachment to ReportPortal
                ReportPortal.emitLog(new ReportPortalMessage(ByteSource.wrap(htmlBytes), "text/html", attachmentName),
                        "INFO",
                        Calendar.getInstance().getTime());

                // Clear the list after attaching
                screenshots.clear();
                AssertExt.setScreenshotTaken(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Send an HTML attachment to ReportPortal.
     *
     * @param htmlContent The HTML content to attach.
     * @param attachmentName The name of the HTML file (can be null or empty).
     */
    public static void attachHtmlLog(String htmlContent, String attachmentName) {
        // Convert HTML content to bytes
        byte[] data = htmlContent.getBytes(StandardCharsets.UTF_8);

        // Define MIME type for HTML
        String mimeType = "text/html";

        // Ensure the attachment name has a valid default if null or empty
        String finalAttachmentName = (attachmentName == null || attachmentName.isEmpty()) ? "html_report" : attachmentName;

        // Emit the log with the HTML attachment to ReportPortal
        ReportPortal.emitLog(new ReportPortalMessage(ByteSource.wrap(data), mimeType, finalAttachmentName),
                "INFO",  // Log level
                Calendar.getInstance().getTime()  // Timestamp for the log
        );
    }


    public static void attachPlaywrightScreenshot(String attachmentName) {
        // Access the WebHooks singleton to get the Playwright Page instance
        WebHooks webHooks = WebHooks.getInstance();
        Page page = webHooks.getPage();  // Assuming WebHooks manages the Playwright Page instance

        if (page == null) {
            throw new IllegalStateException("Playwright Page instance is not available.");
        }
        if (page != null && !AssertExt.isScreenshotTaken()) {
            byte[] screenshotBytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(false));

            // Define MIME type for PNG
            String mimeType = "image/png";

            // Set a default name if no name is provided
            String finalAttachmentName = (attachmentName == null || attachmentName.isEmpty()) ? "screenshot" : attachmentName;

            // Emit the screenshot log with ReportPortal
            ReportPortal.emitLog(new ReportPortalMessage(ByteSource.wrap(screenshotBytes), mimeType, finalAttachmentName),
                    LogLevel.INFO.name(),
                    Calendar.getInstance().getTime());
            AssertExt.setScreenshotTaken(true);

        }
    }


    /**
     * Convert a Cucumber DataTable to HTML table format and log it to ReportPortal.
     *
     * @param dataTable The Cucumber DataTable object.
     */
    public static void logDataTableAsHtml(DataTable dataTable) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");

        // Extract header
        List<String> headers = dataTable.row(0);
        htmlContent.append("<tr>");
        for (String header : headers) {
            htmlContent.append("<th>").append(header).append("</th>");
        }
        htmlContent.append("</tr>");

        // Extract rows (excluding the header)
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            htmlContent.append("<tr>");
            // Check if all values in the row are dashes ("----"). If so, skip this row.
            boolean isSeparatorRow = row.values().stream().allMatch(value -> value.matches("-+"));
            if (isSeparatorRow) {
                continue;  // Skip separator rows
            }
            for (String header : headers) {
                htmlContent.append("<td>").append(row.get(header)).append("</td>");
            }
            htmlContent.append("</tr>");
        }

        htmlContent.append("</table>");

        // Log the generated HTML table to ReportPortal
        logTableAsHtml(htmlContent.toString());
    }

    public static void logTableAsHtml(String tableContent) {
        // Emit the log message with the HTML table content directly
        ReportPortal.emitLog(tableContent, "INFO", Calendar.getInstance().getTime());
    }
    public static void attachHtmlTableAsFile(String tableContent, String attachmentName) {
        // Convert HTML table content to bytes
        byte[] data = tableContent.getBytes(StandardCharsets.UTF_8);

        // Define MIME type for HTML
        String mimeType = "text/html";

        // Ensure the attachment name has a valid default if null or empty
        String finalAttachmentName = (attachmentName == null || attachmentName.isEmpty()) ? "html_table.html" : attachmentName;

        // Emit the log with the HTML table as an attachment to ReportPortal
        ReportPortal.emitLog(new ReportPortalMessage(ByteSource.wrap(data), mimeType, finalAttachmentName),
                "INFO",  // Log level
                Calendar.getInstance().getTime()  // Timestamp for the log
        );
    }
}