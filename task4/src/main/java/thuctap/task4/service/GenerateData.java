package thuctap.task4.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GenerateData {

    private static final String INSERT_USER_SQL = "INSERT INTO user (name, address, age) VALUES (?, ?, ?)";
    private static final String THEM_COT="ALTER TABLE user DROP COLUMN money;";
    private static final int TOTAL_RECORDS = 5000000;
    private static final int THREAD_COUNT = 10;
    private static final String[] NAMES = {"Nguyen Van C", "Hoang Anh", "Tran Van B", "Pham Thi C", "Le Duy H", "Nguyen Van H", "Huynh Kim", "Ho Thi Lan", "Nguyen Thi B", "Phan Van D"};
    private static final String[] ADDRESSES = {"Hanoi", "Saigon", "Danang", "Hue", "Haiphong", "Nha Trang", "Vung Tau", "Can Tho", "Bien Hoa", "Buon Ma Thuot"};

    public static void main(String[] args) throws InterruptedException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/task4");
        config.setUsername("root");
        config.setPassword("1234");

        HikariDataSource dataSource = new HikariDataSource(config);

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(new DataInserter(dataSource, TOTAL_RECORDS / THREAD_COUNT));
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        dataSource.close();
    }

    static class DataInserter implements Runnable {
        private final HikariDataSource dataSource;
        private final int recordsToInsert;

        DataInserter(HikariDataSource dataSource, int recordsToInsert) {
            this.dataSource = dataSource;
            this.recordsToInsert = recordsToInsert;
        }

        @Override
        public void run() {
            Random random = new Random();

            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                try (PreparedStatement preparedStatement = connection.prepareStatement(THEM_COT)) {
                    for (int i = 0; i < recordsToInsert; i++) {
                        String name = NAMES[random.nextInt(NAMES.length)];
                        String address = ADDRESSES[random.nextInt(ADDRESSES.length)];
                        int age = 18 + random.nextInt(83);

                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, address);
                        preparedStatement.setInt(3, age);

                        preparedStatement.addBatch();

                        if (i % 10000 == 0) {
                            preparedStatement.executeBatch();
                            connection.commit();
                            System.out.println("Inserted " + (i + 1) + " records by " + Thread.currentThread().getName());
                        }
                    }
                    preparedStatement.executeBatch();
                    connection.commit();
                    System.out.println("Inserted total of " + recordsToInsert + " records by " + Thread.currentThread().getName());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
