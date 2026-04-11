package com.sky.context;


// 定义一个线程局部变量，用于保存当前登录员工的id
public class BaseContext {

    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    // 设置当前线程的登录员工id
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    // 获取当前线程的登录员工id
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    // 移除当前线程的登录员工id
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
