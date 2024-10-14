package h2.fw.stepdefinitions.restful;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import java.util.Map;

public class IsMapNotContainingSubset extends TypeSafeMatcher<Map<?, ?>> {
    private final Map<?, ?> expectedSubset;

    public IsMapNotContainingSubset(Map<?, ?> expectedSubset) {
        this.expectedSubset = expectedSubset;
    }

    @Override
    protected boolean matchesSafely(Map<?, ?> actualMap) {
        // Check if actualMap does NOT contain any of the expectedSubset's entries
        for (Map.Entry<?, ?> entry : expectedSubset.entrySet()) {
            if (actualMap.entrySet().contains(entry)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a map not containing the subset: " + expectedSubset);
    }

    public static IsMapNotContainingSubset doesNotContainSubset(Map<?, ?> subset) {
        return new IsMapNotContainingSubset(subset);
    }
}
