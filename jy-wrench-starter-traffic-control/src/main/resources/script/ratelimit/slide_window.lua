-- KEYS[1]: 用于限流的键，例如 rate_limit:user123
-- ARGV[1]: 当前时间戳（毫秒）
-- ARGV[2]: 时间窗口大小（毫秒）
-- ARGV[3]: 窗口内允许的最大请求数

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

local key = KEYS[1]
local now = safe_tonumber(ARGV[1])
local window = safe_tonumber(ARGV[2])
local limit = safe_tonumber(ARGV[3])

-- 验证参数转换是否成功
if now == nil then
    return redis.error_reply("Invalid now timestamp value: " .. tostring(ARGV[1]))
end

if window == nil then
    return redis.error_reply("Invalid window value: " .. tostring(ARGV[2]))
end

if limit == nil then
    return redis.error_reply("Invalid limit value: " .. tostring(ARGV[3]))
end

-- 1. 清除当前时间窗口之前（now - window）的过期记录
redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

-- 2. 获取当前窗口内的请求数量
local current = redis.call('ZCARD', key)

-- 3. 判断是否超过限流阈值
if current < limit then
    -- 4. 如果未超过，添加当前请求的时间戳（作为score和member）
    -- 使用随机数确保member唯一，避免覆盖同一毫秒内的请求
    redis.call('ZADD', key, now, now .. ':' .. math.random())
    -- 5. 为Key设置过期时间，避免内存无限增长（通常设置为窗口大小的2倍）
    redis.call('EXPIRE', key, window/1000 * 2)
    return 1 -- 表示允许请求
else
    return 0 -- 表示拒绝请求
end