package org.fao.gift.common.dto;

import org.fao.fenix.commons.find.dto.filter.StandardFilter;

public class UserWithFilter {

    private StandardFilter filter;
    private User user;


    public UserWithFilter() {
    }

    public UserWithFilter(StandardFilter filter, User user) {
        this.filter = filter;
        this.user = user;
    }

    public StandardFilter getFilter() {
        return filter;
    }

    public void setFilter(StandardFilter filter) {
        this.filter = filter;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserWithFilter that = (UserWithFilter) o;

        if (filter != null ? !filter.equals(that.filter) : that.filter != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;
    }

    @Override
    public int hashCode() {
        int result = filter != null ? filter.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserWithFilter{" +
                "filter=" + filter +
                ", user=" + user +
                '}';
    }
}
