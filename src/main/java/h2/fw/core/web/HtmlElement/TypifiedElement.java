package h2.fw.core.web.HtmlElement;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class TypifiedElement {
    protected Locator element;
    protected Page page;

    public TypifiedElement(Page page, Locator element) {
        this.page = page;
        this.element = element;
    }

    public void fill(String value) {
        element.fill(value);
    }

    public void focus() {
        element.focus();
    }
    public void doubleClick() {
        element.dblclick();
    }
    public void press(String key) {
        element.press(key);
    }
    public void clear(){
        element.clear();
    }
    public void clearByKeyBoard(){
        element.press("Meta+A");
        element.press("Backspace");
    }


    public void fillByJS(String value) {
        element.evaluate("(el, value) => el.value = value", value);
    }

    public String getValueByJS() {
        Object result = element.evaluate("(element) => element.value");
        return result != null ? result.toString() : null;
    }
    public boolean isVisible() {
        return element.isVisible();
    }
    public void click() {
        element.click();
    }

    public void clickByJS() {
        page.evaluate("(element) => element.click()", element);
    }

    public String textContent() {
        return element.textContent();
    }
}
