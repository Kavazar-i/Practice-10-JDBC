public class DataBaseSettings {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/start";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234567890";

    private final String dbUrl;
    private final String user;
    private final String password;

    public DataBaseSettings() {
        this.dbUrl = DB_URL;
        this.user = USER;
        this.password = PASSWORD;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}