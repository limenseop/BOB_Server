package com.bob_senior.bob_server.domain.Post.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class PostPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postPhotoIdx;

    @Embedded
    private PhotoId photoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postIdx")
    private Post post;

}
