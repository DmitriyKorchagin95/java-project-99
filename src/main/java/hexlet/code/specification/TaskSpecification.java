package hexlet.code.specification;

import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        return withTitleCont(params.getTitleCont())
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> titleCont == null || titleCont.isBlank()
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> assigneeId == null
                ? cb.conjunction()
                : cb.equal(root.join("assignee", JoinType.LEFT).get("id"), assigneeId);
    }

    private Specification<Task> withStatus(String statusSlug) {
        return (root, query, cb) -> statusSlug == null || statusSlug.isBlank()
                ? cb.conjunction()
                : cb.equal(root.join("taskStatus", JoinType.LEFT).get("slug"), statusSlug);
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Task> subRoot = subquery.from(Task.class);
            Join<Task, Label> labelJoin = subRoot.join("labels");

            subquery.select(subRoot.get("id")).where(
                    cb.equal(subRoot.get("id"), root.get("id")),
                    cb.equal(labelJoin.get("id"), labelId)
            );

            return cb.exists(subquery);
        };
    }
}
