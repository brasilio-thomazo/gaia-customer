package br.dev.optimus.ged;

import br.dev.optimus.ged.model.Group;
import br.dev.optimus.ged.model.User;
import br.dev.optimus.ged.repository.GroupRepository;
import br.dev.optimus.ged.repository.UserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class StartUp {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public StartUp(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void init(@Observes StartupEvent event) {
        if (groupRepository.count() == 0) {
            var root = Group.builder()
                    .name("root")
                    .permissions("root")
                    .locked(true)
                    .visible(false)
                    .editable(false)
                    .build();

            var admin = Group.builder()
                    .name("admin")
                    .permissions("admin")
                    .locked(true)
                    .visible(true)
                    .editable(false)
                    .build();

            var nobody = Group.builder()
                    .name("nobody")
                    .permissions("nobody")
                    .locked(true)
                    .visible(false)
                    .editable(false)
                    .build();

            groupRepository.create(root);
            groupRepository.create(admin);
            groupRepository.create(nobody);

            if (userRepository.count() == 0) {
                userRepository.create(User.builder()
                        .group(root)
                        .name("root")
                        .email("root@change.me")
                        .username("root")
                        .password("root")
                        .visible(false)
                        .editable(false)
                        .locked(true)
                        .build());

                userRepository.create(User.builder()
                        .group(admin)
                        .name("admin")
                        .email("admin@change.me")
                        .username("admin")
                        .password("admin")
                        .visible(true)
                        .editable(false)
                        .locked(true)
                        .build());
            }
        }
    }
}
