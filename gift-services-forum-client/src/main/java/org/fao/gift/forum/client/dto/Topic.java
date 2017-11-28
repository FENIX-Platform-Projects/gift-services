package org.fao.gift.forum.client.dto;

public class Topic {

    private Long cid;
    private String title;
    private String content;

    public Topic() {
    }

    public Topic(Long cid, String title, String content) {
        this.cid = cid;
        this.title = title;
        this.content = content;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Topic topic = (Topic) o;

        if (cid != null ? !cid.equals(topic.cid) : topic.cid != null) return false;
        if (title != null ? !title.equals(topic.title) : topic.title != null) return false;
        return content != null ? content.equals(topic.content) : topic.content == null;
    }

    @Override
    public int hashCode() {
        int result = cid != null ? cid.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "cid='" + cid + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
