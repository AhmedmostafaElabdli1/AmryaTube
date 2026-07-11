package com.AmryaTube.app.subscription.service;

import com.AmryaTube.app.channel.repository.ChannelRepository;
import com.AmryaTube.app.subscription.repository.SubscriptionRepository;
import com.AmryaTube.app.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               ChannelRepository channelRepository,
                               UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }
}
