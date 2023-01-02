package ru.netology.servlet;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.repository.PostRepositoryImpl;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;
  private PostService service;
  private PostRepository repository;

  private final static String METHOD_GET = "GET";
  private final static String METHOD_POST = "POST";
  private final static String METHOD_DELETE = "DELETE";


  @Override
  public void init() {
    ApplicationContext context = new AnnotationConfigApplicationContext("ru.netology");
    repository = context.getBean(PostRepository.class);
    service = context.getBean(PostService.class);
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals(METHOD_GET) && path.equals("/api/posts")) {
        controller.all(resp);
        return;
      }
      if (method.equals(METHOD_GET) && path.matches("/api/posts/\\d+")) {
        // easy way
        final var id = getId(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals(METHOD_POST) && path.equals("/api/posts")) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals(METHOD_DELETE) && path.matches("/api/posts/\\d+")) {
        // easy way
        final var id = getId(path);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private Long getId(String temp){
    return Long.parseLong(temp.substring(temp.lastIndexOf("/") + 1));
  }
}

