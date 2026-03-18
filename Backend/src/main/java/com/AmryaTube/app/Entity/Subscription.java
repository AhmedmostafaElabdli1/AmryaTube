package com.AmryaTube.app.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "subscription",
        uniqueConstraints = {
        @UniqueConstraint(
                columnNames =
                        {"user_id","channel_id"}
        )
        }
        )
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User subscriber;

    @ManyToOne()
    @JoinColumn(name = "channel_id")
    private Channel channel;

    private Date subscribedAt;

    private boolean notificationEnabled;


}
