package ro.sevenartstravel.service.entity.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.sevenartstravel.dto.UserArtObjectActionDTO;
import ro.sevenartstravel.entity.ArtObject;
import ro.sevenartstravel.entity.User;
import ro.sevenartstravel.entity.UserArtObjectAction;
import ro.sevenartstravel.enums.ActionType;
import ro.sevenartstravel.exception.DuplicateActionException;
import ro.sevenartstravel.exception.NotFoundException;
import ro.sevenartstravel.mapper.UserArtObjectActionMapper;
import ro.sevenartstravel.repository.ArtObjectRepository;
import ro.sevenartstravel.repository.UserArtObjectActionRepository;
import ro.sevenartstravel.repository.UserRepository;
import ro.sevenartstravel.service.entity.UserArtObjectActionService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserArtObjectActionServiceImpl implements UserArtObjectActionService {

    private final UserArtObjectActionRepository userArtObjectActionRepository;
    private final UserRepository userRepository;
    private final ArtObjectRepository artObjectRepository;
    private final UserArtObjectActionMapper mapper;

    public UserArtObjectActionServiceImpl(UserArtObjectActionRepository userArtObjectActionRepository, UserRepository userRepository, ArtObjectRepository artObjectRepository, UserArtObjectActionMapper mapper) {
        this.userArtObjectActionRepository = userArtObjectActionRepository;
        this.userRepository = userRepository;
        this.artObjectRepository = artObjectRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void addAction(UUID userId, UUID artObjectId, ActionType actionType) {
        if (userArtObjectActionRepository.existsByUserIdAndArtObjectIdAndActionType(userId, artObjectId, actionType)) {
            throw new DuplicateActionException("Action '" + actionType + "' already exists for user " + userId + " and art object " + artObjectId);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        ArtObject artObject = artObjectRepository.findById(artObjectId)
                .orElseThrow(() -> new NotFoundException("ArtObject", artObjectId));

        UserArtObjectAction action = UserArtObjectAction.builder()
                .user(user)
                .artObject(artObject)
                .actionType(actionType)
                .build();

        userArtObjectActionRepository.save(action);
        log.info("Added {} action for user {} on artObject {}", actionType, userId, artObjectId);
    }


    @Override
    @Transactional
    public void removeAction(UUID userId, UUID artObjectId, ActionType actionType) {
        Optional<UserArtObjectAction> existing = userArtObjectActionRepository.findByUserIdAndArtObjectIdAndActionType(userId, artObjectId, actionType);

        existing.ifPresentOrElse(action -> {
                    userArtObjectActionRepository.delete(action);
                    log.info("Removed {} action for user {} on artObject {}", actionType, userId, artObjectId);
                },
                () -> log.warn("No {} action found for user {} on artObject {}", actionType, userId, artObjectId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAction(UUID userId, UUID artObjectId, ActionType actionType) {
        log.debug("Checking existence of {} for user {} and artObject {}", actionType, userId, artObjectId);
        return userArtObjectActionRepository.existsByUserIdAndArtObjectIdAndActionType(userId, artObjectId, actionType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserArtObjectActionDTO> getUserActionOnArtObject(UUID userId, UUID artObjectId, ActionType actionType) {
        log.debug("Fetching {} action for user {} on artObject {}", actionType, userId, artObjectId);
        return userArtObjectActionRepository.findByUserIdAndArtObjectIdAndActionType(userId, artObjectId, actionType)
                .map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserArtObjectActionDTO> getActionsByUserAndType(UUID userId, ActionType actionType, Pageable pageable) {
        log.debug("Fetching all {} actions for user {} with pagination {}", actionType, userId, pageable);
        return userArtObjectActionRepository.findByUserIdAndActionType(userId, actionType, pageable)
                .map(mapper::toDTO);
    }
}