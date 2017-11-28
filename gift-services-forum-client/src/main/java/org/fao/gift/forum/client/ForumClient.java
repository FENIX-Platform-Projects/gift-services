package org.fao.gift.forum.client;

public interface ForumClient {

    // CREATE

    long createCategory(String name);

    long createPost(long topicId, String content);

    long createTopic(long categoryId, String title, String content);

    long createUser(String username, String email);

    // PRIVILEGES

    void setOwner(long categoryId, long userId);
}
