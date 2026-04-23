package com.airquality.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 注销账号DTO
 */
@Data
public class DeleteAccountDTO {

    /**
     * 密码验证
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 确认注销
     */
    @NotBlank(message = "请确认注销操作")
    private String confirm;
}
