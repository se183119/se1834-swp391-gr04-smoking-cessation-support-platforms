package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.Like;
import com.smokingcessation.platform.entity.SocialPost;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.UserAchievement;
import com.smokingcessation.platform.repository.LikeRepository;
import com.smokingcessation.platform.repository.SocialPostRepository;
import com.smokingcessation.platform.repository.UserAchievementRepository;
import com.smokingcessation.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialPostService {

    private final SocialPostRepository socialPostRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final LikeRepository likeRepository; // ✅ THÊM
    private final UserRepository userRepository; // ✅ THÊM

    public List<SocialPost> getAllActivePosts() {
        return socialPostRepository.findActivePostsOrderByCreatedAtDesc();
    }

    public List<SocialPost> getPopularPosts() {
        return socialPostRepository.findPopularPosts();
    }

    public List<SocialPost> getAchievementSharePosts() {
        return socialPostRepository.findAchievementSharePosts();
    }

    public List<SocialPost> getUserPosts(Long userId) {
        return socialPostRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public SocialPost createPost(Long userId, String content, SocialPost.PostType type) {
        User user = new User();
        user.setId(userId);

        SocialPost post = new SocialPost();
        post.setUser(user);
        post.setContent(content);
        post.setType(type);
        post.setStatus(SocialPost.PostStatus.ACTIVE);

        return socialPostRepository.save(post);
    }

    // Chia sẻ huy hiệu thành tích
    public SocialPost shareAchievement(Long userId, Long userAchievementId, String message) {
        User user = new User();
        user.setId(userId);

        UserAchievement userAchievement = userAchievementRepository.findById(userAchievementId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy huy hiệu"));

        // Đánh dấu huy hiệu đã được chia sẻ
        userAchievement.setIsShared(true);
        userAchievement.setSharedAt(java.time.LocalDateTime.now());
        userAchievementRepository.save(userAchievement);

        SocialPost post = new SocialPost();
        post.setUser(user);
        post.setContent(message);
        post.setType(SocialPost.PostType.ACHIEVEMENT_SHARE);
        post.setSharedAchievement(userAchievement);
        post.setStatus(SocialPost.PostStatus.ACTIVE);

        return socialPostRepository.save(post);
    }

    // Đăng bài động viên
    public SocialPost createMotivationPost(Long userId, String content) {
        return createPost(userId, content, SocialPost.PostType.MOTIVATION);
    }

    // Chia sẻ kinh nghiệm
    public SocialPost shareExperience(Long userId, String experience) {
        return createPost(userId, experience, SocialPost.PostType.EXPERIENCE_SHARE);
    }

    // Đặt câu hỏi
    public SocialPost askQuestion(Long userId, String question) {
        return createPost(userId, question, SocialPost.PostType.QUESTION);
    }

    // Đưa ra lời khuyên
    public SocialPost giveAdvice(Long userId, String advice) {
        return createPost(userId, advice, SocialPost.PostType.ADVICE);
    }

    // public SocialPost likePost(Long postId) {
    //     SocialPost post = socialPostRepository.findById(postId)
    //         .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

    //     post.setLikesCount(post.getLikesCount() + 1);
    //     return socialPostRepository.save(post);
    // }

    // public SocialPost unlikePost(Long postId) {
    //     SocialPost post = socialPostRepository.findById(postId)
    //         .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

    //     if (post.getLikesCount() > 0) {
    //         post.setLikesCount(post.getLikesCount() - 1);
    //     }
    //     return socialPostRepository.save(post);
    // }

    public SocialPost likePost(Long userId, Long postId) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        
        // Kiểm tra post tồn tại
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));
        
        // Kiểm tra đã like chưa
        if (likeRepository.existsByUserIdAndPostIdAndPostType(userId, postId, "SOCIAL_POST")) {
            throw new RuntimeException("Đã like bài đăng này rồi");
        }
        
        // Tạo like record
        Like like = new Like();
        like.setUser(user);
        like.setPostId(postId);
        like.setPostType("SOCIAL_POST");
        likeRepository.save(like);
        
        // Cập nhật count
        post.setLikesCount(likeRepository.countByPostIdAndPostType(postId, "SOCIAL_POST"));
        return socialPostRepository.save(post);
    }

    public SocialPost unlikePost(Long userId, Long postId) {
        // Kiểm tra user tồn tại
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        
        // Kiểm tra post tồn tại
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));
        
        // Kiểm tra đã like chưa
        if (!likeRepository.existsByUserIdAndPostIdAndPostType(userId, postId, "SOCIAL_POST")) {
            throw new RuntimeException("Chưa like bài đăng này");
        }
        
        // Xóa like record
        likeRepository.deleteByUserIdAndPostIdAndPostType(userId, postId, "SOCIAL_POST");
        
        // Cập nhật count
        post.setLikesCount(likeRepository.countByPostIdAndPostType(postId, "SOCIAL_POST"));
        return socialPostRepository.save(post);
    }

    public SocialPost updatePost(Long postId, String content) {
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        post.setContent(content);
        return socialPostRepository.save(post);
    }

    public void deletePost(Long postId) {
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        post.setStatus(SocialPost.PostStatus.DELETED);
        socialPostRepository.save(post);
    }

    public void reportPost(Long postId) {
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        post.setStatus(SocialPost.PostStatus.REPORTED);
        socialPostRepository.save(post);
    }

    public void pinPost(Long postId) {
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        post.setIsPinned(true);
        socialPostRepository.save(post);
    }

    public void unpinPost(Long postId) {
        SocialPost post = socialPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng"));

        post.setIsPinned(false);
        socialPostRepository.save(post);
    }
}
