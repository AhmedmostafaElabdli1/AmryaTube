package com.AmryaTube.app.playlist.entity;

import com.AmryaTube.app.video.entity.Video;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Association entity for the Playlist <-> Video many-to-many relationship.
 * Promoted from a plain @ManyToMany join table so the link can carry its own
 * data: the video's position in the playlist and when it was added.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "playlist_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_playlist_video",
                columnNames = {"playlist_id", "video_id"}
        ),
        indexes = @Index(name = "idx_playlist_position", columnList = "playlist_id, position")
)
public class PlaylistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    // 0-based order of the video within the playlist
    @Column(nullable = false)
    private Integer position;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime addedAt;

    @CreatedBy
    @Column(updatable = false)
    private String addedBy;
}