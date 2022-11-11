package com.bob_senior.bob_server.service;

import com.bob_senior.bob_server.domain.base.BaseException;
import com.bob_senior.bob_server.domain.base.BaseResponseStatus;
import com.bob_senior.bob_server.domain.user.*;
import com.bob_senior.bob_server.domain.user.entity.BlockId;
import com.bob_senior.bob_server.domain.user.entity.FriendId;
import com.bob_senior.bob_server.domain.user.entity.Friendship;
import com.bob_senior.bob_server.domain.user.entity.User;
import com.bob_senior.bob_server.repository.BlockRepository;
import com.bob_senior.bob_server.repository.FriendshipRepository;
import com.bob_senior.bob_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final FriendshipRepository friendshipRepository;

    @Autowired
    UserService(UserRepository userRepository, BlockRepository blockRepository, FriendshipRepository friendshipRepository){
        this.userRepository = userRepository;
        this.blockRepository = blockRepository;

        this.friendshipRepository = friendshipRepository;
    }

    public boolean checkUserExist(Integer userIdx){
        return userRepository.existsUserByUserIdx(userIdx);
    }

    public String getNickNameByIdx(Integer userIdx) {
        User user = userRepository.findUserByUserIdx(userIdx);
        return user.getNickName();
    }

    //해당 유저에게 친구요청 해보기
    public void makeNewFriendshipRequest(Integer requesterIdx, String targetUUID) throws BaseException {
        User user = userRepository.findByUuid(targetUUID);
        //1. 이미 친구 추가 된 경우 확인
        if(friendshipRepository.existsByIdAndAndStatus(new FriendId(requesterIdx,user.getUserIdx()),"ACTIVE")){
            throw new BaseException(BaseResponseStatus.ALREADY_HAS_FRIENDSHIP);
        }

        //2. 차단 여부 확인하기
        boolean hasBlocked = blockRepository.existsById(new BlockId(user.getUserIdx(),requesterIdx));
        if(hasBlocked){
            throw new BaseException(BaseResponseStatus.CAN_NOT_REQUEST_FRIENDSHIP);
        }

        //3. 이미 친구 요청이 보내졌는지 확인.. 할필요 없이 그냥 덮어버리기?
        friendshipRepository.save(
                Friendship.builder()
                        .id(new FriendId(requesterIdx,user.getUserIdx()))
                        .status("WAITING")
                        .build()
        );
    }

    public List<SimplifiedUserProfileDTO> getRequestedFriendShipWaiting(Integer userIdx, Pageable pageable) throws BaseException{
        //해당 유저에게 들어온 친구추가 요청을 확인하기..어찌넘길까
        List<Friendship> list = friendshipRepository.findAllByUserIdxInWaiting(userIdx,pageable).getContent();
        List<SimplifiedUserProfileDTO> data = new ArrayList<>();
        for (Friendship friendship : list) {
            Integer other = friendship.getId().getMinUserIdx() == userIdx?friendship.getId().getMaxUserIdx() : friendship.getId().getMinUserIdx();
            User user = userRepository.findUserByUserIdx(other);
            data.add(
                    SimplifiedUserProfileDTO.builder()
                            .nickname(user.getNickName())
                            .department(user.getDepartment())
                            .schoolId(user.getSchoolId())
                            .school(user.getSchool())
                            .build()
            );
        }
        return data;
    }

    public void determineFriendRequest(Integer userIdx, Integer targetIdx, boolean accept) throws BaseException{
        //해당 유저의 request를 처리하기
        FriendId friendId = new FriendId(userIdx,targetIdx);
        boolean already = friendshipRepository.existsByIdAndAndStatus(friendId,"ACTIVE");
        if(already){
            throw new BaseException(BaseResponseStatus.ALREADY_HAS_FRIENDSHIP);
        }
        if(!friendshipRepository.existsById(friendId)){
            //애초에 요청이 없던 케이스
            throw new BaseException(BaseResponseStatus.INVALID_USER_TO_ACCEPT);
        }
        //아닐시 friendship을 active로 업데이트
        if(accept) {
            //승락시 active로 업데이트
            friendshipRepository.updateFriendShipACTIVE(friendId);
        }
        else{
            friendshipRepository.delete(new Friendship(friendId,"WAITING"));
        }
    }
}
