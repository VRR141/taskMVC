package ru.netology.repository;

import ru.netology.exception.IncorrectIdException;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PostRepositoryImpl implements PostRepository {

  private ConcurrentHashMap<Long, Post> storage;

  private AtomicLong aLong;

  public PostRepositoryImpl() {
    storage = new ConcurrentHashMap<>();
    aLong = new AtomicLong(1);
  }

  public List<Post> all() {
    Collection<Post> posts = storage.values();
    List<Post> result = posts.stream().collect(Collectors.toList());
    return result;
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(storage.get(id));
  }

  public Post save(Post post) {
    long id = post.getId();
    if (id < 0){
      throw new IncorrectIdException("id must be positive or 0");
    }
    if (id == 0){
      createNewPost(post);
      return post;
    }
    Post temp = storage.get(id);
    if (temp == null){
      throw new NotFoundException("post is null, can't update");
    }
    storage.put(id, post);
    return post;
  }

  public void removeById(long id) {
    if (storage.containsKey(id)) {
      storage.remove(id);
      return;
    }
    throw new NotFoundException();
  }

  private void createNewPost(Post post){
    post.setId(aLong.get());
    storage.put(aLong.get(), post);
    aLong.getAndIncrement();
  }
}
