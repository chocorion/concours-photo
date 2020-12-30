import dao.*;
import dao.sql.*;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import services.*;
import services.implementation.imgur.ImgurImageService;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SqlUserDao.class).to(UserDao.class);
        bind(SqlPostDao.class).to(PostDao.class);
        bind(SqlUserSettingDao.class).to(UserSettingDao.class);
        bind(SqlSettingDao.class).to(SettingDao.class);
        bind(ImgurImageService.class).to(AbstractImageService.class);
        bind(SqlCommentDao.class).to(CommentDao.class);
        bind(SqlReactionDao.class).to(ReactionDao.class);

        bindAsContract(AuthenticationService.class);
        bindAsContract(PostService.class);
        bindAsContract(ReactionService.class);
    }
}