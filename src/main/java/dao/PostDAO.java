package dao;

import java.util.List;
import model.Post;

public interface PostDAO {
    List<Post> getAllPostsOrderedByDate();
	void addPost(Post post);
}