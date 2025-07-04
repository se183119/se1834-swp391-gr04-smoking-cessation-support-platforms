package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.BlogPost;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {

    private final BlogPostRepository blogPostRepository;

    public List<BlogPost> getAllPublishedPosts(BlogPost.BlogStatus status) {
        return blogPostRepository.findPublishedPostsOrderByPublishedAtDesc(status);
    }

    public List<BlogPost> getPopularPosts() {
        return blogPostRepository.findPopularPosts();
    }

    public List<BlogPost> getPostsByAuthor(Long authorId) {
        return blogPostRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    public List<BlogPost> searchPosts(String keyword) {
        return blogPostRepository.searchByKeyword(keyword);
    }

    public List<BlogPost> getPostsByTag(String tag) {
        return blogPostRepository.findByTag(tag);
    }

    public Optional<BlogPost> getPostById(Long id) {
        return blogPostRepository.findById(id);
    }

    // Tạo bài blog mới
    public BlogPost createPost(Long authorId, String title, String content,
                              String excerpt, String featuredImage, String tags) {
        User author = new User();
        author.setId(authorId);

        BlogPost post = new BlogPost();
        post.setAuthor(author);
        post.setTitle(title);
        post.setContent(content);
        post.setExcerpt(excerpt);
        post.setFeaturedImage(featuredImage);
        post.setTags(tags);
        post.setStatus(BlogPost.BlogStatus.DRAFT);

        return blogPostRepository.save(post);
    }

    // Cập nhật bài blog
    public BlogPost updatePost(Long postId, String title, String content,
                              String excerpt, String featuredImage, String tags) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        post.setTitle(title);
        post.setContent(content);
        post.setExcerpt(excerpt);
        post.setFeaturedImage(featuredImage);
        post.setTags(tags);

        return blogPostRepository.save(post);
    }

    // Xuất bản bài blog
    public BlogPost publishPost(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        post.setStatus(BlogPost.BlogStatus.PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        return blogPostRepository.save(post);
    }

    // Ẩn bài blog
    public BlogPost archivePost(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        post.setStatus(BlogPost.BlogStatus.ARCHIVED);

        return blogPostRepository.save(post);
    }

    // Tăng lượt xem
    public void incrementViewCount(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        post.setViewsCount(post.getViewsCount() + 1);
        blogPostRepository.save(post);
    }

    // Like bài viết
    public BlogPost likePost(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        post.setLikesCount(post.getLikesCount() + 1);

        return blogPostRepository.save(post);
    }

    // Unlike bài viết
    public BlogPost unlikePost(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));

        if (post.getLikesCount() > 0) {
            post.setLikesCount(post.getLikesCount() - 1);
        }

        return blogPostRepository.save(post);
    }

    // Xóa bài viết
    public void deletePost(Long postId) {
        blogPostRepository.deleteById(postId);
    }
}
