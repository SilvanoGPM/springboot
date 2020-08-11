package br.com.skygod.awesome.error;

public class ResourceNotFoundDetails {

    private String title;
    private String detail;
    private String developerMessage;
    private int status;
    private long timestamp;

    private ResourceNotFoundDetails() {
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
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

        public ResourceNotFoundDetails build() {
            ResourceNotFoundDetails resourceNotFoundDetails = new ResourceNotFoundDetails();
            resourceNotFoundDetails.detail = this.detail;
            resourceNotFoundDetails.developerMessage = this.developerMessage;
            resourceNotFoundDetails.timestamp = this.timestamp;
            resourceNotFoundDetails.status = this.status;
            resourceNotFoundDetails.title = this.title;
            return resourceNotFoundDetails;
        }
    }
}
