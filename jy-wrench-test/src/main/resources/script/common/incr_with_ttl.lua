-- 带TTL管理的自增操作
-- KEYS[1]: 键名
-- ARGV[1]: 增量值
-- ARGV[2]: TTL（秒）

-- 安全转换函数
local function safe_tonumber(str)
    if type(str) == "number" then
        return tonumber(str)
    end
    if type(str) ~= "string" then
        return nil
    end

    -- 移除可能的不可见字符
    str = string.gsub(str, "[^0-9%-]", "")
    return tonumber(str)
end

-- 将参数转换为数字
local delta = safe_tonumber(ARGV[1])
local ttl = safe_tonumber(ARGV[2])

-- 验证转换是否成功
if delta == nil then
    return redis.error_reply("Invalid delta value: " .. tostring(ARGV[1]))
end

if ttl == nil then
    return redis.error_reply("Invalid TTL value: " .. tostring(ARGV[2]))
end

-- 检查键是否存在
local exists = redis.call('exists', KEYS[1])
if exists == 0 then
    -- 首次设置：设置值并指定TTL
    redis.call('set', KEYS[1], delta, 'EX', ttl)
    return delta
else
    -- 后续操作：自增并保留原TTL
    return redis.call('incrby', KEYS[1], delta)
end