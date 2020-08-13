package br.com.skygod.awesome.error;

public class PropertyReferenceDetails extends ErrorDetails {

    private PropertyReferenceDetails(){
    }

    public static final class Builder {
        private String title;
        private String detail;
        private String developerMessage;
        private int status;
        private long timestamp;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
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

        public PropertyReferenceDetails build() {
            PropertyReferenceDetails propertyReferenceDetails = new PropertyReferenceDetails();
            propertyReferenceDetails.setTitle(title);
            propertyReferenceDetails.setDetail(detail);
            propertyReferenceDetails.setDeveloperMessage(developerMessage);
            propertyReferenceDetails.setStatus(status);
            propertyReferenceDetails.setTimestamp(timestamp);
            return propertyReferenceDetails;
        }
    }
}
