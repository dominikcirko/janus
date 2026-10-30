package janus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersioningConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .usePathSegment(1, path ->
                        path.pathWithinApplication()
                                .value()
                                .startsWith("/api/"))
                .setVersionRequired(false)
                .addSupportedVersions("1", "2")
                .detectSupportedVersions(false);
    }
}
