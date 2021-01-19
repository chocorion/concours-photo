package dao.sql;

import dao.ThemeDao;
import model.Theme;
import model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SqlThemeDao extends SqlDao<Theme> implements ThemeDao {
    @Override
    protected Theme createObjectFromResult(ResultSet resultSet) throws SQLException {
        Integer winnerId = getInteger(resultSet, "winner");
        Integer authorId = getInteger(resultSet, "author");

        User winner = (winnerId == null)? null : new SqlUserDao().getById(winnerId);
        User author = (authorId == null)? null : new SqlUserDao().getById(authorId);

        Integer themeId = resultSet.getInt("id");
        String state = resultSet.getString("state");

        return new Theme(
            themeId,
            resultSet.getString("title"),
            resultSet.getString("photo_url"),
            state,
            resultSet.getString("date"),
            winner,
            author,
            state.equals("proposal") ? getNbVotes(themeId) : null
        );
    }

    @Override
    public List<Theme> getAll() throws SQLException {
        String statement = "SELECT * FROM theme";
        return queryAllObjects(statement);
    }

    @Override
    public Optional<Theme> getCurrent() throws SQLException {
        String statement = "SELECT * FROM theme WHERE state='active'";

        return queryFirstOptional(statement);
    }

    @Override
    public List<Theme> getProposals() throws SQLException {
        String statement = "SELECT * FROM theme WHERE state='proposal'";
        return queryAllObjects(statement);
    }

    @Override
    public Optional<Theme> getUserProposal(User user) throws SQLException {
        String statement = "SELECT * FROM theme WHERE state='proposal' AND author = ?";
        List<Object> opt = Arrays.asList(user.id);

        return queryFirstOptional(statement, opt);
    }

    @Override
    public Optional<Theme> getById(int id) throws SQLException {
        String statement = "SELECT * from theme WHERE id=?";
        List<Object> opt = Arrays.asList(id);

        return queryFirstOptional(statement, opt);
    }

    @Override
    public Optional<Theme> getUserThemeVote(User user) throws SQLException {
        String statement = "SELECT theme FROM user WHERE id=?";
        List<Object> opt = Arrays.asList(user.id);

        return queryFirstOptional(statement, opt);
    }

    @Override
    public void setUserVote(User user, Integer themeId) throws SQLException {
        String statement = "UPDATE user SET theme=? WHERE id=?";
        List<Object> opt = Arrays.asList(themeId, user.id);

        exec(statement, opt);
    }

    @Override
    public Theme insert(Theme theme) throws SQLException {
        String statement = "INSERT INTO theme (title, state, photo_url, winner, date) VALUES (?, ?, ?, ?, ?)";
        List<Object> opt = Arrays.asList(
                theme.title,
                theme.state,
                theme.photo,
                (theme.winner == null)? null : theme.winner.id,
                theme.date
        );

        int insertedId = doInsert(statement, opt);
        return getById(insertedId).orElseThrow(SQLException::new);
    }

    @Override
    public void delete(int id) throws SQLException {
        String statement = "DELETE FROM theme WHERE id=?";
        List<Object> opt = Arrays.asList(id);

        exec(statement, opt);
    }

    @Override
    public Integer getNbVotes(int id) throws SQLException {

        String statement = "SELECT COUNT(*) FROM user WHERE theme = ?";
        List<Object> opt = Arrays.asList(id);

        return queryFirstInt(statement, opt);
    }
}

