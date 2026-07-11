package com.AmryaTube.app.channel.repository;

import com.AmryaTube.app.channel.entity.Channel;
import com.AmryaTube.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    Optional<Channel> findByHandle(String handle);
    boolean existsByHandle(String handle);
    List<Channel> findAllByOwner(User owner);
}
