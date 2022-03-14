package com.project.Utils;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Result<T> implements Serializable {
    public Boolean success;
    public String errorCode;
    public String msg;
    public Map<T, Object> data;
    public Result(){}
    public Result(Boolean Success, String ErrorCode, String Msg, Map<T, Object> data){
        this.success = Success;
        this.errorCode = ErrorCode;
        this.msg = Msg;
        this.data = data;
    }
}
