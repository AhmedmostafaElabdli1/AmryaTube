package com.AmryaTube.app.video.repository;

import com.AmryaTube.app.channel.entity.Channel;
import com.AmryaTube.app.video.entity.Video;
import com.AmryaTube.app.video.enums.VideoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    Page<Video> findAllByChannel(Channel channel, Pageable pageable);
    Page<Video> findAllByStatus(VideoStatus status, Pageable pageable);
    Page<Video> findAllByTitleContainingIgnoreCaseAndStatus(String title, VideoStatus status, Pageable pageable);
}
