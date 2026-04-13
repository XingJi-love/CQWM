package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    // TODO 捕获数据库重复键值异常(新增员工时出现相同username时，提示已存在)
    /**
     * 捕获数据库重复键值异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // Duplicate entry 'zhangsan' for key 'employee.idx_username' 重复键值异常
        // 提取异常信息
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")) {
            // 提取异常信息中的键值
            String[] split = message.split(" "); // 切割字符串
            String username = split[2]; // 提取键值
            String msg = username + MessageConstant.ALREADY_EXISTS; // 拼接错误信息 "zhangsan已存在"
            // 返回错误信息
            return Result.error(msg);
        }else  {
            // 未知错误
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
