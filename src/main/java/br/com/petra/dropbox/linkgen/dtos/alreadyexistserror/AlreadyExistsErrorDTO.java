package br.com.petra.dropbox.linkgen.dtos.alreadyexistserror;

public class AlreadyExistsErrorDTO {
    public String error_summary;
    public Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getError_summary() {
        return error_summary;
    }

    public void setError_summary(String error_summary) {
        this.error_summary = error_summary;
    }
}
