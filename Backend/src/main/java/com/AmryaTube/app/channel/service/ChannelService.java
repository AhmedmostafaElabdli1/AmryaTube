package com.AmryaTube.app.channel.service;

import com.AmryaTube.app.channel.repository.ChannelRepository;
import com.AmryaTube.app.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public ChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }
}
