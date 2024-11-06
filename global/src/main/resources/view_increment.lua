local count_key = KEYS[1]         -- 조회수 또는 광고 시청 수를 증가시킬 키
local ttl_key = KEYS[2]           -- 중복 방지를 위한 TTL 키
local user_id = ARGV[1]           -- 사용자 ID 또는 고유 인증키
local owner_id = ARGV[2]          -- 동영상 소유자 ID
local ttl_value = tonumber(ARGV[3])   -- TTL 시간 (초)
local increment_value = tonumber(ARGV[4]) -- 증가할 값 (1)

-- 게시자가 자신의 동영상을 시청한 경우, 어뷰징으로 간주하여 0 반환
if user_id == owner_id then
    return -1  -- owner 재생으로 조회수 증가 안함
end

-- TTL 키가 존재하면 중복 요청으로 간주하고 0 반환
if redis.call("EXISTS", ttl_key) == 1 then
    return -2  -- 중복 요청으로 조회수 증가 안함
else
    -- 첫 재생 시 카운트 증가 및 TTL 설정
    redis.call("INCRBY", count_key, increment_value)
    redis.call("SET", ttl_key, "1", "EX", ttl_value)
    return redis.call("GET", count_key)  -- 증가된 조회수를 반환
end
