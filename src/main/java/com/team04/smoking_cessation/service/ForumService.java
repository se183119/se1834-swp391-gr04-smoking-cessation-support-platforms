package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.ForumPost;
import com.team04.smoking_cessation.entity.PostLike;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.repository.ForumPostRepository;
import com.team04.smoking_cessation.repository.PostLikeRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ForumService {

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;

    public Page<ForumPost> getForumPosts(Pageable pageable, ForumPost.PostCategory category) {
        if (category != null) {
            return forumPostRepository.findByCategoryAndParentPostIsNullAndStatusOrderByCreatedAtDesc(
                category, ForumPost.PostStatus.ACTIVE, pageable);
        } else {
            return forumPostRepository.findByParentPostIsNullAndStatusOrderByCreatedAtDesc(
                ForumPost.PostStatus.ACTIVE, pageable);
        }
    }

    public ForumPost createPost(String email, Map<String, Object> postData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ForumPost post = new ForumPost();
        post.setUser(user);
        post.setTitle((String) postData.get("title"));
        post.setContent((String) postData.get("content"));

        if (postData.containsKey("category")) {
            post.setCategory(ForumPost.PostCategory.valueOf((String) postData.get("category")));
        } else {
            post.setCategory(ForumPost.PostCategory.GENERAL_DISCUSSION);
        }

        return forumPostRepository.save(post);
    }

    public Map<String, Object> getPostWithReplies(Long postId) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        // Increment view count
        post.setViewCount(post.getViewCount() + 1);
        forumPostRepository.save(post);

        List<ForumPost> replies = forumPostRepository.findByParentPostOrderByCreatedAtAsc(post);

        Map<String, Object> result = new HashMap<>();
        result.put("post", post);
        result.put("replies", replies);
        result.put("replyCount", replies.size());

        return result;
    }

    public ForumPost replyToPost(String email, Long postId, Map<String, Object> replyData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ForumPost parentPost = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        ForumPost reply = new ForumPost();
        reply.setUser(user);
        reply.setContent((String) replyData.get("content"));
        reply.setParentPost(parentPost);
        reply.setCategory(parentPost.getCategory());

        return forumPostRepository.save(reply);
    }

    public Map<String, Object> togglePostLike(String email, Long postId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> existingLike = postLikeRepository.findByUserAndPost(user, post);

        Map<String, Object> result = new HashMap<>();

        if (existingLike.isPresent()) {
            // Unlike
            postLikeRepository.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            result.put("action", "unliked");
        } else {
            // Like
            PostLike like = new PostLike(user, post);
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            result.put("action", "liked");
        }

        forumPostRepository.save(post);
        result.put("likeCount", post.getLikeCount());

        return result;
    }

    public Page<ForumPost> searchPosts(String query, Pageable pageable) {
        return forumPostRepository.searchPosts(query, ForumPost.PostStatus.ACTIVE, pageable);
    }

    public List<ForumPost> getUserPosts(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return forumPostRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Map<String, Object> getForumStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPosts", forumPostRepository.count());
        stats.put("totalUsers", userRepository.count());

        List<ForumPost> pinnedPosts = forumPostRepository.findPinnedPosts(ForumPost.PostStatus.ACTIVE);
        stats.put("pinnedPosts", pinnedPosts);

        return stats;
    }

    public void deletePost(String email, Long postId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().equals(user)) {
            throw new RuntimeException("Access denied: You can only delete your own posts");
        }

        post.setStatus(ForumPost.PostStatus.DELETED);
        forumPostRepository.save(post);
    }
}
