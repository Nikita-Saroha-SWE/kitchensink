package org.springboot.kitchensink.exception;

import java.util.Map;

public class FieldValidationException extends RuntimeException {

	private static final long serialVersionUID = -9196770583898164978L;
	private final Map<String, String> errors;

    public FieldValidationException(Map<String, String> errors) {
        super(errors.values().stream().findFirst().orElse("Validation failed for one or more fields"));
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
