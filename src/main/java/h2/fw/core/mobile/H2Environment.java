package h2.fw.core.mobile;

import static h2.fw.utils.OverriddenVariable.getOverriddenStringValue;

public class H2Environment {
    public String get(String environmentVariableName) {
        return getOverriddenStringValue(environmentVariableName);
    }
}
