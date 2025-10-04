package io.github.quiethappiness.wrencher.infrastructure.dao;

import io.github.quiethappiness.db.router.types.annotations.DBRouter;
import io.github.quiethappiness.wrencher.infrastructure.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserDao {

     @DBRouter(key = "userId")
     User queryUserInfoByUserId(User req);

     @DBRouter(key = "userId")
     void insertUser(User req);

}
