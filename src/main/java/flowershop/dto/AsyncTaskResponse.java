package flowershop.dto;

import flowershop.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AsyncTaskResponse {
    private long id;
    private TaskStatus status;
}
