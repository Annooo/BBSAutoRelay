package com.cn.BBSAutoRelay.controller;

import com.cn.BBSAutoRelay.annotation.LoggerManage;
import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.model.ResultMap;
import com.cn.BBSAutoRelay.service.AccountService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Administrator on 2017/8/16.
 */
@Controller
@RequestMapping(value = "/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping("/goAccountList")
    public String goAccount(){
        return "/account/accountList";
    }
    @ResponseBody
    @PostMapping("/add")
    public int addAccount(Account account){
        return accountService.addAccount(account);
    }

    @LoggerManage(description = "滑稽")
    @ResponseBody
    @GetMapping("/queryAccountList")
    public ResultMap queryAccountList(
            @RequestParam(name = "pageNum", required = false) int pageNum,
            @RequestParam(name = "pageSize", required = false) int pageSize){
        PageInfo page = accountService.queryAccounts(pageNum,pageSize);
        return new ResultMap("", page.getList(),0, page.getTotal());
    }
}
