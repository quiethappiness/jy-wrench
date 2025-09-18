-- 带TTL管理的设置操作（增强版）
-- KEYS[1]: 键名
-- ARGV[1]: 值
-- ARGV[2]: TTL（秒）

-- 安全转换函数
local function safe_tonumber(str)
    if type(str) == "number" then
        return str
    end
    if type(str) ~= "string" then
        return nil
    end

    -- 移除非数字字符（保留负号）
    str = string.gsub(str, "[^0-9%-]", "")
    return tonumber(str)
end

-- 将参数转换为数字
local delta = safe_tonumber(ARGV[1])
-- 获取当前TTL
local ttlRemain = redis.call('ttl', KEYS[1])
-- 转换TTL参数
local ttl = safe_tonumber(ARGV[2])

-- 验证转换是否成功
if delta == nil then
    return redis.error_reply("Invalid delta value: " .. tostring(ARGV[1]))
end
if ttl == nil then
    return redis.error_reply("Invalid TTL value: " .. tostring(ARGV[2]))
end


if ttlRemain == -2 then  -- Key不存在
    -- 设置值并指定TTL
    redis.call('set', KEYS[1], ARGV[1], 'EX', ttl)
elseif ttlRemain == -1 then  -- Key存在但无过期时间
    -- 仅设置值（保留永久状态）
    redis.call('set', KEYS[1], ARGV[1])
else  -- Key存在且有过期时间
    -- 设置值并保留剩余TTL
    redis.call('set', KEYS[1], ARGV[1])
    redis.call('expire', KEYS[1], ttlRemain)
end

return "OK"