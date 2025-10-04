package io.github.quiethappiness.wrencher.infrastructure.po;

import io.github.quiethappiness.db.router.domain.model.DBRouterBase;
import lombok.*;

import java.util.Date;

/**
 * 博客：https://bugstack.cn - 沉淀、分享、成长，让自己和他人都能有所收获！
 * 公众号：bugstack虫洞栈
 * Create by 小傅哥(fustack)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends DBRouterBase
{

    private Long id;
    private String userId;          // 用户ID
    private String userNickName;    // 昵称
    private String userHead;        // 头像
    private String userPassword;    // 密码
    private Date createTime;        // 创建时间
    private Date updateTime;        // 更新时间
    
    public User(String number)
    {
        super();
    }
}
