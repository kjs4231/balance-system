package com.example.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class LuaScriptConfig {

    @Bean
    public RedisScript<Long> luaScript() {
        return RedisScript.of(new ClassPathResource("view_increment.lua"), Long.class);
    }

}