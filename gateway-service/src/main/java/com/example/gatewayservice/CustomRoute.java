package com.example.gatewayservice;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomRoute {

    @Bean
    public RouteLocator cRoute(RouteLocatorBuilder builder) {
        return builder.routes()

                .route(p -> p.path("/global/**")
                        .uri("lb://GLOBAL"))

                .route(p -> p.path("/content-service/videos/**")
                        .filters(f -> f.rewritePath("/content-service/videos/(?<segment>.*)", "/videos/${segment}"))
                        .uri("lb://CONTENT-SERVICE"))
                .route(p -> p.path("/videos/**")
                        .uri("lb://CONTENT-SERVICE"))

                .route(p -> p.path("/global/join")
                        .filters(f -> f.rewritePath("/global/(?<segment>.*)", "/${segment}"))
                        .uri("lb://GLOBAL"))
                .route(p -> p.path("/join")
                        .uri("lb://GLOBAL"))

                .route(p -> p.path("/batch-service/revenues/**")
                        .filters(f -> f.rewritePath("/batch-service/revenues/(?<segment>.*)", "/revenues/${segment}"))
                        .uri("lb://BATCH-SERVICE"))
                .route(p -> p.path("/revenues/**")
                        .uri("lb://BATCH-SERVICE"))

                .route(p -> p.path("/batch-service/top5/**")
                        .filters(f -> f.rewritePath("/batch-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://BATCH-SERVICE"))
                .route(p -> p.path("/top5/**")
                        .uri("lb://BATCH-SERVICE"))

                .route(p -> p.path("/batch-service/run-day-batch-job")
                        .filters(f -> f.rewritePath("/batch-service/(?<segment>.*)", "/${segment}"))
                        .uri("lb://BATCH-SERVICE"))
                .route(p -> p.path("/run-day-batch-job")
                        .uri("lb://BATCH-SERVICE"))

                .build();
    }



}