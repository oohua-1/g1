package h2.fw.stepdefinitions.restful;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import java.util.Map;

public class IsMapContainingSubset extends TypeSafeMatcher<Map<?, ?>> {
    private final Map<?, ?> expectedSubset;

    public IsMapContainingSubset(Map<?, ?> expectedSubset) {
        this.expectedSubset = expectedSubset;
    }

    @Override
    protected boolean matchesSafely(Map<?, ?> actualMap) {
        return actualMap.entrySet().containsAll(expectedSubset.entrySet());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a map containing the subset: " + expectedSubset);
    }

    public static IsMapContainingSubset containsSubset(Map<?, ?> subset) {
        return new IsMapContainingSubset(subset);
    }
}
