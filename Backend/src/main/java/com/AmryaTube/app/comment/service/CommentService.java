package com.AmryaTube.app.comment.service;

import com.AmryaTube.app.comment.repository.CommentRepository;
import com.AmryaTube.app.video.repository.VideoRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;

    public CommentService(CommentRepository commentRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
    }
}
