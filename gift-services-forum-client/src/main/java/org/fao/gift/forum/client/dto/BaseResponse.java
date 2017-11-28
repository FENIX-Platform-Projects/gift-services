package org.fao.gift.forum.client.dto;

public class BaseResponse {

    private String responseCode;
    private Long id;

    public BaseResponse() {
    }

    public BaseResponse(String responseCode, Long id) {
        this.responseCode = responseCode;
        this.id = id;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseResponse that = (BaseResponse) o;

        if (responseCode != null ? !responseCode.equals(that.responseCode) : that.responseCode != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        int result = responseCode != null ? responseCode.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "responseCode='" + responseCode + '\'' +
                ", id=" + id +
                '}';
    }
}
