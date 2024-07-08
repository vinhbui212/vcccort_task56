package thuctap.task4.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import thuctap.task4.exception.UserException;
import thuctap.task4.model.User;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAOImpl implements DAO {

    private static final String INSERT_USER_SQL = "INSERT INTO user (name, address, age) VALUES (?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE user SET name = ?, address = ?, age = ? WHERE id = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM user WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT id, name, address, age FROM user WHERE id = ?";
    private static final String SELECT_USERS_BY_NAME_WITH_PAGINATION =
            "SELECT id, name, address, age, money FROM user WHERE name LIKE ? LIMIT ? OFFSET ?";
    private static final String SELECT_USERS_BY_ADDRESS = "SELECT id, name, address, age FROM user WHERE address LIKE ?";
    private static final String SELECT_ALL_USERS_ORDER_BY_NAME = "SELECT id, name, address, age FROM user ORDER BY name";

    private final HikariDataSource dataSource;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Autowired
    public UserDAOImpl(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private <T> T executeWithRetry(DatabaseOperation<T> operation) throws Exception {
        int attempt = 0;
        while (true) {
            try {
                return operation.execute();
            } catch (SQLException e) {
                if (++attempt >= MAX_RETRIES) {
                    throw new Exception("Max retries ", e);
                }
                System.out.println("Operation failed, retrying in " + RETRY_DELAY_MS + "ms...");
                Thread.sleep(RETRY_DELAY_MS * attempt);
            }
        }
    }

    @Override
    public void insertUser(User user) throws Exception {
        executeWithRetry(() -> {
            if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getAddress()) ||
                    user.getAge() < 1 || user.getAge() > 100) {
                throw new UserException("Check form again", true,902 );
            }
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {

                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getAddress());
                preparedStatement.setInt(3, user.getAge());
                preparedStatement.executeUpdate();
            }
            catch (Exception e){
              throw  new UserException("Add fail", true, 404);
            }
            return null;
        });
    }

    @Override
    public void updateUser(int id, User user) throws Exception {
        executeWithRetry(() -> {
            if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getAddress()) ||
                    user.getAge() < 1 || user.getAge() > 100) {
                throw new UserException("Check form again", true,902 );
            }
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
                preparedStatement.setString(1, user.getName());
                preparedStatement.setString(2, user.getAddress());
                preparedStatement.setInt(3, user.getAge());
                preparedStatement.setInt(4, id);
                preparedStatement.executeUpdate();
            }
            catch (Exception e){
                throw  new UserException("Update fail", true, 404);
            }
            return null;
        });
    }

    @Override
    public void deleteUser(int id) throws Exception {
        executeWithRetry(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_SQL)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
            catch (Exception e){
                throw  new UserException("Delete fail", true, 404);
            }
            return null;
        });
    }



    @Override
    public List<User> searchByName(String name, int limit, int offset) throws Exception {
        return executeWithRetry(() -> {
            List<User> users = new ArrayList<>();
            String query = "SELECT id, name, address, age FROM user WHERE name LIKE ? LIMIT ? OFFSET ?";
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + name + "%");
                preparedStatement.setInt(2, limit);
                preparedStatement.setInt(3, offset);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("name");
                    String address = rs.getString("address");
                    int age = rs.getInt("age");

                    users.add(new User(id, username, address, age));
                }
            } catch (SQLException e) {
                throw new UserException("Failed to search users by name with pagination", true, 500);
            }
            return users;
        });
    }




    @Override
    public User searchById(int id) throws Exception {
        return executeWithRetry(() -> {
            User user = null;
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
                preparedStatement.setInt(1, id);
                ResultSet rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    int age = rs.getInt("age");
                    user = new User(id, name, address, age);
                }
            }
            catch (Exception e){
                throw  new UserException("Can't find this id", true, 404);
            }
            return user;
        });
    }

    @Override
    public List<User> searchByAddress(String address) throws Exception {
        return executeWithRetry(() -> {
            List<User> users = new ArrayList<>();
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USERS_BY_ADDRESS)) {
                preparedStatement.setString(1, "%" + address + "%");
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String userAddress = rs.getString("address");
                    int age = rs.getInt("age");
                    users.add(new User(id, name, userAddress, age));
                }
            }catch (Exception e){
                throw  new UserException("find fail", true, 404);
            }
            return users;
        });
    }

    @Override
    public List<User> arrangeByName() throws Exception {
        return executeWithRetry(() -> {
            List<User> users = new ArrayList<>();
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS_ORDER_BY_NAME)) {
                ResultSet rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String address = rs.getString("address");
                    int age = rs.getInt("age");
                    users.add(new User(id, name, address, age));
                }
            }catch (Exception e){
                throw  new UserException("Get fail", true, 404);
            }
            return users;
        });
    }

    @FunctionalInterface
    private interface DatabaseOperation<T> {
        T execute() throws Exception;
    }
//    public void genRandom(){
//        List<User> users=new ArrayList<User>();
//        users.add("Nguyen Van A","da",35);
//
//    }
}
