package com.hardin.wilson.app;

import com.codahale.metrics.health.HealthCheck;

/**
 * Health check class, we can add additional things here later
 */

public class HomeHealthCheck extends HealthCheck {
    private final String template;

    public HomeHealthCheck(String template) {
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        final String saying = String.format(template, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("template doesn't include a name");
        }
        return Result.healthy();
    }
}