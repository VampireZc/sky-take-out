package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {
    void insert(AddressBook addressBook);

    List<AddressBook> list(Long id);

    @Select("select * from address_book where user_id = #{currentId} and is_default = 1")
    AddressBook getDefault(Long currentId);

    void update(AddressBook addressBook);

    @Delete("delete from address_book where user_id = #{currentId} and id = #{id} ")
    void delete(Long id, Long currentId);

    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    @Update("update address_book set is_default = 1 where user_id = #{currentId} and id = #{id}")
    void setDefault(Long currentId, Long id);

}
