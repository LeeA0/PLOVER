package com.plover.service;

import com.plover.model.metoring.Mentoring;
import com.plover.model.metoring.request.MentoringRequest;
import com.plover.model.user.UserDto;
import com.plover.repository.MentoringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Cacheable;
import javax.transaction.Transactional;

@Service
public class MentoringService {

    private MentoringRepository mentoringRepository;

    public MentoringService(MentoringRepository mentoringRepository){
        this.mentoringRepository = mentoringRepository;
    }

    @Transactional
    public Long save(UserDto user, MentoringRequest mentoringRequest){
        Mentoring mentoring = mentoringRequest.toMentoring();
        mentoring.setUser(user);
        mentoringRepository.save(mentoring);
        return mentoring.getNo();
    }

    @Transactional
    public MentoringRequest getPost(Long id) {
        Mentoring mentoring = mentoringRepository.findById(id).get();

        MentoringRequest mentoringRequest = MentoringRequest.builder()
                .title(mentoring.getTitle())
                .content(mentoring.getContent())
                .fileId(mentoring.getFileId())
                .createdDate(mentoring.getCreatedDate())
                .build();
        return mentoringRequest;
    }
}