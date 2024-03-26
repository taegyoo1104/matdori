package com.estsoft13.matdori.service;

import com.estsoft13.matdori.domain.Restaurant;
import com.estsoft13.matdori.domain.Review;
import com.estsoft13.matdori.dto.AddReviewRequestDto;
import com.estsoft13.matdori.dto.ReviewResponseDto;
import com.estsoft13.matdori.dto.UpdateReviewRequestDto;
import com.estsoft13.matdori.repository.RestaurantRepository;
import com.estsoft13.matdori.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public List<ReviewResponseDto> getReviews() {
        return reviewRepository.findAll()
                .stream()
                .map(ReviewResponseDto::new)
                .toList();
    }

    @Transactional
    public ReviewResponseDto createReview(AddReviewRequestDto requestDto, Long restaurantId) {
        // 식당 찾기 및 예외 처리
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식당이 존재하지 않습니다."));

        // 새 리뷰 객체 생성 및 저장
        Review review = new Review(requestDto);
        review.setRestaurant(restaurant);  // Review 엔티티에 식당 설정
        reviewRepository.save(review);
        return new ReviewResponseDto(review);
    }

    @Transactional
    public ReviewResponseDto getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(ReviewResponseDto::new)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 ID가 존재하지 않습니다."));
    }

    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    public ReviewResponseDto updateReview(Long reviewId,  UpdateReviewRequestDto requestDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 ID가 존재하지 않습니다."));

        review.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getRating());
        return new ReviewResponseDto(review);

    }
}
