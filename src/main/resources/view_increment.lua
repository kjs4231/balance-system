local hash_key = KEYS[1]
local ttl_field = ARGV[1] -- TTL 필드 이름
local count_field = ARGV[2] -- 조회수 필드 이름
local ttl_value = tonumber(ARGV[3]) -- TTL 값
local increment_value = tonumber(ARGV[4]) -- 증가값

-- TTL 필드가 존재하면 중복 요청으로 간주
if redis.call('HGET', hash_key, ttl_field) then
    return -2
end

-- 조회수 증가 및 TTL 설정
local new_count = redis.call('HINCRBY', hash_key, count_field, increment_value)
redis.call('HSET', hash_key, ttl_field, '1')
redis.call('EXPIRE', hash_key, ttl_value)

return new_count