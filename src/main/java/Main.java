import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Main {
    private static final DataBaseSettings SETTINGS = new DataBaseSettings();

    private static final String COORDINATES_TABLE = "Coordinates";
    private static final String FREQUENCIES_TABLE = "Frequencies";

    private static final String CREATE_TABLE = "CREATE TABLE %s (len INT, num INT);";
    private static final String SELECT_ALL_FROM_TABLE = "SELECT * FROM %s";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";

    private static final String INSERT_INTO_TABLE = "INSERT INTO %s (len, num) VALUES (?, ?)";
    private static final String SELECT_FROM_WHERE_LEN_BIGGER_THAN_NUM = "SELECT * FROM %s WHERE len>num";

    private static final String DELIMITER = ":";
    private static final int LEN_IDX = 1;
    private static final int NUM_IDX = 2;

    public static void main(String[] args) {

        try (
                final Connection connection = DriverManager.getConnection(SETTINGS.getDbUrl(), SETTINGS.getUser(), SETTINGS.getPassword());
                final Statement statement = connection.createStatement();
        ) {
            // Retrieve data from the Coordinates table
            final ArrayList<Pair> frequencies = new ArrayList<>();
            try (final ResultSet resultSet = statement.executeQuery(String.format(SELECT_ALL_FROM_TABLE, COORDINATES_TABLE))) {
                while (resultSet.next()) {
                    final double x1 = resultSet.getDouble(LEN_IDX);
                    final double x2 = resultSet.getDouble(NUM_IDX);

                    final int len = (int) (Math.abs(Math.round(x1) - Math.round(x2)));

                    boolean flag = false;
                    for (Pair frequency : frequencies) {
                        if (frequency.len == len) {
                            frequency.num ++;
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        frequencies.add(new Pair(len, 1));
                    }
                }

                // Sort frequencies
                frequencies.sort(Comparator.comparingInt(o -> o.len));

                // Drop and create Frequencies table
                statement.execute(String.format(DROP_TABLE, FREQUENCIES_TABLE));
                statement.execute(String.format(CREATE_TABLE, FREQUENCIES_TABLE));

                // Insert frequencies into the Frequencies table using batch updates
                final PreparedStatement preparedStatement = connection.prepareStatement(String.format(INSERT_INTO_TABLE, FREQUENCIES_TABLE));

                for (final Pair freq : frequencies) {
                    System.out.println(freq.len + ":" + freq.num);

                    preparedStatement.setInt(LEN_IDX, freq.len);
                    preparedStatement.setInt(NUM_IDX, freq.num);
                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();

                try (final ResultSet resultSet2 = statement.executeQuery(String.format(SELECT_FROM_WHERE_LEN_BIGGER_THAN_NUM, FREQUENCIES_TABLE))) {
                    System.out.println();
                    while (resultSet2.next()) {
                        System.out.println(resultSet2.getInt(LEN_IDX) + DELIMITER + resultSet2.getInt(NUM_IDX));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
