package io.github.quiethappiness.wrencher.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfo {

    //code、info可以统一定义一个类
    private String code;
    private String info;

    private String name;
    private Integer age;
    private String address;

}
