package com.sky.mapper;

import com.sky.anno.AutoFill;
import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    @AutoFill(value = OperationType.INSERT) // 切面编程
    public void insert(Employee employee);

    /**
     * 根据姓名查询员工
     * @param name
     * @return
     */
    public List<Employee> query(String name);

    /**
     * 修改员工信息
     * @param employee
     */
    @AutoFill(value = OperationType.UPDATE) // 切面编程
    public void update(Employee employee);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    public EmployeeDTO getById(Long id);
}
