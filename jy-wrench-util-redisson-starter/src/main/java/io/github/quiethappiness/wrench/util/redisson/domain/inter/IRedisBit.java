package io.github.quiethappiness.wrench.util.redisson.domain.inter;

import org.redisson.api.RBitSet;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface IRedisBit
{
	RBitSet getBitSet(String key);
	
	/**
	 * 获取BitIndex
	 * @param userId ,可以是各种 id，例如手机号，邮箱，用户名等等
	 * 	用户ID
	 * @return BitIndex
	 */
	default int getBitIndex(String userId)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hashBytes = md.digest(userId.getBytes(StandardCharsets.UTF_8));
			// 将哈希字节数组转换为正整数
			BigInteger bigInt = new BigInteger(1, hashBytes);
			// 取模以确保索引在合理范围内
			return bigInt.mod(BigInteger.valueOf(Integer.MAX_VALUE))
				.intValue();
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException("MD5 algorithm not found", e);
		}
	}
}