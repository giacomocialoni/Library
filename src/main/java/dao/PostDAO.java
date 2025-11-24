package dao;

import java.util.List;
import model.Post;
import exception.DAOException;

public interface PostDAO {
    List<Post> getAllPostsOrderedByDate() throws DAOException;
    void addPost(Post post) throws DAOException;
}