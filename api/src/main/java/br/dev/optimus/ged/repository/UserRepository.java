package br.dev.optimus.ged.repository;

import java.time.Instant;
import java.util.List;

import br.dev.optimus.ged.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Long> {
    private final GroupRepository groupRepository;

    public UserRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new BadRequestException("passwords do not match");
        }
        if (password.length() < 6) {
            throw new BadRequestException("password must be at least 6 characters");
        }
    }

    private void validate(User.DTO dto, Long id) {
        var isUpdate = id != null;
        if (dto.username() == null || dto.username().isBlank()) {
            throw new BadRequestException("username is required");
        }
        if (dto.email() == null || dto.email().isBlank()) {
            throw new BadRequestException("email is required");
        }
        if (dto.password() == null || dto.password().isBlank() && !isUpdate) {
            throw new BadRequestException("password is required");
        }
        if (!isUpdate) {
            if (existsByUsername(dto.username())) {
                throw new BadRequestException("user username already exists");
            }
            if (existsByEmail(dto.email())) {
                throw new BadRequestException("user email already exists");
            }
            validatePassword(dto.password(), dto.passwordConfirm());
        } else {
            if (existsByUsernameAndIdNot(dto.username(), id)) {
                throw new BadRequestException("user username already exists");
            }
            if (existsByEmailAndIdNot(dto.email(), id)) {
                throw new BadRequestException("user email already exists");
            }
            if (dto.password() != null && !dto.password().isBlank()) {
                validatePassword(dto.password(), dto.passwordConfirm());
            }
        }

    }

    public List<User> listAll() {
        return find("visible = true and deletedAt = 0").list();
    }

    public User findById(Long id) {
        return find("id = ?1 and visible = true and deletedAt = 0", id)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    public boolean existsByUsername(String username) {
        return find("username = ?1", username).firstResultOptional().isPresent();
    }

    public boolean existsByEmail(String email) {
        return find("email = ?1", email).firstResultOptional().isPresent();
    }

    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return find("username = ?1 and id != ?2", username, id).firstResultOptional().isPresent();
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return find("email = ?1 and id != ?2", email, id).firstResultOptional().isPresent();
    }

    public void create(User data) {
        var now = Instant.now().toEpochMilli();
        data.setCreatedAt(now);
        data.setUpdatedAt(now);
        persist(data);
    }

    public User create(User.DTO dto) {
        validate(dto, null);
        var group = groupRepository.findById(dto.groupId());
        var data = User.builder()
                .group(group)
                .name(dto.name())
                .jobTitle(dto.jobTitle())
                .phone(dto.phone())
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .locked(dto.locked())
                .build();
        create(data);
        return data;
    }

    public void update(User data) {
        data.setUpdatedAt(Instant.now().toEpochMilli());
        persist(data);
    }

    public User update(Long id, User.DTO dto) {
        validate(dto, id);
        var data = findById(id);
        data.setName(dto.name());
        data.setJobTitle(dto.jobTitle());
        data.setPhone(dto.phone());
        data.setUsername(dto.username());
        data.setEmail(dto.email());
        if (dto.password() != null && !dto.password().isBlank()) {
            data.setPassword(dto.password());
        }
        data.setLocked(dto.locked());
        update(data);
        return data;
    }

    public void delete(Long id) {
        var user = findById(id);
        user.setDeletedAt(Instant.now().toEpochMilli());
        persist(user);
    }

    public User restore(Long id) {
        var data = findById(id);
        data.setDeletedAt(0);
        persist(data);
        return data;
    }
}
