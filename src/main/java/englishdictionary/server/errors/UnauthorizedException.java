package englishdictionary.server.errors;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Account authorization failed");
    }
}
