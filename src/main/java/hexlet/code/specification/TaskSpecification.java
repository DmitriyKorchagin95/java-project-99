package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        Specification<Task> specification = (root, query, cb) -> cb.conjunction();

        if (params.getTitleCont() != null && !params.getTitleCont().isBlank()) {
            specification = specification.and(
                    withTitle(params.getTitleCont())
            );
        }

        if (params.getAssigneeId() != null) {
            specification = specification.and(
                    withAssigneeId(params.getAssigneeId())
            );
        }

        if (params.getStatus() != null && !params.getStatus().isBlank()) {
            specification = specification.and(
                    withStatus(params.getStatus())
            );
        }

        if (params.getLabelId() != null) {
            specification = specification.and(
                    withLabelId(params.getLabelId())
            );
        }

        return specification;
    }

    private Specification<Task> withTitle(String titleCont) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + titleCont.toLowerCase() + "%"
                );
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("assignee").get("id"),
                        assigneeId
                );
    }

    private Specification<Task> withStatus(String status) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("taskStatus").get("slug"),
                        status
                );
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            query.distinct(true);

            return cb.equal(
                    root.join("labels").get("id"),
                    labelId
            );
        };
    }
}
