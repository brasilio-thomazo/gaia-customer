package br.dev.optimus.ged.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.dev.optimus.ged.model.Group;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class GroupRepository implements PanacheRepositoryBase<Group, Integer> {
    private void validate(Group.DTO dto, Integer id) {
        if (dto.name() == null || dto.name().isBlank()) {
            throw new BadRequestException("name is required");
        }
        if (dto.permissions() == null || dto.permissions().isEmpty()) {
            throw new BadRequestException("permissions is required");
        }
        if (id == null) {
            if (existsByName(dto.name())) {
                throw new BadRequestException("group name already exists");
            }
        } else {
            if (existsByNameAndIdNot(dto.name(), id)) {
                throw new BadRequestException("group name already exists");
            }
        }
    }

    private Set<String> cleanPermissions(Collection<String> permissions) {
        var regex = "^(?!(root|admin|nobody)$)user:(list|create|update|delete)$";
        return permissions.stream()
                .filter(permission -> !permission.matches(regex))
                .collect(Collectors.toSet());
    }

    public List<Group> listAll() {
        return find("visible = true and deletedAt = 0").list();
    }

    public Group findById(Integer id) {
        return find("id = ?1 and visible = true and deletedAt = 0", id)
                .firstResultOptional()
                .orElseThrow(() -> new NotFoundException("group not found"));
    }

    public boolean existsByName(String name) {
        return find("name = ?1", name).firstResultOptional().isPresent();
    }

    boolean existsByNameAndIdNot(String name, Integer id) {
        return find("name = ?1 and id != ?2", name, id).firstResultOptional().isPresent();
    }

    public void create(Group group) {
        var now = Instant.now().toEpochMilli();
        group.setCreatedAt(now);
        group.setUpdatedAt(now);
        persist(group);
    }

    public Group create(Group.DTO dto) {
        validate(dto, null);
        var data = Group.builder()
                .name(dto.name())
                .permissions(cleanPermissions(dto.permissions()))
                .locked(dto.locked())
                .build();
        create(data);
        return data;
    }

    public void update(Group group) {
        group.setUpdatedAt(Instant.now().toEpochMilli());
        persist(group);
    }

    public Group update(Integer id, Group.DTO dto) {
        validate(dto, id);
        var data = findById(id);
        data.setName(dto.name());
        data.setPermissions(cleanPermissions(dto.permissions()));
        data.setLocked(dto.locked());
        update(data);
        return data;
    }

    public void delete(Integer id) {
        Group group = findById(id);
        group.setDeletedAt(Instant.now().toEpochMilli());
        persist(group);
    }

    public Group restore(Integer id) {
        Group group = findById(id);
        group.setDeletedAt(0);
        persist(group);
        return group;
    }
}
