package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.IncorrectIdException;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {

  private ConcurrentHashMap<Long, Post> storage;

  private AtomicLong aLong;

  private ConcurrentHashMap<Long, Boolean> deletedTable;

  public PostRepositoryImpl() {
    storage = new ConcurrentHashMap<>();
    deletedTable = new ConcurrentHashMap<>();
    aLong = new AtomicLong(1);
    fillPosts();
  }

  public List<Post> all() {
    Collection<Post> posts = storage.values();
    List<Post> result = posts.stream().filter(post -> !deletedTable.get(post.getId())).collect(Collectors.toList());
    return result;
  }

  public Optional<Post> getById(long id) {
    boolean deleted;
    try {
      deleted = deletedTable.get(id);
    } catch (NullPointerException e){
      deleted = true;
    }
    if (deleted){
      throw new NotFoundException(String.format("post with id: %s not found", id));
    }
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
      deletedTable.put(id , true);
      return;
    }
    throw new NotFoundException();
  }

  private void createNewPost(Post post){
    post.setId(aLong.get());
    storage.put(aLong.get(), post);
    deletedTable.put(aLong.get(), false);
    aLong.getAndIncrement();
  }

  private void fillPosts(){
    for (int i = 0; i < 25; i++){
      Post post = new Post();
      post.setContent("some content " + i);
      createNewPost(post);
    }
  }
}
