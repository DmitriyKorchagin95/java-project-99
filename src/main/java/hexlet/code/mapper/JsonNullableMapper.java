package hexlet.code.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class JsonNullableMapper {

    /**
     * Checks whether nullable parameter was passed explicitly.
     *
     * @param <T>
     *            the type of the value contained in the JsonNullable
     * @param nullable
     *            the JsonNullable object to check
     * @return true if value was set explicitly and is present, false otherwise
     */
    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }

    /**
     * Unwraps the value from JsonNullable container.
     *
     * @param <T>
     *            the type of the value contained in the JsonNullable
     * @param jsonNullable
     *            the JsonNullable object to unwrap
     * @return the contained value if present, null otherwise
     */
    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    /**
     * Wraps a value into JsonNullable container.
     *
     * @param <T>
     *            the type of the value to wrap
     * @param entity
     *            the value to wrap in JsonNullable
     * @return JsonNullable containing the specified value
     */
    public <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }
}
