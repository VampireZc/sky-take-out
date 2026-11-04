package com.sky.mapper;

import com.sky.dto.EmployeeDTO;
import com.sky.entity.Employee;
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
    public void insert(Employee employee);

    public List<Employee> query(String name);

    public void update(Employee employee);

    public EmployeeDTO getById(Long id);

    public void updateStatus(Integer status, Long id);
}
