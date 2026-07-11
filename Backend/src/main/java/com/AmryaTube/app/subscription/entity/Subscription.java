package com.AmryaTube.app.subscription.entity;

import com.AmryaTube.app.channel.entity.Channel;
import com.AmryaTube.app.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"subscriber_id", "channel_id"}))
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriber_id", nullable = false)
    private User subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(nullable = false)
    @Builder.Default
    private Boolean notificationEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isBlocked = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime subscribedAt;
}
