package com.bob_senior.bob_server.domain.Post.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postTagIdx;

    @Embedded
    private TagId tag;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "postIdx")
    private Post post;
}
