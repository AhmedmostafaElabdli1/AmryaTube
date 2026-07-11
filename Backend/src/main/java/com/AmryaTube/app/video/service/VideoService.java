package com.AmryaTube.app.video.service;

import com.AmryaTube.app.channel.repository.ChannelRepository;
import com.AmryaTube.app.video.repository.VideoRepository;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final ChannelRepository channelRepository;

    public VideoService(VideoRepository videoRepository, ChannelRepository channelRepository) {
        this.videoRepository = videoRepository;
        this.channelRepository = channelRepository;
    }
}
