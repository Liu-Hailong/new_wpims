package com.project.model;

import lombok.Data;

@Data
public class Account {
    private String OpenId;
    private String SessionId;
    private Integer userid;
    private Integer invited_userid;
    private String password;

    public Boolean valid(){
        return OpenId != null && !OpenId.isEmpty();
    }
}
