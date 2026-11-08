package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressMapper;
import com.sky.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        Long id = BaseContext.getCurrentId();
        return addressMapper.list(id);
    }

    @Override
    public AddressBook getDefault() {
        return addressMapper.getDefault(BaseContext.getCurrentId());
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressMapper.update(addressBook);
    }

    @Override
    public void delete(Long id) {
        Long currentId = BaseContext.getCurrentId();
        addressMapper.delete(id, currentId);

    }

    @Override
    public AddressBook getById(Long id) {
        return addressMapper.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(AddressBook address) {
        Long currentId = BaseContext.getCurrentId();
        Long id = address.getId();
        AddressBook addressBook = addressMapper.getDefault(currentId);
        // 存在默认地址
        if (addressBook != null) {
            addressBook.setIsDefault(0);
            // 把默认地址设置为非默认
            addressMapper.update(addressBook);
        }
        // 把当前地址设置为默认
        addressMapper.setDefault(currentId, id);
    }
}
