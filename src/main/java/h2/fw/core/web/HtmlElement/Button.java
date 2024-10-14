package h2.fw.core.web.HtmlElement;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Button extends TypifiedElement {

    public Button(Page page, Locator element) {
        super(page, element);
    }

    @Override
    public void click() {
        super.click();
    }
}