package com.bob_senior.bob_server.domain.vote.entity;

import com.bob_senior.bob_server.domain.vote.entity.VoteId;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voteRecordIdx;

    @Embedded
    private VoteId voteId;

    @Column
    private String voteContent;

    @Column
    private Integer count;

    //참여한 user에 대한 정보를 어디서 보관해야되나....

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voteIdx")
    private Vote vote;
}
