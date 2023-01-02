package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.repository.PostRepositoryImpl;
import ru.netology.service.PostService;

@Configuration
public class AppConfig {

    @Bean
    PostRepository repository(){
        return new PostRepositoryImpl();
    }

    @Bean
    PostService service(){
        return new PostService(repository());
    }

    @Bean
    PostController controller(){
        return new PostController(service());
    }
}
