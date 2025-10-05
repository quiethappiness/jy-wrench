package io.github.quiethappiness.wrencher.infrastructure.dao;

import io.github.quiethappiness.wrench.db.router.types.annotations.DBRouter;
import io.github.quiethappiness.wrencher.infrastructure.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserDao {

     @DBRouter(field = "userId")
     User queryUserInfoByUserId(User req);

     @DBRouter(field = "userId")
     void insertUser(User req);

}
