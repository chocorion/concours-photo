package dao.sql;

import dao.UserDao;
import model.SettingName;
import model.User;
import model.UserSetting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SqlUserDao extends SqlDao<User> implements UserDao {

    @Override
    protected User createObjectFromResult(ResultSet resultSet) throws SQLException {
        Integer userId = getInteger(resultSet, "id");
        HashMap<SettingName, UserSetting> userSettings = new SqlUserSettingDao().getAllForUser(userId);

        return new User(resultSet.getString("username"), userSettings, userId);
    }

    @Override
    public User getById(int id) throws SQLException {
        String statement = "SELECT * FROM user WHERE id=?";
        List<Object> opt = Arrays.asList(id);

        return queryFirstObject(statement, opt);
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        String statement = "SELECT * FROM user WHERE username=?";
        List<Object> opt = Arrays.asList(username);

        return queryFirstObject(statement, opt);
    }

    @Override
    public User insert(User user, String hash) throws SQLException {
        if(user.id != null) throw new SQLException(String.valueOf(UserDaoException.ID_PROVIDED));

        String statement = "INSERT INTO user (username, sha) VALUES (?,?)";
        List<Object> opt = Arrays.asList(user.username, hash);

        int userId = doInsert(statement, opt);
        new SqlUserSettingDao().insertDefaultsForUser(userId);

        return new User(user.username, new SqlUserSettingDao().getAllForUser(userId), userId);
    }

    @Override
    public void update(User user) throws SQLException {
        if(user.id == null) throw new SQLException(String.valueOf(UserDaoException.ID_NOT_PROVIDED));

        String statement = "UPDATE user SET username=? WHERE id=?";
        List<Object> opt = Arrays.asList(user.id, user.username);

        for(UserSetting userSetting : user.settings.values()) {
            new SqlUserSettingDao().update(userSetting);
        }

        exec(statement, opt);
    }

    @Override
    public void delete(User user) throws SQLException {
        if(user.id == null) throw new SQLException(String.valueOf(UserDaoException.ID_NOT_PROVIDED));

        String statement = "DELETE FROM user WHERE id=?";
        List<Object> opt = Arrays.asList(user.id);

        exec(statement, opt);
    }

    @Override
    public User getByLogin(String username, String hash) throws SQLException {
        String statement = "SELECT * FROM user WHERE username=? AND sha=?";
        List<Object> opt = Arrays.asList(username, hash);

        return queryFirstObject(statement, opt);
    }

    @Override
    public void updateHash(User user, String hash) throws SQLException {
        if(user.id == null) throw new SQLException(String.valueOf(UserDaoException.ID_NOT_PROVIDED));

        String statement = "UPDATE user SET sha ? WHERE user=?";
        List<Object> opt = Arrays.asList(user.id, hash);

        exec(statement, opt);
    }
}

enum UserDaoException {
    ID_PROVIDED("The user ID was provided when it was not required"),
    ID_NOT_PROVIDED("The user ID was not provided when it was required");

    String value;

    UserDaoException(String error) {
        this.value = error;
    }
}