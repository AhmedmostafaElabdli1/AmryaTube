package com.AmryaTube.app.subscription.repository;

import com.AmryaTube.app.channel.entity.Channel;
import com.AmryaTube.app.subscription.entity.Subscription;
import com.AmryaTube.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findBySubscriberAndChannel(User subscriber, Channel channel);
    boolean existsBySubscriberAndChannel(User subscriber, Channel channel);
    List<Subscription> findAllBySubscriber(User subscriber);
    long countByChannel(Channel channel);
}
