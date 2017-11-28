package org.fao.gift.forum.client.dto;

//{
//    "privileges" :[
//    "find",
//    "posts:edit",
//    "read",
//    "topics:read",
//    "topics:reply",
//    "upload:post:file",
//    "upload:post:image"],
//    "groups" : [ "8" ]
//}

import java.util.Arrays;

public class CategoryPrivileges {

    private String[] privileges;
    private String[] groups;

    public CategoryPrivileges() {
    }

    public CategoryPrivileges(String[] groups) {
        this.groups = groups;
        this.privileges = new String[]{
                "find",
                "posts:edit",
                "read",
                "topics:read",
                "topics:reply",
                "upload:post:file",
                "upload:post:image"
        };
    }

    public CategoryPrivileges(String[] privileges, String[] groups) {
        this.privileges = privileges;
        this.groups = groups;
    }

    public String[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String[] privileges) {
        this.privileges = privileges;
    }

    public String[] getGroups() {
        return groups;
    }

    public void setGroups(String[] groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryPrivileges that = (CategoryPrivileges) o;

        if (!Arrays.equals(privileges, that.privileges)) return false;
        return Arrays.equals(groups, that.groups);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(privileges);
        result = 31 * result + Arrays.hashCode(groups);
        return result;
    }

    @Override
    public String toString() {
        return "CategoryPrivileges{" +
                "privileges=" + Arrays.toString(privileges) +
                ", groups=" + Arrays.toString(groups) +
                '}';
    }
}