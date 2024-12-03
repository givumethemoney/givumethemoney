package com.hey.givumethemoney.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hey.givumethemoney.domain.ThumbNail;
import com.hey.givumethemoney.repository.ThumbNailRepository;

@Service
public class ThumbNailService {
    private final ThumbNailRepository thumbNailRepository;

    @Autowired
    public ThumbNailService(ThumbNailRepository thumbNailRepository) {
        this.thumbNailRepository = thumbNailRepository;
    }

    public List<ThumbNail> findByDonationId(Long id) {
        return thumbNailRepository.findByDonationId(id);
    }

}
