package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface UserAddressService {
    // 新增地址
    void save(AddressBook addressBook);

    // 显示该用户的所有地址
    List<AddressBook> list();

    // 查询用户默认地址
    AddressBook getDefault();

    // 修改地址
    void update(AddressBook addressBook);

    // 删除地址
    void delete(Long id);

    // 根据id查询
    AddressBook getById(Long id);

    // 设置默认地址
    void setDefault(AddressBook addressBook);
}
