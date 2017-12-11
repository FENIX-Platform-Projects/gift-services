package org.fao.gift.forum.client;

public interface ForumClient {

    // CREATE

    /**
     * Create a new forum category
     *
     * @param name the name of the category
     * @return the ID of the created category
     */
    long createCategory(String name);

    /**
     * Create a new forum post
     *
     * @param topicId the ID of the topic to reply to
     * @param content the content of the post
     * @return the ID of the created post
     */
    long createPost(long topicId, String content);

    /**
     * Create a new forum topic
     *
     * @param categoryId the category of the topic
     * @param title      the title of te post
     * @param content    the content of the topic
     * @return the ID of the created topic
     */
    long createTopic(long categoryId, String title, String content);

    /**
     * Create a new forum user
     *
     * @param username the user's username
     * @param email    the user's email
     * @return the created user's ID
     */
    long createUser(String username, String email);

    // PRIVILEGES

    /**
     * Enable the user to contribute to a specific forum category
     *
     * @param categoryId the ID of the category
     * @param userId     the user's ID
     */
    void setOwner(long categoryId, long userId);
}
