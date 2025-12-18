package bean;

import java.time.LocalDateTime;

public class PostBean {

    private String authorName;
    private String role;
    private String title;
    private String content;
    private LocalDateTime postDate;

    public PostBean() {
        // costruttore vuoto per JavaFX / binding
    }

    public PostBean(String authorName,
                    String role,
                    String title,
                    String content,
                    LocalDateTime postDate) {
        this.authorName = authorName;
        this.role = role;
        this.title = title;
        this.content = content;
        this.postDate = postDate;
    }

    // ===== GETTERS =====

    public String getAuthorName() {
        return authorName;
    }

    public String getRole() {
        return role;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPostDate() {
        return postDate;
    }

    // ===== SETTERS =====

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPostDate(LocalDateTime postDate) {
        this.postDate = postDate;
    }
}