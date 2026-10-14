package com.fu.cinema.service.impl;

import com.fu.cinema.dto.MovieForm;
import com.fu.cinema.entity.Movie;
import com.fu.cinema.enums.MovieStatus;
import com.fu.cinema.exception.ResourceNotFoundException;
import com.fu.cinema.repository.MovieRepository;
import com.fu.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Page<Movie> searchForAdmin(String keyword, Long genreId, MovieStatus status, int page, int size, String sort) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<Movie> searchForCustomer(String keyword, Long genreId, int page, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Movie getById(Long movieId) {
        return movieRepository.findById(movieId).orElseThrow(() -> new ResourceNotFoundException("Phim", movieId));
    }

    @Override
    public MovieForm getForm(Long movieId) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public void create(MovieForm form) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public void update(Long movieId, MovieForm form) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public boolean delete(Long movieId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Movie> findSchedulable() {
        return movieRepository.findByStatusNotOrderByTitleAsc(MovieStatus.STOPPED);
    }

    @Override
    public long countShowing() {
        return movieRepository.countByStatus(MovieStatus.SHOWING);
    }
}
