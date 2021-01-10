package ru.pyatkinmv;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import ru.pyatkinmv.consume.api.LikeConsumer;
import ru.pyatkinmv.consume.api.PostConsumer;
import ru.pyatkinmv.consume.api.SubscribeConsumer;
import ru.pyatkinmv.consume.impl.LikeConsumerImpl;
import ru.pyatkinmv.consume.impl.PostConsumerImpl;
import ru.pyatkinmv.consume.impl.SubscribeConsumerImpl;
import ru.pyatkinmv.dao.PostRepository;
import ru.pyatkinmv.supply.api.LikeSupplier;
import ru.pyatkinmv.supply.api.PostSupplier;
import ru.pyatkinmv.supply.api.SubscribeSupplier;
import ru.pyatkinmv.supply.impl.LikeSupplierImpl;
import ru.pyatkinmv.supply.impl.PostSupplierImpl;
import ru.pyatkinmv.supply.impl.SubscribeSupplierImpl;

@SpringBootConfiguration
public class BotfarmAppConfig {
    @Value("${vk.token}")
    private String token;

    @Value("${vk.user-id}")
    private Integer userId;

    @Bean
    public VkApiClient vk() {
        return new VkApiClient(HttpTransportClient.getInstance());
    }

    @Bean
    public UserActor actor() {
        return new UserActor(userId, token);
    }

    @Bean
    public LikeConsumer likeConsumer() {
        return new LikeConsumerImpl(vk(), actor());
    }

    @Bean
    public SubscribeConsumer subscribeConsumer() {
        return new SubscribeConsumerImpl(vk(), actor());
    }

    @Bean
    public PostConsumer postConsumer() {
        return new PostConsumerImpl(vk(), actor());
    }

    @Bean
    public LikeSupplier likeSupplier() {
        return new LikeSupplierImpl(vk(), actor());
    }

    @Bean
    public SubscribeSupplier subscribeSupplier() {
        return new SubscribeSupplierImpl(vk(), actor());
    }

    @Bean
    public PostSupplier postSupplier(@Autowired PostRepository postRepository) {
        return new PostSupplierImpl(vk(), actor(), postRepository);
    }
}

