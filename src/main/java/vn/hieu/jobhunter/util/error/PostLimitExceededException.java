package vn.hieu.jobhunter.util.error;

public class PostLimitExceededException extends Exception {
    public PostLimitExceededException(String message) {
        super(message);
    }
}
