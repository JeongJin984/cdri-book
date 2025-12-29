package cdri.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookCategoryModifyReq (
    @NotNull @Min(1) Long categoryId
) {
}
