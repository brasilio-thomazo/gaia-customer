package br.dev.optimus.ged.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "groups", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @JdbcTypeCode(value = SqlTypes.JSON)
    private Set<String> permissions;
    private boolean visible;
    private boolean editable;
    private boolean locked;
    @Column(name = "created_at")
    @JsonbProperty("created_at")
    private long createdAt;
    @Column(name = "updated_at")
    @JsonbProperty("updated_at")
    private long updatedAt;
    @Column(name = "deleted_at")
    @JsonbProperty("deleted_at")
    private long deletedAt;

    public static class Builder {
        private String name;
        private Set<String> permissions;
        private boolean visible;
        private boolean editable;
        private boolean locked;

        public Builder() {
            this.editable = true;
            this.visible = true;
            this.locked = false;
        }

        public Group build() {
            return new Group(this);
        }

        public Builder name(String name) {
            this.name = name.toLowerCase();
            return this;
        }

        public Builder permissions(Set<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder permissions(String... permissions) {
            this.permissions = Set.of(permissions);
            return this;
        }

        public Builder permissions(Collection<String> permissions) {
            this.permissions = Set.copyOf(permissions);
            return this;
        }

        public Builder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder locked(boolean locked) {
            this.locked = locked;
            return this;
        }

    }

    public record DTO(
            String name,
            List<String> permissions,
            boolean locked) {
    }

    public Group() {
    }

    public Group(Builder builder) {
        this.name = builder.name;
        this.permissions = builder.permissions;
        this.visible = builder.visible;
        this.editable = builder.editable;
        this.locked = builder.locked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public void setPermissions(String... permissions) {
        this.permissions = Set.of(permissions);
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(long deletedAt) {
        this.deletedAt = deletedAt;
    }

}
