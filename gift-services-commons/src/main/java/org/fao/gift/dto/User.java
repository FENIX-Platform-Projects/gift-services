package org.fao.gift.dto;

public class User {

    private long forumId;
    private String name;
    private String username;
    private UserRole role;
    private String institution;
    private String email;
    private String jwt;

    public User() {
    }

    public User(long forumId, String name, String username, UserRole role, String institution, String email, String jwt) {
        this.forumId = forumId;
        this.name = name;
        this.username = username;
        this.role = role;
        this.institution = institution;
        this.email = email;
        this.jwt = jwt;
    }

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (forumId != user.forumId) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (role != user.role) return false;
        if (institution != null ? !institution.equals(user.institution) : user.institution != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return jwt != null ? jwt.equals(user.jwt) : user.jwt == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (forumId ^ (forumId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (institution != null ? institution.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (jwt != null ? jwt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "forumId=" + forumId +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", institution='" + institution + '\'' +
                ", email='" + email + '\'' +
                ", jwt='" + jwt + '\'' +
                '}';
    }
}
