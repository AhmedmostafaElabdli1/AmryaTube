package com.AmryaTube.app.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "video")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @NotBlank
    private String title;

    @NotBlank // not null + not "" or " " actual charachters only
    private String description;


    @NotNull
    private Long duration;


    @NotBlank
    private String imgPath;

    @NotBlank
    private String videoPath;

    private Long views= 0L;
    private Long likeCount=0L;
    private Long dislikeCount=0L;
    private Long commentCount=0L;


    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;



}
