package br.com.skygod.awesome.error;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrorDetails
        extends ErrorDetails {

    private Map<String, String> fieldsMap;

    private ValidationErrorDetails() {
    }

    public static final class Builder {
        private final Map<String, String> fieldsMap;
        private String title;
        private String detail;
        private String developerMessage;
        private int status;
        private long timestamp;

        private Builder() {
            this.fieldsMap = new HashMap<>();
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder putFields(String fieldName, String fieldMessage) {
            String[] fieldsNames = fieldName.split(", ");
            String[] fieldsMessages = fieldMessage.split(", ");

            for (int i = 0; i < fieldsNames.length; i++) {
                this.fieldsMap.put(fieldsNames[i], fieldsMessages[i]);
            }

            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder developerMessage(String developerMessage) {
            this.developerMessage = developerMessage;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ValidationErrorDetails build() {
            ValidationErrorDetails validationErrorDetails = new ValidationErrorDetails();
            validationErrorDetails.setTitle(title);
            validationErrorDetails.setDetail(detail);
            validationErrorDetails.setDeveloperMessage(developerMessage);
            validationErrorDetails.setStatus(status);
            validationErrorDetails.setTimestamp(timestamp);
            validationErrorDetails.fieldsMap = this.fieldsMap;
            return validationErrorDetails;
        }
    }

    public Map<String, String> getFieldsMap() {
        return fieldsMap;
    }
}
