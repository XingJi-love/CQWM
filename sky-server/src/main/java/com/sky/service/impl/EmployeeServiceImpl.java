package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    // 注入员工mapper
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        // 获取登录信息
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对(使用md5加密算法对前端传来的明文密码进行加密，再进行比对)
        // 进行md4加密
        password = DigestUtils.md5DigestAsHex(password.getBytes()); // 加密后的密码
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     * @return
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        // 封装数据
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置账号的状态，默认正常状态1表示正常，0表示禁用
        employee.setStatus(StatusConstant.ENABLE);

        // 设置密码，默认密码123456(使用md5加密算法对明文密码进行加密)
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // TODO 改为当前登录员工id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        // 保存数据
        employeeMapper.insert(employee);
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // select * from employee limit 0,10; 分页查询
        // getPage() 当前页码  getPageSize() 每页显示的条数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());

        // 执行分页查询
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        // 获取总记录数和当前页数据集合
        long total = page.getTotal();
        List<Employee> records = page.getResult();

        // 封装数据返回
        return new PageResult(total, records);
    }


    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // update employee set status = ? where id = ?;
        // 动态修改属性 status id 封装为一个对象 employee 对象 封装为对象的好处：方便扩展字段
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();

        // 更新员工状态
        employeeMapper.update(employee);
    }


    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        // 根据id查询员工
        Employee employee = employeeMapper.getById(id);

        // 传给前端的数据是脱敏后的数据
        employee.setPassword("****");

        // 返回数据
        return employee;
    }


    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        // 封装数据
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置当前记录的修改时间
        employee.setUpdateTime(LocalDateTime.now());

        // 当前登录员工id
        employee.setUpdateUser(BaseContext.getCurrentId());

        // 更新数据
        employeeMapper.update(employee);
    }
}
