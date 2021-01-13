package ru.pyatkinmv;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootConfiguration
@EnableScheduling
public class BotfarmAppConfig {
    @Bean
    public VkApiClient vk() {
        return new VkApiClient(HttpTransportClient.getInstance());
    }
}

