package com.AmryaTube.app.playlist.repository;

import com.AmryaTube.app.playlist.entity.Playlist;
import com.AmryaTube.app.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    List<Playlist> findAllByOwnerAndIsPublicTrue(User owner);
    List<Playlist> findAllByOwner(User owner);
}
