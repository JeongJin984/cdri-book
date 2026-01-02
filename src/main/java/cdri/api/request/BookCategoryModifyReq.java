package cdri.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request for modifying book category")
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookCategoryModifyReq (
    @Schema(description = "IDs of the new category", example = "[1, 2]")
    @NotNull List<@Min(1) Long> categoryId
) {
}
