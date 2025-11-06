package model;

import java.time.LocalDateTime;

public class Post {
    private String userEmail;
    private String authorName;
    private String role;
    private String title;
    private String content;
    private LocalDateTime postDate;

    public Post(String userEmail, String authorName, String role, String title, String content, LocalDateTime postDate) {
        this.userEmail = userEmail;
        this.authorName = authorName;
        this.role = role;
        this.title = title;
        this.content = content;
        this.postDate = postDate;
    }

    public String getUserEmail() { return userEmail; }
    public String getAuthorName() { return authorName; }
    public String getRole() { return role; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getPostDate() { return postDate; }
}