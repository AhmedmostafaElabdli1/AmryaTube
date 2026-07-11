package com.AmryaTube.app.comment.repository;

import com.AmryaTube.app.comment.entity.Comment;
import com.AmryaTube.app.video.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findAllByVideoAndParentIsNull(Video video, Pageable pageable);
    Page<Comment> findAllByParentId(UUID parentId, Pageable pageable);
}
