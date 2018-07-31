package com.cn.BBSAutoRelay.controller;

import com.cn.BBSAutoRelay.model.Account;
import com.cn.BBSAutoRelay.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        return "account/accountList";
    }
    @ResponseBody
    @PostMapping("/add")
    public int addAccount(Account account){
        return accountService.addAccount(account);
    }

    @ResponseBody
    @GetMapping("/queryAccountList/{pageNum}/{pageSize}")
    public Object queryAccountList(
            @PathVariable(name = "pageNum", required = false)
                    int pageNum,
            @PathVariable(name = "pageSize", required = false)
                    int pageSize){
        return accountService.queryAccounts(pageNum,pageSize);
    }
}
