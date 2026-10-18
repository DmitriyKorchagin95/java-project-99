package hexlet.code.repository;

import edu.umd.cs.findbugs.annotations.NonNull;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    boolean existsByAssignee(User assignee);

    boolean existsByTaskStatus(TaskStatus taskStatus);

    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    Page<Task> findAll(Specification<Task> spec, @NonNull Pageable pageable);
}
