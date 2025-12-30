package cdri.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request for modifying book category")
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookCategoryModifyReq (
    @Schema(description = "ID of the new category", example = "1")
    @NotNull @Min(1) Long categoryId
) {
}
