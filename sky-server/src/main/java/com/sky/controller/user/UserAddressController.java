package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @PostMapping
    @Operation(summary = "新增地址")
    public Result<String> save(@RequestBody AddressBook addressBook){
        userAddressService.save(addressBook);
        return Result.success("新增地址成功");
    }

    @GetMapping("/list")
    @Operation(summary = "地址列表")
    public Result<List<AddressBook>> list(){
        List<AddressBook> list =  userAddressService.list();
        return Result.success(list);
    }

    @GetMapping("/default")
    @Operation(summary = "获取默认地址")
    public Result<AddressBook> getDefault(){
        AddressBook addressBook = userAddressService.getDefault();
        return Result.success(addressBook);
    }

    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook){
        userAddressService.update(addressBook);
        return Result.success("修改地址成功");
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam Long id){
        userAddressService.delete(id);
        return Result.success("删除地址成功");
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = userAddressService.getById(id);
        return Result.success(addressBook);
    }

    @PutMapping("/default")
    @Operation(summary = "设置默认地址")
    public Result<String> setDefault(@RequestBody AddressBook addressBook){
        userAddressService.setDefault(addressBook);
        return Result.success("设置默认地址成功");
    }
}
