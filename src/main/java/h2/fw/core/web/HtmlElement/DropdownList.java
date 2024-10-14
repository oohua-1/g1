package h2.fw.core.web.HtmlElement;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import h2.fw.core.web.HtmlElement.TypifiedElement;

import java.util.List;

public class DropdownList extends TypifiedElement {

    private String optionSelector = "option"; // Default selector for options

    public DropdownList(Page page, Locator element) {
        super(page, element);
    }

    // Set a custom selector for options
    public void setOptionSelector(String optionSelector) {
        this.optionSelector = optionSelector;
    }

    // Select an option by visible text
    public void selectByVisibleText(String visibleText) {
        element.selectOption(new String[]{visibleText});
    }

    // Select an option by value
    public void selectByValue(String value) {
        element.selectOption(value);
    }

    // Select an option by index
    public void selectByIndex(int index) {
        element.selectOption(new String[]{String.valueOf(index)});
    }

    // Get selected option text
    public String getSelectedOptionText() {
        return element.textContent();
    }

    // Get selected option value
    public String getSelectedOptionValue() {
        return element.inputValue();
    }

    // Get all options text
    public List<String> getAllOptionsText() {
        return element.locator(optionSelector).allTextContents();
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllOptionsValue() {
        return (List<String>) element.locator(optionSelector).evaluateAll("(options) => options.map(option => option.value)");
    }


    // Check if the dropdown is enabled
    public boolean isEnabled() {
        return element.isEnabled();
    }

    // Check if the dropdown is disabled
    public boolean isDisabled() {
        return !element.isEnabled();
    }

    // Get the count of options in the dropdown
    public int getOptionsCount() {
        return element.locator(optionSelector).count();
    }
}
