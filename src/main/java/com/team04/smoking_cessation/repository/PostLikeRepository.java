package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.PostLike;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserAndPost(User user, ForumPost post);

    Long countByPost(ForumPost post);

    boolean existsByUserAndPost(User user, ForumPost post);
}
