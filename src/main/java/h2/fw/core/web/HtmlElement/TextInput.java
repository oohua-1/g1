package h2.fw.core.web.HtmlElement;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import h2.fw.core.web.HtmlElement.TypifiedElement;

public class TextInput extends TypifiedElement {

    public TextInput(Page page, Locator element) {
        super(page, element);
    }

    @Override
    public void fill(String value) {
        super.fill(value);
    }

    public void focus() {
        element.focus();
    }

    public void press(String key) {
        element.press(key);
    }

    public String getAttribute(String attribute) {
        return element.getAttribute(attribute);
    }
    public String getText() {
        return element.textContent();
    }
}

