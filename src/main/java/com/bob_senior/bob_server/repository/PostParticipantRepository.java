package com.bob_senior.bob_server.repository;

import com.bob_senior.bob_server.domain.Post.entity.PostUser;
import com.bob_senior.bob_server.domain.Post.entity.PostParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostParticipantRepository extends JpaRepository<PostParticipant, Integer> {

    List<PostParticipant> findPostParticipantsByPostUser_PostIdxAndStatusAndPosition(Integer postIdx, String status,String position);

    boolean existsByPostUser(PostUser id);

    Long countByPostUser_PostIdxAndStatus(int postIdx, String status);

    Page<PostParticipant> findAllByPostUser_PostIdxAndStatus(Integer postIdx, String Status, Pageable pageable);

    @Modifying
    @Query(value = "update PostParticipant p set p.status = :status where p.postUser.postIdx =:postIdx and p.postUser.userIdx = :userIdx")
    void changePostParticipationStatus(@Param("status") String status, @Param("postIdx") Integer postIdx, @Param("userIdx") Integer userIdx);

    boolean existsByPostUserAndAndStatus(PostUser id, String status);

    Page<PostParticipant> findAllByPostUser_UserIdxAndStatus(Integer userIdx, String Status, Pageable pageable);


}
